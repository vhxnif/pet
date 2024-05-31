package com.vhxnif.pet.command

import org.springframework.stereotype.Component
import picocli.CommandLine
import picocli.CommandLine.Command

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-31
 */
@Component
@Command(
    name = "pet",
    description = ["pet"],
    mixinStandardHelpOptions = true,
    version = ["0.1.0"],
    scope = CommandLine.ScopeType.INHERIT
)
class AppCommand : Runnable {
    override fun run() {
        println("All-powerful Pet!")
    }
}