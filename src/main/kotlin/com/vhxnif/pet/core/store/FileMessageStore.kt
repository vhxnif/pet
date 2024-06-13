package com.vhxnif.pet.core.store

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.readText
import kotlin.io.use
import kotlin.text.isEmpty
import kotlin.text.startsWith

/**
 *
 * @author chen
 * @since 2024-06-12
 */
@Component
class FileMessageStore(
    private val objectMapper: ObjectMapper,
    private val contextCount: Int = 10
) : IMessageStore {

    lateinit var contextPath: String

    init {
        val os = System.getProperty("os.name")
        val configPath = if (os.startsWith("Windows")) {
            System.getenv("APPDATA")
        } else {
            System.getenv("HOME") + File.separator + ".config"
        }
        contextPath = configPath + File.separator + "pet" + File.separator + "message_context"
        Paths.get(contextPath).apply {
            when {
                parent == null -> error("path not exists. $this")
                Files.notExists(parent) -> Files.createDirectories(parent)
                Files.notExists(this) -> Files.createFile(this)
            }
        }
    }

    override fun contextMessage(): Flux<ChatMessage> {
        return with(contextMessageJsonStr()) {
            if (isEmpty()) {
                Flux.empty()
            } else {
                val type = object : TypeReference<List<ChatMessage>>() {}
                objectMapper.readValue(this, type).toFlux()
            }
        }
    }

    override fun saveMessage(chatMessage: ChatMessage) {
        contextMessage().concatWith(Flux.just(chatMessage)).collectList().block()?.let {
            val list = if (it.size <= contextCount) it else it.toFlux().skip((it.size - contextCount).toLong()).collectList().block()
            val str = objectMapper.writeValueAsString(list)
            Files.newBufferedWriter(Paths.get(contextPath), StandardCharsets.UTF_8)
                .use { writer -> writer.write(str) }
        }
    }

    private fun contextMessageJsonStr(): String =
        Files.newBufferedReader(Paths.get(contextPath), StandardCharsets.UTF_8).use { reader -> reader.readText() }

}