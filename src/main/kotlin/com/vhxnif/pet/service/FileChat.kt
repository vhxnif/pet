package com.vhxnif.pet.service

import com.vhxnif.pet.core.StreamingAiChatClient
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author chen
 * @since 2024-05-27
 */
@Component
class FileChat(
    private val chatClient: StreamingAiChatClient,
    @Value("classpath:/prompts/file/user.st")
    private val userPrompt: Resource
) {

    fun chat(text: String, filePath: String): Flux<String> {
        return chatClient.call {
            messages {
                user(userPrompt, "text" to text, "doc" to doc(filePath))
            }
        }
    }

    fun doc(filePath: String) : String {
        return TikaDocumentReader(FileSystemResource(filePath)).get().joinToString { it.content }
    }

}