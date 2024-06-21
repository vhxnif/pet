package com.vhxnif.pet.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.vhxnif.pet.util.osConfig
import com.vhxnif.pet.util.petConfigDir
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 * @author chen
 * @since 2024-05-24
 */
@Configuration
@EnableConfigurationProperties(value = [CustomProperties::class])
class AppConfig(
    val objectMapper: ObjectMapper
) {


    @PostConstruct
    fun appConfig() {
        objectMapper.registerKotlinModule()
        val osConfig = osConfig()
        val path = Paths.get(osConfig)
        if (!Files.exists(path)) {
            error("os config dir is missing. $osConfig")
        }
        val petConfigPath = Paths.get(petConfigDir())
        if (!Files.exists(petConfigPath)) {
            Files.createDirectory(petConfigPath)
        }
    }

    @Bean
    fun chatCustomConfig(customProperties: CustomProperties) : ChatCustomConfig {
        return ChatCustomConfig().apply {
            customProperties.chat?.let {
                setSystemPrompt(ClassPathResource("/prompts/chat/system.st"))
                it.systemPromptPath?.let {
                    s -> if("\${SYSTEM_PROMPT_PATH}" != s) {
                        setSystemPrompt(FileSystemResource(s))
                    }
                }
                it.coderModel?.let { c -> if("\${CODER_MODEL}" != c) { setCoderModel(c) } }
            }
        }
    }
}