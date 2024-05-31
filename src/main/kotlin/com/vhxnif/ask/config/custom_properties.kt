package com.vhxnif.ask.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-29
 */
@ConfigurationProperties(prefix = "custom.config")
class CustomProperties (
    var chat: ChatOption? = null
)

class ChatOption (
    var systemPromptPath: String? = null,
    var coderModel: String? = null,
)