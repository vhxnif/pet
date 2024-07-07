package com.vhxnif.pet.command

import com.vhxnif.pet.config.annotation.Sword
import com.vhxnif.pet.service.Translate
import com.vhxnif.pet.transformer.TranslateTextSplitter
import com.vhxnif.pet.util.print
import com.vhxnif.pet.util.toFile
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.core.io.FileSystemResource
import picocli.CommandLine.*
import reactor.core.publisher.Flux

/**
 *
 * @author vhxnif
 * @since 2024-05-24
 */
@Sword
@Command(
    name = "translate", aliases = ["ts"], description = ["AI Translation Tool."], version = ["0.1.0"]
)
class TranslateCommand(
    private val translate: Translate
) : Runnable {

    @ArgGroup(exclusive = true, multiplicity = "1")
    lateinit var exclusive: Exclusive

    @Option(names = ["-l", "--language"], description = ["The translate target language."], defaultValue = "zh")
    lateinit var lang: String

    @Option(names = ["-s", "--without-source"], description = ["Printed without source text."])
    var ignoreSourceText: Boolean = false

    @Option(names = ["-o", "--output"], description = ["Output file path."])
    var outputPath: String? = null

    class Exclusive {
        @Option(names = ["-f", "--file"], description = ["The file needs to be translated"])
        var file: String? = null

        @Parameters
        var text: String? = null
    }

    override fun run() {
        output {
            when {
                exclusive.file != null -> fileContent(exclusive.file!!)
                else -> listOf(exclusive.text!!)
            }.asSequence().flatMap {
                    with(translate.translate(lang, it)) {
                        listOf(sourceTextProcess(it), this)
                    }
                }
        }
    }

    private fun sourceTextProcess(sourceText: String): Flux<String> {
        return if (ignoreSourceText) {
            Flux.empty()
        } else {
            Flux.just(sourceText)
        }
    }

    private fun fileContent(path: String): List<String> {
        return TranslateTextSplitter().apply(TikaDocumentReader(FileSystemResource(path)).get())
            .map { doc -> doc.content }.filter { c -> c.isNotBlank() }
    }

    private fun output(res: () -> Sequence<Flux<String>>) {
        val translateRes = res()
        if (outputPath != null) {
            translateRes.toFile(outputPath!!)
        } else {
            translateRes.print()
        }
    }


}



