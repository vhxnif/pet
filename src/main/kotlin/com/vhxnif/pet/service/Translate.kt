package com.vhxnif.pet.service

import com.vhxnif.pet.core.StreamingAiChatClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author vhxnif
 * @since 2024-05-24
 */
@Component
class Translate(
    @Value("classpath:/prompts/translate/system.st")
    private val systemPrompt: Resource,
    @Value("classpath:/prompts/translate/user.st")
    private val userPrompt: Resource,
    private val client: StreamingAiChatClient
) {

    fun translate(lang: String, text: String): Flux<String> {
        return client.call {
            prompt {
                system(systemPrompt)
                user(userPrompt, "to" to lang, "text" to text)
            }
        }
    }
}