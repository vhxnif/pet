package com.vhxnif.pet.command

import com.vhxnif.pet.config.annotation.Sword
import com.vhxnif.pet.service.CommonChat
import com.vhxnif.pet.service.FileChat
import com.vhxnif.pet.util.print
import picocli.CommandLine.*

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-30
 */
@Sword
@Command(
    name = "chat",
    aliases = ["ct", "ask"],
    description = ["A chat cli tool."],
    version = ["0.1.0"],
)
class ChatCommand(
    private val chat: CommonChat,
    private val fileChat: FileChat,
) : Runnable {

    @Parameters
    lateinit var text: String

    @ArgGroup(exclusive = true)
    var exclusive: Exclusive? = null

    class Exclusive {
        @Option(names = ["-f", "--file"], description = ["The file content that chat based on."])
        var file: String? = null

        @Option(names = ["-c", "--coder"], description = ["The model that uses chat coding."])
        var coder = false
    }

    override fun run() {
        when {
            exclusive?.file != null -> fileChat.chat(text, exclusive?.file!!)
            else -> chat.say(text, exclusive?.coder ?: false)
        }.print()
    }

}