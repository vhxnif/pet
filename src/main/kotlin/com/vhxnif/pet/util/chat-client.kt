package com.vhxnif.pet.util

import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.StreamingChatClient
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
 * @since 2024-06-06
 */

fun StreamingChatClient.call(f: () -> Prompt) = stream(f()).map { it.result.output.content }
fun ChatClient.call(f: () -> Prompt): String = call(f()).result.output.content
fun userMessage(template: Resource, f: () -> MutableMap<String, Any>) : Message = PromptTemplate(template).createMessage(f())
fun userMessage(text: String) : Message = UserMessage(text)
fun systemMessage(template: Resource): Message = SystemMessage(template)
fun messages(vararg message: Message?) = Prompt(message.filterNotNull().toList())

class PromptWrapper {
    var messages : List<Message>? = null
    var options : ChatOptions? = null

    fun messages(vararg msg: Message?) {
        messages = msg.filterNotNull().toList()
    }

    fun options(f: () -> ChatOptions?) {
        options = f()
    }

}
fun prompt(f: PromptWrapper.() -> Unit): Prompt {
    return with(PromptWrapper()) {
        f()
        if (messages.isNullOrEmpty()) {
            error("message must not be null.")
        }
        if(options != null) {
            Prompt(messages, options)
        } else {
            Prompt(messages)
        }
    }
}
