package com.vhxnif.pet.service

import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.openai.OpenAiChatClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-24
 */
@Component
class Translate(
    @Value("classpath:/prompts/translate/system.st")
    private val systemPrompt: Resource,
    @Value("classpath:/prompts/translate/user.st")
    private val userPrompt: Resource,
    private val client: OpenAiChatClient
) {

    fun translate(lang: String, text: String): Flux<String> {
       return client.stream(
            Prompt(listOf(
                SystemMessage(systemPrompt),
                PromptTemplate(userPrompt).createMessage(mutableMapOf<String, Any>(
                    "to" to lang, "text" to text
                ))
            ))
        ).map { it.result.output.content }
    }


    fun ts(lang: String, text: String): String {
        return client.call(
            Prompt(listOf(
                SystemMessage(systemPrompt),
                PromptTemplate(userPrompt).createMessage(mutableMapOf<String, Any>(
                    "to" to lang, "text" to text
                ))
            ))
        ).result.output.content
    }
}