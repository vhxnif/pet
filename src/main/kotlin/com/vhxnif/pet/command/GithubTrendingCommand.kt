package com.vhxnif.pet.command

import com.vhxnif.pet.config.annotation.Sword
import com.vhxnif.pet.service.GithubTrending
import com.vhxnif.pet.service.GithubTrending.Since
import groovyjarjarpicocli.CommandLine.Help.Ansi
import kotlinx.coroutines.*
import picocli.CommandLine.Command
import picocli.CommandLine.Option

/**
 *
 * @author vhxnif
 * @since 2024-06-24
 */

@Sword
@Command(
    name = "github-trending",
    aliases = ["gt"],
    description = ["Github Trending."],
    version = ["0.1.0"]
)
class GithubTrendingCommand(
    private val githubTrending: GithubTrending,
) : Runnable {

    @Option(names = ["-s", "--since"], description = ["eg. DAILY WEEKLY MONTHLY"])
    var since: Since = Since.DAILY

    @Option(names = ["-l", "--language"], description = ["eg. kotlin, javascript, java..."])
    var language: String? = null

    /**
     * Pre-defined Styles	Pre-defined Colors
     * ---------------------------------------
     * bold                 black
     * faint                red
     * underline            green
     * italic               yellow
     * blink                blue
     * reverse              magenta
     * reset                cyan
     *                      white
     */
    override fun run(){
         githubTrending.trending(since, language).forEach {
            val str = Ansi.AUTO.string(
                """
                   [@|bold,yellow ${it.name}|@] 
                   @|blue,underline https://github.com/${it.name}|@
                   [@|green ${it.language}|@] @|red,italic ${it.todayStart}|@
                   [@|cyan star|@]: @|red ${it.star}|@ [@|cyan fork|@]: @|red ${it.fork}|@ 
                   @|magenta ${it.desc}|@
                   @|blue ${it.transDesc}|@
                """.trimIndent()
            )
            println(str)
        }
    }
}
