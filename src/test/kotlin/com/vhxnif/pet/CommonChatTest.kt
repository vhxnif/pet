package com.vhxnif.pet

import com.vhxnif.pet.service.CommonChat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatOptions

/**
 *
 * @author xiaochen.zhang
 * @since 2024-06-07
 */
class CommonChatTest : BaseTest() {

    // --- common chat test ---
    private fun commonChat(f: (chat: CommonChat) -> Unit) {
        val chat = CommonChat(streamingChatClient, chatCustomConfig)
        f(chat)
    }

    @Test
    fun test_common_chat_without_custom_config() = commonChat {
        it.say(text)
        vryStreamingPrompt { Prompt(text) }
    }

    @Test
    fun test_coding_common_chat_without_custom_config() = commonChat {
        it.say(text, true)
        vryStreamingPrompt { Prompt(text) }
    }

    @Test
    fun test_coding_common_chat_with_custom_config() = commonChat {
        val systemMessage = SystemMessage("system prompt")
        val coderModel = "coder_model"
        whenever(chatCustomConfig.systemMessage()).thenReturn(systemMessage)
        whenever(chatCustomConfig.coderModel()).thenReturn(coderModel)
        it.say(text, true)
        vryStreamingPrompt {
            Prompt(
                listOf(
                    systemMessage,
                    UserMessage(text),
                ),
                OpenAiChatOptions.Builder().withModel(coderModel).build()
            )
        }
    }

    @Test
    fun test_custom_config_system_prompt() = commonChat {
        val systemMessage = SystemMessage("system prompt")
        whenever(chatCustomConfig.systemMessage()).thenReturn(systemMessage)
        val res = it.systemPrompt()
        assert(res == systemMessage.content)
    }


}