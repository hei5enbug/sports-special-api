package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.SecurityInformation
import com.bongmany.sportsspecialapi.controller.NBAController
import com.bongmany.sportsspecialapi.model.KBLField
import com.bongmany.sportsspecialapi.model.TodayGame
import com.bongmany.sportsspecialapi.repository.KBLRepository
import com.bongmany.sportsspecialapi.repository.TodayRepository
import org.apache.juli.logging.LogFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.Level


@Service
class KBLService(private val kblRepository: KBLRepository, private val todayRepository: TodayRepository) {

    private var lastUpdate: Date? = null
    private var chromeOptions: ChromeOptions? = null
    private lateinit var webDriver: WebDriver
    private lateinit var originalWindow: String
    private val log = LogFactory.getLog(NBAController::class.java)

    fun runCrawler() {
        lastUpdate = kblRepository.findFirstByOrderByGameDateDesc()?.gameDate
        if (lastUpdate == null) lastUpdate = Date.valueOf("2020-10-09")

        val monthList = listOf(
            "202010", "202011", "202012",
            "202101", "202102", "202103", "202104", "202105"
        )
        val firstIndex = when (val lastMonth = lastUpdate!!.toLocalDate().monthValue) {
            10, 11, 12 -> lastMonth - 10
            else -> lastMonth + 2
        }
        val monthRange = firstIndex..monthList.lastIndex
        createDriver()

        try {
            getTodayGame()
            for (monthIndex in monthRange) {
                rangeSchedule(monthList[monthIndex])
            }
        } finally {
            quitDriver()
        }
    }

    private fun rangeSchedule(yearMonth: String) {
        val url = "${SecurityInformation.kblURL}/schedule/kbl?date=$yearMonth"
        webDriver.get(url)

        val scheduleList = webDriver.findElements(By.cssSelector("#scheduleList > tr"))
        for (tr in scheduleList) {
            val gameLink = tr.findElements(By.cssSelector("td.td_btn > a"))
            val dataDate = tr.getAttribute("data-date")
            val toDate = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).parse(dataDate)
            val gameDate = Date.valueOf(SimpleDateFormat("yyyy-MM-dd").format(toDate))
            if (gameLink.isEmpty() || (gameDate <= lastUpdate)) continue

            val fieldLine = makeField(gameLink[0].getAttribute("href"))
            if (fieldLine != null) {
                try {
                    kblRepository.save(fieldLine)
                } catch (e: DataIntegrityViolationException) {
                    log.error(e)
                    continue
                }
            }
        }
    }

    private fun getTodayGame() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val todayKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        val recentDate = arrayListOf<String>(
            todayKST.minusDays(1).format(formatter),
            todayKST.format(formatter),
            todayKST.plusDays(1).format(formatter)
        )

        for (gameDate in recentDate) {
            val dateCode = gameDate.replace("-", "")
            val url = "${SecurityInformation.kblURL}/schedule/kbl?date=$dateCode"
            webDriver.get(url)

            val trSelect = webDriver.findElements(By.className("tr_selected"))
            for (tr in trSelect) {
                val gameTime = tr.findElements(By.className("td_time"))
                if (gameTime.isEmpty()) break
                val homeTeam = tr.findElements(By.className("txt_team"))[0].text
                val awayTeam = tr.findElements(By.className("txt_team"))[1].text
                val dbField = TodayGame(Date.valueOf(gameDate), homeTeam, awayTeam, "kbl", gameTime[0].text)
                try {
                    todayRepository.save(dbField)
                } catch (e: DataIntegrityViolationException) {
                    log.error(e)
                    continue
                }
            }
        }
    }

    private fun makeField(url: String): KBLField? {
        log.debug("# KBLService - $url")

        val secondOptions = ChromeOptions()
            .addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage")
        val secondDriver = ChromeDriver(secondOptions)
        secondDriver.get(url)

        val innerTime = secondDriver.findElements(By.className("inner_time"))
        if (innerTime.size == 0) {
            log.info("# KBLService - wrong page error")
            return null
        } else if (!innerTime[0].text.contains("경기종료")) {
            log.info("# KBLService - unfinished game")
            return null
        }

        var threeCheck = false
        var threeWinner = ""
        var freeCheck = false
        var freeWinner = ""

        val txtDate = secondDriver.findElement(By.className("txt_time")).text.substring(0, 9)
        val yearCheck = if (txtDate.substring(0, 2).toInt() in 10..12) "2020" else "2021"
        val toDate = SimpleDateFormat("yyyyMM.dd (EE)", Locale.KOREAN).parse(yearCheck + txtDate)
        val dateForm = Date.valueOf(SimpleDateFormat("yyyy-MM-dd").format(toDate))

        val hometeam = secondDriver.findElement(
            By.cssSelector(
                "#gameScoreboardWrap > div > div > span.team_vs.team_vs1 > span > span"
            )
        ).text
        val awayteam = secondDriver.findElement(
            By.cssSelector(
                "#gameScoreboardWrap > div > div > span.team_vs.team_vs2 > span > span"
            )
        ).text
        val relayPiece = secondDriver.findElements(By.className("relay_piece"))
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

        secondDriver.quit()

        return KBLField(dateForm, hometeam, awayteam, threeWinner, freeWinner)
    }

    private fun createDriver() {
        System.setProperty(
            "webdriver.chrome.driver",
            SecurityInformation.chromeDriverPath
        )
        System.setProperty("webdriver.chrome.silentOutput", "true")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        chromeOptions = ChromeOptions()
            .addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage")

        webDriver = ChromeDriver(chromeOptions)
    }

    private fun quitDriver() {
        webDriver.quit()
        log.info("# KBLService - quitDriver Success")
    }
}
