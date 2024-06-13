package com.vhxnif.pet.core

import com.vhxnif.pet.core.store.IMessageStore
import com.vhxnif.pet.core.store.assistantChatMessage
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component

/**
 *
 * @author chen
 * @since 2024-06-13
 */
@Component
class AiChatClient(
    private val client: ChatClient,
    override val messageStore: IMessageStore,
) : BaseAiClient(messageStore) {

    fun call(f: BaseAiClient.() -> Prompt) : String {
        return client.call(f()).result.output.content
    }

    fun contextCall(f: BaseAiClient.() -> Prompt) : String {
        return client.call(contextPrompt(f())).result.output.content.apply {
            messageStore.saveMessage(assistantChatMessage(this))
        }
    }

}
