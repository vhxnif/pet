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
 * @since 2024-05-31
 */
@Component
class ImproveWriting (
    @Value("classpath:/prompts/improveWriting/system.st")
    private val systemPrompt: Resource,
    @Value("classpath:/prompts/improveWriting/user.st")
    private val userPrompt: Resource,
    private val chatClient: StreamingChatClient,
) {

    fun improve(text: String) : Flux<String> {
        return chatClient.call {
            messages (
                systemMessage(systemPrompt),
                userMessage(userPrompt) {
                    mutableMapOf("text" to text)
                }
            )
        }
    }

}