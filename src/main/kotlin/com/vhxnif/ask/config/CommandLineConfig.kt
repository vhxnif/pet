package com.vhxnif.ask.config

import com.vhxnif.ask.command.*
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import picocli.CommandLine
import picocli.CommandLine.IFactory

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-30
 */
@Configuration
class CommandLineConfig(
    private val appCommand: AppCommand,
    private val chatCommand: ChatCommand,
    private val translateCommand: TranslateCommand,
    private val chatConfigCommand: ChatConfigCommand,
    private val timeConversionCommand: DateTimeConversionCommand,
    private val improveWritingCommand: ImproveWritingCommand,
) {

    @Bean
    fun command(factory: IFactory) = CommandLine(appCommand, factory)
        .addSubcommand(chatCommand)
        .addSubcommand(translateCommand)
        .addSubcommand(chatConfigCommand)
        .addSubcommand(timeConversionCommand)
        .addSubcommand(improveWritingCommand)

    @Bean
    fun runner(commandLine: CommandLine) = CommandLineRunner { commandLine.execute(*it) }

}