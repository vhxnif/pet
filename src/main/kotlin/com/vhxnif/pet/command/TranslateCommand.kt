package com.vhxnif.pet.command

import com.vhxnif.pet.service.Translate
import com.vhxnif.pet.util.print
import org.springframework.stereotype.Component
import picocli.CommandLine.*

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-24
 */
@Component
@Command(
    name = "translate",
    aliases = ["ts"],
    description = ["AI Translation Tool"],
    version = ["0.1.0"]
)
class TranslateCommand(
    private val translate: Translate
) : Runnable{

    @Parameters
    lateinit var text: String

    @Option(names = ["-l", "--language"], description = ["The translate target language."], defaultValue = "zh")
    lateinit var lang: String

    override fun run() {
        translate.translate(lang, text).print()
    }

}



