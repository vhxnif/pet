package com.vhxnif.pet.service

import com.vhxnif.pet.util.call
import com.vhxnif.pet.util.messages
import com.vhxnif.pet.util.userMessage
import org.springframework.ai.chat.StreamingChatClient
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
    private val chatClient: StreamingChatClient,
    @Value("classpath:/prompts/file/user.st")
    private val userPrompt: Resource
) {

    fun chat(text: String, filePath: String): Flux<String> {
        return chatClient.call {
            messages(
                userMessage(userPrompt) {
                    mutableMapOf("text" to text, "doc" to doc(filePath))
                }
            )
        }
    }

    fun doc(filePath: String) : String {
        return TikaDocumentReader(FileSystemResource(filePath)).get().joinToString { it.content }
    }

}