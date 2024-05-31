package com.vhxnif.pet.command

import com.vhxnif.pet.config.ChatCustomConfig
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import picocli.CommandLine.Command
import picocli.CommandLine.Option

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-30
 */
@Component
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

    @Option(names = ["-rsp", "--reset-system-prompt"], description = [
        "Reset the system prompt file path. The best file type is a string template, such as xxx.st.",
    ])
    var systemPromptPath : String? = null

    override fun run() {
        when{
            systemPrompt -> chatCustomConfig.systemMessage()?.let { println(it.content) }
            systemPromptPath != null -> chatCustomConfig.setSystemPrompt(FileSystemResource(systemPromptPath!!))
            else -> System.err.println("Option does not match.")
        }
    }
}