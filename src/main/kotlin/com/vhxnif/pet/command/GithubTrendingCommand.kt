package com.vhxnif.pet.command

import com.vhxnif.pet.config.annotation.Sword
import com.vhxnif.pet.service.GithubTrending
import com.vhxnif.pet.service.GithubTrending.Since
import com.vhxnif.pet.util.ansi
import com.vhxnif.pet.util.PreDefinedColor.*
import com.vhxnif.pet.util.PreDefinedStyle.*
import com.vhxnif.pet.util.WaitTaskList
import com.vhxnif.pet.util.println
import groovyjarjarpicocli.CommandLine.Help.Ansi
import kotlinx.coroutines.*
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.util.concurrent.CompletableFuture

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
    override fun run() {
        githubTrending.trending(since, language).forEach {
            WaitTaskList.startTask()
            CompletableFuture.runAsync {
                val name = ansi { it.name use BOLD use YELLOW }
                val url = ansi { "https://github.com/${it.name}" use UNDERLINE use BOLD use BLUE }
                val lang = ansi { (it.language ?: "") use GREEN }
                val todayStar = ansi { it.todayStar use ITALIC use RED }
                val starTag = ansi { "star" use CYAN }
                val star = ansi { it.star use RED }
                val forkTag = ansi { "fork" use CYAN }
                val fork = ansi { it.fork use RED }
                val desc = ansi { it.desc use MAGENTA }
                val transDesc = ansi { it.transDesc use BLUE }
                """
                   [$name] 
                   $url
                   [$lang] $todayStar
                   [$starTag]: $star [$forkTag]: $fork 
                   $desc
                   $transDesc
                """.trimIndent().println()
                WaitTaskList.downTask()
            }

        }
    }
}
