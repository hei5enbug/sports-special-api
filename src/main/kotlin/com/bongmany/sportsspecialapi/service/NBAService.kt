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
    private var checkDuplicated = false

    fun runCrawler() {
        lastUpdate = nbaRepository.findFirstByOrderByIdDesc()?.gameDate
        if (lastUpdate == null) lastUpdate = Date.valueOf("0001-12-01")
        checkDuplicated = lastUpdate.toString() != "0001-12-01"

        val monthList = listOf("december", "january", "february", "march", "april", "may")
        getTodayGame(monthList)

        val firstIndex =
            if (lastUpdate!!.toLocalDate().monthValue == 12) 0
            else lastUpdate!!.toLocalDate().monthValue
        val monthRange = firstIndex..monthList.lastIndex

        for (i in monthRange) {
            val url = "${SecurityInformation.secondURL}${monthList[i]}.html"
            rangeSchedule(url)
        }
    }

    private fun getTodayGame(monthList: List<String>) {
        val formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH)
        val todayET = ZonedDateTime.now(ZoneId.of("America/New_York"))
        val recentDate = arrayListOf<ZonedDateTime>(
            todayET.minusDays(1),
            todayET,
            todayET.plusDays(1)
        )

        for (gameDate in recentDate) {
            val todayURL = "${SecurityInformation.secondURL}${monthList[gameDate.monthValue]}.html"
            val doc = Jsoup.connect(todayURL).get()
            val scheduleList = doc.getElementsMatchingOwnText(gameDate.format(formatter))
            for (href in scheduleList) {
                val tr = href.parent().parent()
                val dateForm = gameDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                var gameTime = tr.getElementsByAttributeValue("data-stat", "game_start_time").text() + "m"
                gameTime = convertTimeFormat(gameTime)
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

    private fun rangeSchedule(url: String) {
        val doc = Jsoup.connect(url).get()
        val scheduleList = doc.select("#schedule tbody").first().getElementsByTag("tr")

        for (tr in scheduleList) {
            val gameDate = tr.getElementsByAttributeValue("data-stat", "date_game").text()
            val parseGameDate = SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH).parse(gameDate)
            val dateForm = SimpleDateFormat("yyyy-MM-dd").format(parseGameDate)
            if (parseGameDate <= lastUpdate) continue

            val homeTeam = tr.getElementsByAttributeValue("data-stat", "home_team_name").text()
            val awayTeam = tr.getElementsByAttributeValue("data-stat", "visitor_team_name").text()
            val boxScore = tr.getElementsByAttributeValue("data-stat", "box_score_text").first()
                .getElementsByTag("a")
                .attr("href")
            val specialData = getSpecialData(homeTeam, awayTeam, boxScore)

            if (specialData != null) {
                val dbField = NBAField(Date.valueOf(dateForm), homeTeam, awayTeam, specialData[0], specialData[1])
//                log.info("## NBAService save : $dateForm, $homeTeam, $awayTeam, ${specialData[0]}, ${specialData[1]}")
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

    fun convertTimeFormat(prevFormat: String): String {
        val parseGameTime = SimpleDateFormat("h:mma", Locale.ENGLISH).parse(prevFormat)
        return SimpleDateFormat("HH:mm").format(parseGameTime)
    }
}