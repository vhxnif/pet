package com.vhxnif.pet

import com.vhxnif.pet.config.ChatCustomConfig
import org.mockito.kotlin.*
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.ChatResponse
import org.springframework.ai.chat.Generation
import org.springframework.ai.chat.StreamingChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import reactor.core.publisher.Flux

/**
 *
 * @author xiaochen.zhang
 * @since 2024-06-07
 */
open class BaseTest {

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

    val text = "kotlin"

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