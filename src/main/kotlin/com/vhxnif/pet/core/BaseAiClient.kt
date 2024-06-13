package com.vhxnif.pet.core

import com.vhxnif.pet.core.store.IMessageStore
import com.vhxnif.pet.core.store.toChatMessage
import com.vhxnif.pet.core.store.toMessage
import com.vhxnif.pet.util.systemMessage
import com.vhxnif.pet.util.userMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.Resource

/**
 *
 * @author chen
 * @since 2024-06-13
 */
open class BaseAiClient(
    open val messageStore: IMessageStore
) {

    data class MessageBuilder(
        val messages: MutableList<Message> = mutableListOf()
    ) {
        private fun put(f: () -> Message) = messages.add(f())
        fun user(prompt: Resource, vararg v: Pair<String, Any>) = put {
            PromptTemplate(prompt).createMessage(mutableMapOf(*v))
        }

        fun user(content: String) = put {
            UserMessage(content)
        }

        fun user(message: Message) = put { message }

        fun system(prompt: Resource) = put {
            SystemMessage(prompt)
        }

        fun system(content: String) = put {
            SystemMessage(content)
        }

        fun system(message: Message) = put { message }

    }


    data class PromptBuilder(
        var messages: List<Message>? = null,
        var chatOptions: ChatOptions? = null,
    ) {

        fun message(f: MessageBuilder.() -> Unit) {
            val mb = MessageBuilder().apply { f() }
            messages = mb.messages
        }

        fun option(f: () -> ChatOptions?) {
            chatOptions = f()
        }

    }

    fun prompt(f: PromptBuilder.() -> Unit): Prompt {
        val pb = PromptBuilder().apply {
            f()
        }
        val (messages, chatOptions) = pb
        if (messages.isNullOrEmpty()) {
            error("prompt message is missing")
        }
        return if (chatOptions != null) {
            Prompt(messages, chatOptions)
        } else {
            Prompt(messages)
        }
    }

    fun messages(f: MessageBuilder.() -> Unit): Prompt {
        val (messages) = MessageBuilder().apply { f() }
        if (messages.isEmpty()) {
            error("prompt message is missing")
        }
        return Prompt(messages)
    }

    fun contextPrompt(pt: Prompt): Prompt {
        val messages = pt.systemMessage().concatWith(
            messageStore.contextMessage().map {
                it.toMessage()
            }.concatWith(
                pt.userMessage().doOnNext {
                    messageStore.saveMessage(it.toChatMessage())
                }
            )
        ).collectList().block()
        return if (pt.options != null) {
            Prompt(messages, pt.options as ChatOptions)
        } else {
            Prompt(messages)
        }
    }

}