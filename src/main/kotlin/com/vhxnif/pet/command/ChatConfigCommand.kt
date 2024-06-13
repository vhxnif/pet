package com.vhxnif.pet.command

import com.vhxnif.pet.config.ChatCustomConfig
import com.vhxnif.pet.config.annotation.Sword
import picocli.CommandLine.Command
import picocli.CommandLine.Option

/**
 *
 * @author chen
 * @since 2024-05-30
 */
@Sword
@Command(
    name = "chat config",
    aliases = ["cf"],
    description = ["About Chat Command config."],
    version = ["0.1.0"]
)
class ChatConfigCommand (
    private val chatCustomConfig: ChatCustomConfig
) : Runnable {

    @Option(names = ["-sp", "--system-prompt"], description = ["The system prompt currently in use."])
    var systemPrompt  = false

    override fun run() {
        when{
            systemPrompt -> chatCustomConfig.systemMessage()?.let { println(it.content) }
            else -> System.err.println("Option does not match.")
        }
    }
}