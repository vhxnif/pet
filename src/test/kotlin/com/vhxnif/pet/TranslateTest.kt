package com.vhxnif.pet

import com.vhxnif.pet.service.Translate
import org.junit.jupiter.api.Test
import org.mockito.Mockito.spy
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate

/**
 *
 * @author xiaochen.zhang
 * @since 2024-06-07
 */
class TranslateTest : BaseTest() {

    private val systemPrompt = prompt("this is system prompt")
    private val userPrompt = prompt("this is user prompt: {to} {text}")
    private val lang = "en"

    @Test
    fun test_translate() {

        val ts = spy(Translate(systemPrompt, userPrompt, streamingChatClient))
        ts.translate(lang, text)
        vryStreamingPrompt {
            Prompt(
                listOf(
                    SystemMessage(systemPrompt),
                    PromptTemplate(userPrompt).createMessage(mutableMapOf<String, Any>(
                        "to" to lang,
                        "text" to text
                    ))
                )
            )
        }
    }

}