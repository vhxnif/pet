package com.vhxnif.pet.service

import com.vhxnif.pet.config.ChatCustomConfig
import org.springframework.ai.chat.StreamingChatClient
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-27
 */
@Component
class CommonChat(
    private val chatClient: StreamingChatClient,
    private val chatCustomConfig: ChatCustomConfig,
) {

    fun say(text: String, coder: Boolean = false): Flux<String> {
        val messages = mutableListOf<Message>()
        chatCustomConfig.systemMessage()?.let {
            messages.add(it)
        }
        messages.add(UserMessage(text))
        return if (coder && chatCustomConfig.coderModel() != null) {
            val options = OpenAiChatOptions.Builder().withModel(chatCustomConfig.coderModel()).build()
            chatClient.stream(Prompt(messages, options))
        } else {
            chatClient.stream(Prompt(messages))
        }.map { it.result.output.content }
    }

    fun systemPrompt(): String? {
        return chatCustomConfig.systemMessage()?.content
    }

    fun resetSystemPrompt(systemPromptFilePath: String) {
        chatCustomConfig.setSystemPrompt(FileSystemResource(systemPromptFilePath))
    }


}