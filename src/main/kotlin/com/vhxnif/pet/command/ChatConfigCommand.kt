package com.vhxnif.pet.command

import com.vhxnif.pet.config.annotation.Sword
import com.vhxnif.pet.service.CommonChat
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Command
import picocli.CommandLine.Help.Ansi
import picocli.CommandLine.Option
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * @author vhxnif
 * @since 2024-05-30
 */
@Sword
@Command(
    name = "chat-config",
    aliases = ["cf"],
    description = ["About Chat Command config."],
    version = ["0.1.0"]
)
class ChatConfigCommand(
    private val commonChat: CommonChat,
) : Runnable {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    @ArgGroup(exclusive = true, multiplicity = "1")
    var exclusive: Exclusive = Exclusive()

    class Exclusive {
        @Option(names = ["-sp", "--system-prompt"], description = ["The system prompt currently in use."])
        var systemPrompt = false

        @Option(names = ["-cs", "--chats"], description = ["The chats used."])
        var chats = false

        @Option(names = ["-sc", "--select-chat"], description = ["Select by chat No."])
        var selectChat: Int? = null

        @Option(names = ["-dc", "--del-chat"], description = ["Delete  by chat No."])
        var delChat: Int? = null

        @Option(names = ["-ct", "--context"], description = ["The context chat messages."])
        var context = false
    }


    override fun run() {
        when {
            exclusive.systemPrompt -> println(commonChat.systemPrompt())
            exclusive.chats -> chats()
            exclusive.selectChat != null -> selectChat(exclusive.selectChat!!)
            exclusive.delChat != null -> delChat(exclusive.delChat!!)
            exclusive.context -> context()
            else -> println(Ansi.AUTO.string("@|red Option does not match.|@"))
        }
    }

    private fun chats() {
        commonChat.chats().forEachIndexed { idx, it ->
            println(Ansi.AUTO.string("${if (idx == 0) "[@|cyan *|@]" else "[@|magenta $idx|@]"} @|bold,yellow ${it.name}|@"))
        }
    }

    private fun selectChat(selectIdx: Int) {
        idxToName(selectIdx)?.let {
            commonChat.selectOrNewChat(it)
            chats()
        }
    }

    private fun delChat(delIdx: Int) {
        idxToName(delIdx)?.let {
            commonChat.delChat(it)
            chats()
        }
    }

    private fun context() {
        commonChat.contextChatMessage().forEach {
            println(
                Ansi.AUTO.string("""
                    [@|yellow ${it.type}|@] [@|magenta ${time(it.actionTime)}|@]
                    @|blue ${fmtContent(it.content)}|@
                """.trimIndent())
            )
            println()
        }
    }

    private fun time(epochMilli: Long): String {
        return formatter.format(
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault())
        )
    }

    private fun fmtContent(content: String): String {
        val limit = 30
        return if (content.length < limit) {
            content
        } else {
            "${content.substring(0, limit)}..."
        }
    }

    private fun idxToName(idx: Int): String? {
        val chats = commonChat.chats()
        return if (idx <= chats.size - 1) {
            chats[idx].name
        } else {
            println(Ansi.AUTO.string("@|red Chat No.$idx Missing.|@"))
            null
        }
    }

}