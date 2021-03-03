package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.SecurityInformation
import com.bongmany.sportsspecialapi.controller.NBAController
import org.apache.juli.logging.LogFactory
import org.jsoup.Jsoup
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class NBAService(private var lastUpdate: Date?) {

    private val nbaData = arrayListOf<List<String>>()
    private val log = LogFactory.getLog(NBAController::class.java)

    // 월별 크롤링할 url 수정
    fun runCrawler(): ArrayList<List<String>> {

        if (lastUpdate == null) lastUpdate = Date.valueOf("0001-12-01")

        val monthList = listOf("december", "january", "february", "march")
        val firstIndex =
            if (lastUpdate!!.toLocalDate().monthValue == 12) 0
            else lastUpdate!!.toLocalDate().monthValue

        val monthRange = firstIndex..monthList.lastIndex
        var firstMonth = lastUpdate.toString() != "0001-12-01"

        for (i in monthRange) {
            val url = "${SecurityInformation.secondURL}${monthList[i]}.html"
            if (firstMonth) {
                rangeSchedule(url, firstMonth)
                firstMonth = false
            } else {
                rangeSchedule(url)
            }
        }
        return nbaData
    }

    // 
    private fun rangeSchedule(url: String, firstMonth: Boolean = false) {
        var dateScan = firstMonth
        val doc = Jsoup.connect(url).get()
        val scheduleList = doc.select("#schedule tbody").first().getElementsByTag("tr")

        for (tr in scheduleList) {
            val gameDate = tr.getElementsByAttributeValue("data-stat", "date_game").text()
            val toDate = SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH).parse(gameDate)
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
//                log.info("## NBAService -- $dbField")
                nbaData.add(dbField)
            } else {
                break
            }
        }
    }

    private fun getSpecialData(homeTeam: String, awayTeam: String, boxScore: String): List<String>? {
        val url = SecurityInformation.nbaURL + boxScore.replace("/boxscores/", "/boxscores/pbp/")
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