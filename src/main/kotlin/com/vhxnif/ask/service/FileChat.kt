package com.vhxnif.ask.service

import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.openai.OpenAiChatClient
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-27
 */
@Component
class FileChat(
    private val chatClient: OpenAiChatClient,
    @Value("classpath:/prompts/file/user.st")
    private val userPrompt: Resource
) {

    fun chat(text: String, filePath: String): Flux<String> {
        val doc = TikaDocumentReader(FileSystemResource(filePath)).get().joinToString { it.content }
        return chatClient.stream(
            Prompt(
                listOf(
                    PromptTemplate(userPrompt)
                        .createMessage(
                            mutableMapOf<String, Any>("text" to text, "doc" to doc)
                        )
                )
            )
        ).map { it.result.output.content }
    }

}