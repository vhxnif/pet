package com.vhxnif.pet.util

import org.springframework.ai.chat.messages.MessageType
import org.springframework.ai.chat.prompt.Prompt
import reactor.kotlin.core.publisher.toFlux

/**
 *
 * @author vhxnif
 * @since 2024-06-06
 */

fun Prompt.userMessage() = instructions.toFlux().filter { it.messageType == MessageType.USER }
fun Prompt.systemMessage() = instructions.toFlux().filter { it.messageType == MessageType.SYSTEM }