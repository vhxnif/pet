package com.vhxnif.ask.command

import com.vhxnif.ask.service.TimeConversion
import org.springframework.stereotype.Component
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Command
import picocli.CommandLine.Option

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-31
 */
@Component
@Command(name = "time convert", aliases = ["tc"], description = ["Convert dates and timestamps between each other."])
class DateTimeConversionCommand(
    private val timeConversion: TimeConversion
) : Runnable {

    class Exclusive {
        @Option(names = ["-s", "--timestamp"], description = ["timestamp"])
        var timestamp : Long? = null

        @Option(names = ["-t", "--date-time"], description = ["date time string, yyyy-MM-dd HH:mm:ss"])
        var datetime: String? = null
    }

    @ArgGroup(exclusive = true, multiplicity = "1")
    lateinit var exclusive: Exclusive

    @Option(names = ["-z", "--timezone"], description = ["timezone, eg. Asia/Shanghai"])
    var timezone: String = TimeConversion.DEFAULT_TIMEZONE

    override fun run() {
        exclusive.timestamp?.let {
           timeConversion.convertDateTime(it, timezone)
        }

        exclusive.datetime?.let {
            timeConversion.convertTimestamp(it, timezone)
        }
    }
}