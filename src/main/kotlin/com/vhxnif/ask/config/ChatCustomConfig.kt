package com.vhxnif.ask.config

import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.core.io.Resource

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-28
 */
class ChatCustomConfig {

    companion object {
        var systemPrompt: SystemMessage? = null
        var coderModel: String? = null
    }

    fun systemMessage(): SystemMessage? = systemPrompt
    fun coderModel(): String? = coderModel

    fun setSystemPrompt(systemPromptResource: Resource) {
        systemPrompt = SystemMessage(systemPromptResource)
    }

    fun setCoderModel(model: String) {
        coderModel = model
    }

}