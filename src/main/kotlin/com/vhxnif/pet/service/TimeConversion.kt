package com.vhxnif.pet.service

import com.vhxnif.pet.core.AiChatClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * @author chen
 * @since 2024-05-31
 */
@Component
class TimeConversion(
    private val chatClient: AiChatClient,
    @Value("classpath:/prompts/timezone/system.st")
    private val systemPrompt: Resource,
    @Value("classpath:/prompts/timezone/user.st")
    private val userPrompt: Resource
) {

    companion object {
        const val DEFAULT_TIMEZONE = "Asia/Shanghai"
    }

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    fun convertDateTime(timestamp: Long, timezone: String) {
        println(
            """
                timestamp: $timestamp
                date_time: ${fromTimestamp(timestamp, zoneId(timezone))}
            """.trimIndent()
        )
    }

    fun convertTimestamp(dateTime: String, timezone: String) {
        println(
            """
                date_time: $dateTime 
                timestamp: ${toTimestamp(dateTime, zoneId(timezone))}
            """.trimIndent()
        )
    }

    private fun zoneId(timezone: String): ZoneId {
        return if (DEFAULT_TIMEZONE == timezone) {
            ZoneId.of(DEFAULT_TIMEZONE)
        } else {
            getTimezone(timezone)
        }
    }

    private fun fromTimestamp(timestamp: Long, zoneId: ZoneId): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), zoneId)
    }

    private fun toTimestamp(dateTime: String, zoneId: ZoneId): Long {
        return formatter.withZone(zoneId).parse(dateTime, ZonedDateTime::from).toEpochSecond()
    }

    private fun getTimezone(region: String): ZoneId {
        val timezone = chatClient.call {
            messages {
                system(systemPrompt)
                user(userPrompt, "region" to region)
            }
        }
        println("$region --> $timezone")
        return ZoneId.of(timezone)
    }

}