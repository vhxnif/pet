package com.vhxnif.pet.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource

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
    fun objectMapperConfig() {
        objectMapper.registerKotlinModule()
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