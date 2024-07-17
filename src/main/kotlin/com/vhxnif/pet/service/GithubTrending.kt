package com.vhxnif.pet.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

/**
 *
 * @author vhxnif
 * @since 2024-06-21
 */
@Component
class GithubTrending {

    enum class Since(val value: String) {
        DAILY("daily"),
        WEEKLY("weekly"),
        MONTHLY("monthly"),
    }


    data class Project(
        val name: String,
        val desc: String,
        val star: String,
        val fork: String,
        val todayStar: String,
        val language: String?,
        var transDesc: String = ""
    )

    fun trending(since: Since, language: String? = null): List<Project> {
        val url = "https://github.com/trending" + if (language != null) "/$language" else "" + "?since=${since.value}"
        return trendingPageProcess(url)
    }

    private fun trendingPageProcess(url: String): List<Project> {
        var doc = Jsoup.connect(url).get()
        return doc.select("article").map {
            var metrics = metrics(it)
            var startAndFork = starAndFork(metrics)
            Project(
                name(it),
                desc(it),
                startAndFork.first,
                startAndFork.second,
                todayStars(metrics),
                language(metrics)
            )
        }
    }

    private fun name(doc: Element): String {
        return doc.select("h2")[0].text().replace(" ", "")
    }

    private fun desc(doc: Element): String {
        return with(doc.select("p")) {
            if (isNotEmpty()) this[0].text() else ""
        }
    }

    private fun metrics(doc: Element): Element {
        return doc.select("div.f6")[0]
    }

    private fun starAndFork(metrics: Element): Pair<String, String> {
        val startAndFork = metrics.select("> a").map { it.text() }
        return Pair(startAndFork[0], startAndFork[1])
    }

    private fun todayStars(metrics: Element): String {
        return metrics.select("span").last()?.text() ?: ""
    }

    private fun language(metrics: Element): String? {
        val language = metrics.select("span > span")
        return if (language.size < 2) {
            "Unknow"
        } else {
            language[1].text()
        }
    }

}