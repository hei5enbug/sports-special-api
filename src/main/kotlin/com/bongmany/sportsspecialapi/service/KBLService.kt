package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.SecurityInformation
import com.bongmany.sportsspecialapi.controller.NBAController
import com.bongmany.sportsspecialapi.model.KBLField
import com.bongmany.sportsspecialapi.repository.KBLRepository
import org.apache.juli.logging.LogFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Service
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.Level

@Service
class KBLService(private val kblRepository: KBLRepository) {

    private var lastUpdate: Date? = kblRepository.findFirstByOrderByGameDateDesc()?.gameDate
    private lateinit var webDriver: WebDriver
    private var chromeOptions: ChromeOptions? = null
    private val log = LogFactory.getLog(NBAController::class.java)

    fun runCrawler() {
        if (lastUpdate == null) lastUpdate = Date.valueOf("2020-10-09")
        createDriver()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val lastDay = lastUpdate!!.toLocalDate().format(formatter)
        val today = LocalDateTime.now().format(formatter)
        val startCode = if (lastDay == "20201009") {
            80042809
        } else {
            getGameCode(lastDay) + 1
        }
        val endCode = getGameCode(today)
        if (startCode >= endCode) {
            return
        }


        for (gameCode in startCode..endCode) {
            val fieldLine = makeField(gameCode)
            if (fieldLine != null) {
                kblRepository.save(fieldLine)
            }
        }

        quitDriver()
    }

    private fun getGameCode(gameDate: String): Int {
        val url = "${SecurityInformation.kblURL}/schedule/kbl?date=$gameDate"
        webDriver.get(url)
        val trSelect = webDriver.findElements(By.className("tr_selected")).last()
        val tdBtn = trSelect.findElements(By.cssSelector("td.td_btn > a"))
        if (tdBtn.isNotEmpty()) {
            val lastLink = tdBtn[0].getAttribute("href")
            return lastLink.substring(lastLink.length - 8).toInt()
        }
        return 99999999
    }

    private fun makeField(gameCode: Int): KBLField? {
        val url = "${SecurityInformation.kblURL}/game/$gameCode/cast"
//        log.info("#KBLService - $url")
        webDriver.get(url)

        val innerTime = webDriver.findElements(By.className("inner_time"))
        if (innerTime.size == 0) {
            log.info("#KBLService - wrong page error")
            return null
        } else if (!innerTime[0].text.contains("경기종료")) {
            log.info("#KBLService - unfinished game")
            return null
        }

        var threeCheck = false
        var threeWinner = ""
        var freeCheck = false
        var freeWinner = ""

        val txtDate = webDriver.findElement(By.className("txt_time")).text.substring(0, 9)
        val yearCheck = if (txtDate.substring(0, 2).toInt() in 10..12) "2020" else "2021"
        val toDate = SimpleDateFormat("yyyyMM.dd (EE)", Locale.KOREAN).parse(yearCheck + txtDate)
        val dateForm = Date.valueOf(SimpleDateFormat("yyyy-MM-dd").format(toDate))

        val hometeam = webDriver.findElement(
            By.cssSelector(
                "#gameScoreboardWrap > div > div > span.team_vs.team_vs1 > span > span"
            )
        ).text
        val awayteam = webDriver.findElement(
            By.cssSelector(
                "#gameScoreboardWrap > div > div > span.team_vs.team_vs2 > span > span"
            )
        ).text
        val relayPiece = webDriver.findElements(By.className("relay_piece"))
        for (it in relayPiece) {
            val relay = it.text
            if (!threeCheck && relay.contains("3점슛성공")) {
                threeWinner = relay.replace(" 3점슛성공", "")
                threeWinner = threeWinner.replaceFirst(" ", "(") + ")"
                threeCheck = true
            } else if (!freeCheck && relay.contains("자유투성공")) {
                freeWinner = relay.replace(" 자유투성공", "")
                freeWinner = freeWinner.replaceFirst(" ", "(") + ")"
                freeCheck = true
            } else if (threeCheck && freeCheck) break
        }

        return KBLField(dateForm, hometeam, awayteam, threeWinner, freeWinner)
    }

    private fun createDriver() {
        System.setProperty(
            "webdriver.chrome.driver",
            SecurityInformation.chromeDriverPath
        )
        System.setProperty("webdriver.chrome.silentOutput", "true")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        chromeOptions = ChromeOptions().addArguments("headless")
        webDriver = ChromeDriver(chromeOptions)
    }

    private fun quitDriver() {
        webDriver.quit()
    }
}
