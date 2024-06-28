package com.vhxnif.pet.core

import com.vhxnif.pet.core.store.IMessageStore
import com.vhxnif.pet.core.store.assistantChatMessage
import com.vhxnif.pet.core.store.toChatMessage
import org.springframework.ai.chat.ChatClient
import org.springframework.stereotype.Component

/**
 *
 * @author vhxnif
 * @since 2024-06-13
 */
@Component
class AiChatClient(
    private val client: ChatClient,
    override val messageStore: IMessageStore,
) : BaseAiClient(messageStore) {

    fun call(f: BaseAiClient.() -> PromptBuilder) : String {
        return client.call(f().toPrompt()).result.output.content
    }

    fun contextCall(f: BaseAiClient.() -> PromptBuilder) : String {
        val promptBuilder = f()
        return client.call(contextPrompt(promptBuilder)).result.output.content.apply {
            messageStore.saveMessage{
                promptBuilder.userMessage!!.toChatMessage() to assistantChatMessage(this)
            }
        }
    }

}
