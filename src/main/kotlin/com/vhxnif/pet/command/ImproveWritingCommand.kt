package com.vhxnif.pet.command

import com.vhxnif.pet.config.annotation.Sword
import com.vhxnif.pet.service.ImproveWriting
import com.vhxnif.pet.util.print
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

/**
 *
 * @author vhxnif
 * @since 2024-05-31
 */
@Sword
@Command(
    name = "improve writing",
    aliases = ["iw"],
    description = ["Improve Writing."],
    version = ["0.1.0"]
)
class ImproveWritingCommand(
    private val improveWriting: ImproveWriting
) : Runnable {

    @Parameters
    lateinit var text: String

    override fun run() {
        improveWriting.improve(text).print()
    }

}