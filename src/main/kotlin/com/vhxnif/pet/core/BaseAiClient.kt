package com.vhxnif.pet.core

import com.vhxnif.pet.core.store.IMessageStore
import com.vhxnif.pet.core.store.toMessage
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

    data class PromptBuilder(
        var systemMessage: Message? = null,
        var userMessage: Message? = null,
        var chatOptions: ChatOptions? = null,
    ) {

        private fun setUser(f: () ->  Message) {
            userMessage = f()
        }

        private fun setSystem(f: () -> Message) {
            systemMessage = f()
        }

        fun user(prompt: Resource, vararg v: Pair<String, Any>) = setUser {
            PromptTemplate(prompt).createMessage(mutableMapOf(*v))
        }

        fun user(content: String) = setUser {
            UserMessage(content)
        }

        fun user(message: Message) = setUser { message }

        fun system(prompt: Resource) = setSystem{
            SystemMessage(prompt)
        }

        fun system(content: String) = setSystem {
            SystemMessage(content)
        }

        fun system(message: Message) = setSystem { message }

        fun option(f: () -> ChatOptions?) {
            chatOptions = f()
        }

        fun toPrompt() : Prompt {
            return assert {
                val messages = mutableListOf<Message>().apply {
                    if (systemMessage != null) {
                        add(systemMessage!!)
                    }
                }
                messages.add(userMessage!!)
                if (chatOptions != null) {
                    Prompt(messages, chatOptions)
                } else {
                    Prompt(messages)
                }
            }
        }

        fun toPrompt(context: List<Message>) : Prompt {
            return assert {
                generatePrompt {
                    mutableListOf<Message>() .apply {
                        if (systemMessage != null) {
                            add(systemMessage!!)
                        }
                        if (context.isNotEmpty()) {
                            addAll(context)
                        }
                        add(userMessage!!)
                    }
                }
            }
        }

        private fun <R> assert(f: () -> R): R {
            if (userMessage == null) {
                error("message is missing.")
            }
            return f()
        }

        private fun generatePrompt(f: () -> List<Message>): Prompt {
            val messages = f()
            return if (chatOptions != null) {
                Prompt(messages, chatOptions)
            } else {
                Prompt(messages)
            }
        }

    }

    fun prompt(f: PromptBuilder.() -> Unit): PromptBuilder  = PromptBuilder().apply {
        f()
        if (userMessage == null) {
            error("message is missing")
        }
    }

    fun contextPrompt(pt: PromptBuilder): Prompt {
        return pt.toPrompt(messageStore.contextMessage().map{ it.toMessage() })
    }

}