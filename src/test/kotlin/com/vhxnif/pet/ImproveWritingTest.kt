package com.vhxnif.pet

import com.vhxnif.pet.service.ImproveWriting
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate

/**
 *
 * @author xiaochen.zhang
 * @since 2024-06-07
 */
class ImproveWritingTest : BaseTest(){



    @Test
    fun test_improve_writing() {
        val systemPrompt = "this is system prompt"
        val userPrompt = "this is content: {text}"
        ImproveWriting(prompt(systemPrompt), prompt(userPrompt), streamingChatClient).improve(text)
        vryStreamingPrompt {
            Prompt(listOf(
                SystemMessage(systemPrompt),
                PromptTemplate(prompt(userPrompt)).createMessage(
                    mutableMapOf<String, Any>(
                        "text" to text
                    )
                )
            ))
        }
    }


}