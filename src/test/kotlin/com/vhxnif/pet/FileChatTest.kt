package com.vhxnif.pet

import com.vhxnif.pet.service.FileChat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate


class FileChatTest : BaseTest() {

    // --- file chat test ---
    @Test
    fun test_file_chat() {
        val doc = "this is a doc content"
        val userPrompt = prompt( "this is input: {text}, this is doc: {doc}")
        val fileChat = spy(FileChat(streamingChatClient, userPrompt)).apply {
            doReturn(doc).whenever(this).doc(any())
        }
        fileChat.chat(text, "/test.pdf")
        val userMessage =  PromptTemplate(userPrompt).createMessage(
            mutableMapOf<String, Any>(
                "text" to text,
                "doc" to doc
            )
        )
        vryStreamingPrompt {
            Prompt(listOf(userMessage))
        }
    }
}
