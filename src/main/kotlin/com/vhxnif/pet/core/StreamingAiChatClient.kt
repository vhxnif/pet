package com.vhxnif.pet.core

import com.vhxnif.pet.core.store.IMessageStore
import com.vhxnif.pet.core.store.assistantChatMessage
import org.springframework.ai.chat.StreamingChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

/**
 *
 * @author chen
 * @since 2024-06-13
 */
@Component
class StreamingAiChatClient(
    private val streamingClient: StreamingChatClient,
    override val messageStore: IMessageStore
) : BaseAiClient(messageStore) {


    fun call(f: BaseAiClient.() -> Prompt) : Flux<String> {
        return streamingClient.stream(f()).map { it.result.output.content }
    }

    fun contextCall(f: BaseAiClient.() -> Prompt) : Flux<String> {
        val res = StringBuilder()
        return streamingClient.stream(contextPrompt(f())).map { it.result.output.content }.doOnNext {
           res.append(it)
        }.doFinally {
           messageStore.saveMessage(assistantChatMessage(res.toString()))
        }
    }

}

