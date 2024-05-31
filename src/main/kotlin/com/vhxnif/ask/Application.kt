package com.vhxnif.ask

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.system.exitProcess

@SpringBootApplication
class Application

fun main(args: Array<String>) {
	exitProcess(SpringApplication.exit(SpringApplication.run(Application::class.java, *args)))
}
