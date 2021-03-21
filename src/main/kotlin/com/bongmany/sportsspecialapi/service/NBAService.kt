package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.SecurityInformation
import com.bongmany.sportsspecialapi.controller.NBAController
import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.model.TodayGame
import com.bongmany.sportsspecialapi.repository.NBARepository
import com.bongmany.sportsspecialapi.repository.TodayRepository
import org.apache.juli.logging.LogFactory
import org.jsoup.Jsoup
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class NBAService(private val nbaRepository: NBARepository, private val todayRepository: TodayRepository) {

    private var lastUpdate: Date? = null
    private val log = LogFactory.getLog(NBAController::class.java)

    fun runCrawler() {
        lastUpdate = nbaRepository.findFirstByOrderByIdDesc()?.gameDate
        if (lastUpdate == null) lastUpdate = Date.valueOf("0001-12-01")
        val monthList = listOf("december", "january", "february", "march", "april", "may")

        val todayET = ZonedDateTime.now(ZoneId.of("America/New_York"))
        val todayURL = "${SecurityInformation.secondURL}${monthList[todayET.monthValue]}.html"
        getTodayGame(todayURL, todayET)

        val firstIndex =
            if (lastUpdate!!.toLocalDate().monthValue == 12) 0
            else lastUpdate!!.toLocalDate().monthValue
        val monthRange = firstIndex..monthList.lastIndex
        var firstMonth = lastUpdate.toString() != "0001-12-01"
        for (i in monthRange) {
            val url = "${SecurityInformation.secondURL}${monthList[i]}.html"
//            log.info("## NBAService -- $url")
            if (firstMonth) {
                rangeSchedule(url, firstMonth)
                firstMonth = false
            } else {
                rangeSchedule(url)
            }
        }
    }

    private fun getTodayGame(url: String, todayET: ZonedDateTime) {
        todayRepository.deleteAll()
        val recentDate = arrayListOf<String>(
            todayET.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            todayET.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            todayET.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )

        val doc = Jsoup.connect(url).get()
        val scheduleList = doc.select("#schedule tbody").first().getElementsByTag("tr")
        for (tr in scheduleList) {
            val gameDate = tr.getElementsByAttributeValue("data-stat", "date_game").text()
            val gameTime = tr.getElementsByAttributeValue("data-stat", "game_start_time").text() + "m"
            val toDate = SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH).parse(gameDate)
            val dateForm = SimpleDateFormat("yyyy-MM-dd").format(toDate)
            if (recentDate.contains(dateForm)) {
                val homeTeam = tr.getElementsByAttributeValue("data-stat", "home_team_name").text()
                val awayTeam = tr.getElementsByAttributeValue("data-stat", "visitor_team_name").text()
                val dbField = TodayGame(Date.valueOf(dateForm), homeTeam, awayTeam, "nba", gameTime)
                try {
                    todayRepository.save(dbField)
                } catch (e: DataIntegrityViolationException) {
                    log.error(e)
                    continue
                }
            }
        }
    }

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
                val dbField = NBAField(Date.valueOf(dateForm), homeTeam, awayTeam, specialData[0], specialData[1])
//                log.info("## NBAService -- $dbField")
                try {
                    nbaRepository.save(dbField)
                } catch (e: DataIntegrityViolationException) {
                    log.error(e)
                    continue
                }
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
                    return listOf("$threePointTeam($threePointPlayer)", "$freeThrowTeam($freeThrowPlayer)")
                }
            }
        }
        return null
    }
}