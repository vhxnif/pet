package com.vhxnif.pet.service

import org.springframework.ai.chat.StreamingChatClient
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
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
        return chatClient.stream(Prompt(
            listOf(
                SystemMessage(systemPrompt),
                PromptTemplate(userPrompt).createMessage(mutableMapOf<String, Any>("text" to text))
            )
        )).map { it.result.output.content }
    }

}