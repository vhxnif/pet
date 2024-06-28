package com.vhxnif.pet.config

import com.vhxnif.pet.command.AppCommand
import com.vhxnif.pet.config.annotation.Sword
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import picocli.CommandLine
import picocli.CommandLine.IFactory

/**
 *
 * @author vhxnif
 * @since 2024-05-30
 */
@Configuration
class CommandLineConfig(
    private val applicationContext: ApplicationContext,
    private val appCommand: AppCommand,
) {

    @Bean
    fun command(factory: IFactory) = CommandLine(appCommand, factory).apply {
        applicationContext.getBeansWithAnnotation(Sword::class.java)
            .forEach { (_, u) ->  addSubcommand(u)}
    }

    @Bean
    fun runner(commandLine: CommandLine) = CommandLineRunner { commandLine.execute(*it) }

}