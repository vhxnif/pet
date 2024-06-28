package com.vhxnif.pet.core.store

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.vhxnif.pet.util.petConfigDir
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

/**
 *
 * @author vhxnif
 * @since 2024-06-12
 */
class FileMessageStore(
    private val objectMapper: ObjectMapper,
    private val contextCount: Int = 10
) : IMessageStore {


    var contextPath: String = petConfigDir() + File.separator + "message_context"

    override fun contextMessage(): List<ChatMessage> {
        return with(contextMessageJsonStr()) {
            if (isEmpty()) {
                listOf()
            } else {
                val type = object : TypeReference<List<ChatMessage>>() {}
                objectMapper.readValue(this, type)
            }
        }
    }

    override fun saveMessage(messages: Pair<ChatMessage, ChatMessage>) {
        val (first, second) = messages
        contextMessage().toFlux().concatWith(Flux.just(first, second)).collectList().block()?.let {
            val str = objectMapper.writeValueAsString(it.take(contextCount))
            Files.newBufferedWriter(Paths.get(contextPath), StandardCharsets.UTF_8)
                .use { writer -> writer.write(str) }
        }
    }

    private fun contextMessageJsonStr(): String =
        Files.newBufferedReader(Paths.get(contextPath), StandardCharsets.UTF_8).use { reader -> reader.readText() }

}