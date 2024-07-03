package com.vhxnif.pet.core

import com.vhxnif.pet.core.store.IMessageStore
import com.vhxnif.pet.core.store.assistantChatMessage
import com.vhxnif.pet.core.store.toChatMessage
import com.vhxnif.pet.util.WaitTaskList
import org.springframework.ai.chat.model.StreamingChatModel
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author vhxnif
 * @since 2024-06-13
 */
@Component
class StreamingAiChatClient(
    private val streamingClient: StreamingChatModel,
    override val messageStore: IMessageStore
) : BaseAiClient(messageStore) {


    fun call(f: BaseAiClient.() -> PromptBuilder) : Flux<String> {
        return streamingClient.stream(f().toPrompt()).map { it.result.output.content }
    }

    fun contextCall(f: BaseAiClient.() -> PromptBuilder) : Flux<String> {
        WaitTaskList.startTask()
        val promptBuilder = f()
        val res = StringBuilder()
        return streamingClient.stream(contextPrompt(promptBuilder)).map { it.result.output.content }.doOnNext {
            res.append(it)
        }.doFinally {
            messageStore.saveMessage {
                promptBuilder.userMessage!!.toChatMessage() to assistantChatMessage(res.toString())
            }
            WaitTaskList.downTask()
        }
    }
}

