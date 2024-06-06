package com.vhxnif.pet.service

import com.vhxnif.pet.util.messages
import com.vhxnif.pet.util.call
import com.vhxnif.pet.util.systemMessage
import com.vhxnif.pet.util.userMessage
import org.springframework.ai.chat.StreamingChatClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-24
 */
@Component
class Translate(
    @Value("classpath:/prompts/translate/system.st")
    private val systemPrompt: Resource,
    @Value("classpath:/prompts/translate/user.st")
    private val userPrompt: Resource,
    private val client: StreamingChatClient
) {

    fun translate(lang: String, text: String): Flux<String> {
        return client.call {
            messages(
                systemMessage(systemPrompt),
                userMessage(userPrompt) {
                    mutableMapOf("to" to lang, "text" to text)
                }
            )
        }
    }
}