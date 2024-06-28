package com.vhxnif.pet.core.store

import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.MessageType
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import java.time.Instant
import java.util.UUID

/**
 *
 * @author vhxnif
 * @since 2024-06-12
 */


data class ChatMessage(
    val id: String,
    val type: String,
    val content: String,
    val actionTime: Long
)


fun assistantChatMessage(content: String): ChatMessage {
    return ChatMessage(
        UUID.randomUUID().toString(),
        MessageType.ASSISTANT.value,
        content,
        Instant.now().toEpochMilli()
    )
}

fun Message.toChatMessage(): ChatMessage {
    return ChatMessage(
        UUID.randomUUID().toString(),
        messageType.value,
        content,
        Instant.now().toEpochMilli()
    )
}

fun ChatMessage.toMessage(): Message {
    return when (type) {
        MessageType.USER.value -> UserMessage(content)
        MessageType.SYSTEM.value -> SystemMessage(content)
        MessageType.ASSISTANT.value -> AssistantMessage(content)
        else -> error("chat message type not matched.")
    }
}


interface IMessageStore {

    fun contextMessage() : List<ChatMessage>

    fun saveMessage(messages:Pair<ChatMessage, ChatMessage>)

}

class DefaultMessageStore : IMessageStore {

    override fun contextMessage(): List<ChatMessage> {
        return listOf()
    }

    override fun saveMessage(messages: Pair<ChatMessage, ChatMessage>) {
    }
}
