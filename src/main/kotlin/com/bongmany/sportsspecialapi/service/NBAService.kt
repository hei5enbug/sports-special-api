package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.URLInformation
import org.jsoup.Jsoup
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class NBAService(private var lastUpdate: Date?) {

    private val nbaData = arrayListOf<List<String>>()

    fun runCrawler(): ArrayList<List<String>> {

        if (lastUpdate == null) lastUpdate = Date.valueOf("0001-12-01")

        val monthList = listOf("december", "january", "february", "march")
        val firstIndex =
            if (lastUpdate!!.month == 11) 0
            else lastUpdate!!.month + 1

        val monthRange = firstIndex..monthList.lastIndex
        var firstMonth = lastUpdate.toString() != "0001-12-01"

        for (i in monthRange) {
            val url = "${URLInformation.secondURL}${monthList[i]}.html"
            if (firstMonth) {
                jsoupSchedule(url, firstMonth)
                firstMonth = false
            } else {
                jsoupSchedule(url)
            }
        }
        return nbaData
    }

    private fun jsoupSchedule(url: String, firstMonth: Boolean = false) {
        var dateScan = firstMonth
        val doc = Jsoup.connect(url).get()
        val scheduleList = doc.select("#schedule tbody").first().getElementsByTag("tr")

        for (tr in scheduleList) {
            val dateGame = tr.getElementsByAttributeValue("data-stat", "date_game").text()
            val toDate = SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH).parse(dateGame)
            val dateForm = SimpleDateFormat("yyyy-MM-dd").format(toDate)
            if (dateScan || lastUpdate.toString() == dateForm) {
                if (lastUpdate.toString() == dateForm) dateScan = false
                continue
            }

            val homeTeam = tr.getElementsByAttributeValue("data-stat", "home_team_name").text()
            val awayTeam = tr.getElementsByAttributeValue("data-stat", "visitor_team_name").text()
            val boxScore = tr.getElementsByAttributeValue("data-stat", "box_score_text").first()
                .getElementsByTag("a")
                .attr("href")
            val specialData = getSpecialData(homeTeam, awayTeam, boxScore)

            if (specialData != null) {
                val dbField = listOf(
                    dateForm, homeTeam, awayTeam,
                    "${specialData[0]}(${specialData[1]})", "${specialData[2]}(${specialData[3]})"
                )
                nbaData.add(dbField)
            } else {
                break
            }
        }
    }

    private fun getSpecialData(homeTeam: String, awayTeam: String, boxScore: String): List<String>? {
        val url = URLInformation.homeURL + boxScore.replace("/boxscores/", "/boxscores/pbp/")
        val doc = Jsoup.connect(url).get()

        var threePointTeam = ""
        var freeThrowTeam = ""
        var threePointPlayer = ""
        var freeThrowPlayer = ""

        val pbp = try {
            doc.select("#pbp tbody").first().getElementsByTag("tr")
        } catch (e: NullPointerException) {
            return null
        }

        for (tr in pbp) {
            val td = tr.getElementsByTag("td")
            var winner = awayTeam
            td.forEach {
                if (freeThrowPlayer == "" && it.text().contains("makes free throw")) {
                    freeThrowTeam = winner
                    freeThrowPlayer = it.getElementsByTag("a").first().text()
                } else if (threePointPlayer == "" && it.text().contains("makes 3-pt")) {
                    threePointTeam = winner
                    threePointPlayer = it.getElementsByTag("a").first().text()
                } else if (it.className().contains("center")) {
                    winner = homeTeam
                }
                if (freeThrowTeam != "" && threePointTeam != "") {
                    return listOf(threePointTeam, threePointPlayer, freeThrowTeam, freeThrowPlayer)
                }
            }
        }
        return null
    }
}