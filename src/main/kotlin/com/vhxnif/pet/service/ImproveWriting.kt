package com.vhxnif.pet.service

import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatClient
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
    private val chatClient: OpenAiChatClient,
) {

    fun improve(input: String) : Flux<String> {
        return chatClient.stream(Prompt(
            listOf(
                SystemMessage(systemPrompt),
                UserMessage(input),
            )
        )).map { it.result.output.content }
    }

}