package com.vhxnif.pet.command

import com.vhxnif.pet.service.Translate
import com.vhxnif.pet.transformer.TranslateTextSplitter
import com.vhxnif.pet.util.print
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.core.io.FileSystemResource
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
) : Runnable {

    @ArgGroup(exclusive = true, multiplicity = "1")
    lateinit var exclusive: Exclusive

    @Option(names = ["-l", "--language"], description = ["The translate target language."], defaultValue = "zh")
    lateinit var lang: String

    @Option(names = ["-s", "--without-source"], description = ["Printed without source text."])
    var printIgnoreSourceText: Boolean = false

    class Exclusive {
        @Option(names = ["-f", "--file"], description = ["The file needs to be translated"])
        var file: String? = null

        @Parameters
        var text: String? = null
    }

    override fun run() {
        when {
            exclusive.file != null -> TranslateTextSplitter()
                .apply(TikaDocumentReader(FileSystemResource(exclusive.file!!)).get())
                .map { doc -> doc.content }
                .filter { c -> c.isNotBlank() }
            else -> listOf(exclusive.text!!)
        }.asSequence().map {
            translate.translate(lang, it).doFirst {
                if(!printIgnoreSourceText) {
                    println(it)
                }
            }
        }.print()
    }

}



