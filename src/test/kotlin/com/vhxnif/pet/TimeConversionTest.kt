package com.vhxnif.pet

import com.vhxnif.pet.service.TimeConversion
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.springframework.ai.chat.ChatResponse
import org.springframework.ai.chat.Generation
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate

/**
 *
 * @author vhxnif
 * @since 2024-06-07
 */
class TimeConversionTest : BaseTest() {
    private val dateTime = "2024-05-12 12:30:20"
    private val timestamp = 1715484620L
    private val region = "东京"
    private val regionTimezone = "Asia/Tokyo"

    private fun timeConversion(f: TimeConversion.() -> Unit) {
        doReturn(ChatResponse(listOf(Generation(regionTimezone)))).whenever(chatClient).call(any<Prompt>())
        val systemPrompt = prompt("this is system prompt")
        val userPrompt = prompt("this is user prompt : {region}")
        TimeConversion(aiChatClient, systemPrompt, userPrompt).apply {
            f()
        }
        vryPrompt {
            Prompt(
                listOf(
                    SystemMessage(systemPrompt),
                    PromptTemplate(userPrompt).createMessage(mutableMapOf<String, Any>("region" to region))
                )
            )
        }
    }

    @Test
    fun convert_date_time() = timeConversion {
        convertDateTime(timestamp, region)
    }

    @Test
    fun convert_timestamp() = timeConversion {
        convertTimestamp(dateTime, region)
    }

}