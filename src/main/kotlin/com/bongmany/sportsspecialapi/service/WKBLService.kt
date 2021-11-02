package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.SecurityInformation
import com.bongmany.sportsspecialapi.controller.NBAController
import com.bongmany.sportsspecialapi.model.WKBLField
import com.bongmany.sportsspecialapi.repository.WKBLRepository
import org.apache.juli.logging.LogFactory
import org.jsoup.Jsoup
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

@Service
class WKBLService(private val wkblRepository: WKBLRepository) {

    private var lastUpdate: Date? = null
    private val log = LogFactory.getLog(NBAController::class.java)

    fun runCrawler() {

        lastUpdate = wkblRepository.findFirstByOrderByIdDesc()?.gameDate
        if (lastUpdate == null) lastUpdate = Date.valueOf("2021-10-01")

        val monthList = listOf("202110", "202111", "202112", "202201", "202203")
        val firstIndex = when (val lastMonth = lastUpdate!!.toLocalDate().monthValue) {
            in 10..12 -> lastMonth - 10
            1 -> 3
            3 -> 4
            else -> 0
        }

        val monthRange = firstIndex..monthList.lastIndex
        var firstMonth = lastUpdate.toString() != "2021-10-01"

        for (i in monthRange) {
            if (firstMonth) {
                rangeSchedule(monthList[i], firstMonth)
                firstMonth = false
            } else {
                rangeSchedule(monthList[i])
            }
        }
    }


    private fun rangeSchedule(scheduleDate: String, firstMonth: Boolean = false) {
        var dateScan = firstMonth

        val url = SecurityInformation.wkblURL + scheduleDate + "&viewType=1"
        val doc = Jsoup.connect(url).get()
        val scheduleList = doc.select("div.info_table01.type_list > table > tbody > tr")
        for (tr in scheduleList) {
            val gameDate = tr.text().split(" ")[0].split('/')[1]
            val toDate = SimpleDateFormat("yyyyMMdd(EE)", Locale.KOREAN).parse(scheduleDate + gameDate)
            val dateForm = SimpleDateFormat("yyyy-MM-dd").format(toDate)
            if (dateScan || lastUpdate.toString() == dateForm) {
                if (lastUpdate.toString() == dateForm) dateScan = false
                continue
            }

            val homeTeam = tr.select("div.info_team.away > strong.team_name").text()
            val awayTeam = tr.select("div.info_team.home > strong.team_name").text()
            val gameURL = tr.select("a.btn_type1.btn_pink").attr("href")
            if (gameURL.isEmpty()) break

            val specialData = makeField(gameURL)
            if (specialData.isEmpty()) println(gameURL)
            else {
                val dbField = WKBLField(Date.valueOf(dateForm), homeTeam, awayTeam, specialData[0], specialData[1])
//                log.info("## WKBLService -- $dbField")
                try {
                    wkblRepository.save(dbField)
                } catch (e: DataIntegrityViolationException) {
                    log.error(e)
                    continue
                }
            }
        }
    }

    private fun makeField(gameURL: String): List<String> {
        val doc = Jsoup.connect(gameURL).get()
        val pbp = doc.select("div.playbyplay-content.list > div > ul")

        var threePointTeam = ""
        var freeThrowTeam = ""
        var threePointPlayer = ""
        var freeThrowPlayer = ""

        for (tr in pbp) {
            val ulTeam = tr.select("dt.event-info > strong:nth-child(2)").text()
            val ulPlayer = tr.select("dd.player-info").text().split(" ")[0]
            val ulMessage = try {
                tr.select("dd.player-info").text().split(" ")[1]
            } catch (e: IndexOutOfBoundsException) {
                continue
            }

            if (freeThrowPlayer == "" && ulMessage.contains("자유투성공")) {
                freeThrowTeam = ulTeam
                freeThrowPlayer = ulPlayer
            } else if (threePointPlayer == "" && ulMessage.contains("3점슛성공")) {
                threePointTeam = ulTeam
                threePointPlayer = ulPlayer
            }

            if (freeThrowTeam != "" && threePointTeam != "") {
                return listOf("$threePointTeam($threePointPlayer)", "$freeThrowTeam($freeThrowPlayer)")
            }
        }
        return listOf()
    }
}