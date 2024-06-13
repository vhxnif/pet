package com.vhxnif.pet.service

import com.vhxnif.pet.config.ChatCustomConfig
import com.vhxnif.pet.util.prompt
import com.vhxnif.pet.util.call
import com.vhxnif.pet.util.matchRun
import com.vhxnif.pet.util.userMessage
import org.springframework.ai.chat.StreamingChatClient
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author chen
 * @since 2024-05-27
 */
@Component
class CommonChat(
    private val chatClient: StreamingChatClient,
    private val chatCustomConfig: ChatCustomConfig,
    @Value("classpath:/prompts/coder/system.st")
    private val systemPrompt: Resource
) {

    fun say(text: String, coder: Boolean = false): Flux<String> {
        return chatClient.call {
            prompt {
                options {
                    (coder && chatCustomConfig.coderModel() != null).matchRun {
                        OpenAiChatOptions.Builder().withModel(chatCustomConfig.coderModel()).build()
                    }
                }
                messages(
                    if(coder) SystemMessage(systemPrompt) else chatCustomConfig.systemMessage(),
                    userMessage(text)
                )
            }
        }
    }

    fun systemPrompt(): String? {
        return chatCustomConfig.systemMessage()?.content
    }

}