package com.vhxnif.pet

import com.vhxnif.pet.config.ChatCustomConfig
import com.vhxnif.pet.core.AiChatClient
import com.vhxnif.pet.core.StreamingAiChatClient
import org.mockito.kotlin.*
import org.springframework.ai.chat.ChatClient
import com.vhxnif.pet.core.store.ChatMessage
import com.vhxnif.pet.core.store.FileMessageStore
import org.springframework.ai.chat.ChatResponse
import org.springframework.ai.chat.Generation
import org.springframework.ai.chat.StreamingChatClient
import org.springframework.ai.chat.messages.MessageType
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import reactor.core.publisher.Flux

/**
 *
 * @author vhxnif
 * @since 2024-06-07
 */
open class BaseTest {
    val text = "kotlin"
    val context = "let's talk about kotlin"

    val contextChatMessage = mock<ChatMessage> {
        on { content } doReturn context
        on { type } doReturn MessageType.USER.value
    }

    val messageStore = mock<FileMessageStore> {
        on { contextMessage() } doReturn listOf(contextChatMessage)
    }
    val streamingChatClient = mock<StreamingChatClient> {
        on { stream(any<Prompt>()) } doReturn Flux.just(ChatResponse(listOf(Generation("success"))))
    }

    val chatClient = mock<ChatClient> {
        on { call(any<Prompt>()) } doReturn ChatResponse(listOf(Generation("success")))
    }

    val chatCustomConfig = mock<ChatCustomConfig> {
        on { systemMessage() } doReturn null
        on { coderModel() } doReturn null
    }


    val streamingAiChatClient = StreamingAiChatClient(streamingChatClient, messageStore)

    val aiChatClient = AiChatClient(chatClient, messageStore)

    fun prompt(content: String) : Resource {
        return ByteArrayResource(content.toByteArray())
    }

    fun vryStreamingPrompt(f: () -> Prompt) {
        verify(streamingChatClient).stream(eq(f()))
    }

    fun vryPrompt(f: () -> Prompt) {
        verify(chatClient).call(eq(f()))
    }
}