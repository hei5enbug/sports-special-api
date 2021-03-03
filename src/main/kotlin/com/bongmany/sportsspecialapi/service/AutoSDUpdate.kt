package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.controller.NBAController
import com.bongmany.sportsspecialapi.model.KBLField
import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.model.WKBLField
import com.bongmany.sportsspecialapi.repository.*
import org.apache.juli.logging.LogFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Date
import javax.annotation.PostConstruct

// Update Schedule 00:10 AM everyday
@Component
class AutoSDUpdate(
    private val NBARepository: NBARepository,
    private val wkblRepository: WKBLRepository,
    private val kblRepository: KBLRepository,
    private val easternRepository: EasternRepository,
    private val westernRepository: WesternRepository
) {

    private final val log = LogFactory.getLog(NBAController::class.java)

    @PostConstruct
    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    fun updateAllSD(){
        updateNBA()
        updateKBL()
//        updateWKBL()
    }

    fun updateNBA() {

        log.info("#AutoSDUpdate - NBA update start")

        val nbaLast = NBARepository.findFirstByOrderByIdDesc()?.gameDate
        val nbaDatas = NBAService(nbaLast).runCrawler()

        nbaDatas.forEach { nbaData ->
            val dbField = NBAField()
            dbField.gameDate = Date.valueOf(nbaData[0])
            dbField.homeTeam = nbaData[1]
            dbField.awayTeam = nbaData[2]
            dbField.firstThreePoint = nbaData[3]
            dbField.firstFreeThrow = nbaData[4]

            NBARepository.save(dbField)
        }

        log.info("#AutoSDUpdate - NBA update finish")
    }

    fun updateKBL() {

        log.info("#AutoSDUpdate - KBL update start")

        val kblLast = kblRepository.findFirstByOrderByIdDesc()?.gameDate
        val kblDatas = KBLService(kblLast).runCrawler()

        kblDatas.forEach { kblData ->
            val dbField = KBLField()
            dbField.gameDate = Date.valueOf(kblData[0])
            dbField.homeTeam = kblData[1]
            dbField.awayTeam = kblData[2]
            dbField.firstThreePoint = kblData[3]
            dbField.firstFreeThrow = kblData[4]

            kblRepository.save(dbField)
        }

        log.info("#AutoSDUpdate - KBL update finish")
    }

    fun updateWKBL() {

        log.info("#AutoSDUpdate - WKBL update start")

        val wkblLast = wkblRepository.findFirstByOrderByIdDesc()?.gameDate
        val wkblDatas = WKBLSerivce(wkblLast).runCrawler()

        wkblDatas.forEach { wkblData ->
            val dbField = WKBLField()
            dbField.gameDate = Date.valueOf(wkblData[0])
            dbField.homeTeam = wkblData[1]
            dbField.awayTeam = wkblData[2]
            dbField.firstThreePoint = wkblData[3]
            dbField.firstFreeThrow = wkblData[4]

            wkblRepository.save(dbField)
        }

        log.info("#AutoSDUpdate - WKBL update finish")
    }


    fun updateStat(){
        val teamList = NBARepository.findTeamList()
        log.info("#AutoSDUpdate - $teamList")
    }

}