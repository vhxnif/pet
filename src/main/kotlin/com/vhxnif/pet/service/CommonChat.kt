package com.vhxnif.pet.service

import com.vhxnif.pet.config.ChatCustomConfig
import com.vhxnif.pet.core.StreamingAiChatClient
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author vhxnif
 * @since 2024-05-27
 */
@Component
class CommonChat(
    private val chatClient: StreamingAiChatClient,
    private val chatCustomConfig: ChatCustomConfig,
    @Value("classpath:/prompts/coder/system.st")
    private val systemPrompt: Resource
) {

    fun say(text: String, coder: Boolean = false): Flux<String> {
        return chatClient.contextCall {
            prompt {
                option {
                    chatCustomConfig.coderModel()?.let {
                        if (coder) OpenAiChatOptions.Builder().withModel(it).build() else null
                    }
                }
                if (coder) system(systemPrompt) else chatCustomConfig.systemMessage()?.let { system(it) }
                user(text)
            }
        }
    }

    fun systemPrompt(): String? {
        return chatCustomConfig.systemMessage()?.content
    }

}