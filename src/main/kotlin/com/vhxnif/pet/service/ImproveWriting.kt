package com.vhxnif.pet.service

import com.vhxnif.pet.core.StreamingAiChatClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author chen
 * @since 2024-05-31
 */
@Component
class ImproveWriting (
    @Value("classpath:/prompts/improveWriting/system.st")
    private val systemPrompt: Resource,
    @Value("classpath:/prompts/improveWriting/user.st")
    private val userPrompt: Resource,
    private val chatClient: StreamingAiChatClient,
) {

    fun improve(text: String) : Flux<String> {
        return chatClient.call {
            prompt {
                system(systemPrompt)
                user(userPrompt, "text" to text)
            }
        }
    }

}