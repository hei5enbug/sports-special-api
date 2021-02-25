package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.controller.SDController
import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.model.WKBLField
import com.bongmany.sportsspecialapi.repository.EasternRepository
import com.bongmany.sportsspecialapi.repository.SDRepository
import com.bongmany.sportsspecialapi.repository.WKBLRepository
import com.bongmany.sportsspecialapi.repository.WesternRepository
import org.apache.juli.logging.LogFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Date
import javax.annotation.PostConstruct

// Update Schedule 00:10 AM everyday
@Component
class AutoSDUpdate(
    private val sdRepository: SDRepository,
    private val wkblRepository: WKBLRepository,
    private val easternRepository: EasternRepository,
    private val westernRepository: WesternRepository
) {

    private final val log = LogFactory.getLog(SDController::class.java)

    @PostConstruct
    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    fun updateAllSD(){
        updateNBA()
        updateWKBL()
        updateStat()
    }

    fun updateNBA() {

        log.info("#AutoSDUpdate - NBA update start")

        val nbaLast = sdRepository.findFirstByOrderByIdDesc()?.gameDate
        val nbaDatas = NBAService(nbaLast).runCrawler()

        nbaDatas.forEach { nbaData ->
            val dbField = NBAField()
            dbField.gameDate = Date.valueOf(nbaData[0])
            dbField.homeTeam = nbaData[1]
            dbField.awayTeam = nbaData[2]
            dbField.firstThreePoint = nbaData[3]
            dbField.firstFreeThrow = nbaData[4]

            sdRepository.save(dbField)
        }

        log.info("#AutoSDUpdate - NBA update finish")
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
        val teamList = sdRepository.findTeamList()
        log.info("#AutoSDUpdate - $teamList")
    }

}