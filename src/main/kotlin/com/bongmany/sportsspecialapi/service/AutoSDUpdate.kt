package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.controller.SDController
import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.repository.EasternRepository
import com.bongmany.sportsspecialapi.repository.SDRepository
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
    private val easternRepository: EasternRepository,
    private val westernRepository: WesternRepository
) {

    private final val log = LogFactory.getLog(SDController::class.java)

    @PostConstruct
    @Scheduled(cron = "0 10 0 * * *")
    fun updateSpecialData() {

        log.info("#log - special/nba/update request")

        val lastData = sdRepository.findFirstByOrderByIdDesc()?.gameDate
        val nbaDatas = NBAService(lastData).runCrawler()

        nbaDatas.forEach { nbaData ->
            val dbField = NBAField()
            dbField.gameDate = Date.valueOf(nbaData[0])
            dbField.homeTeam = nbaData[1]
            dbField.awayTeam = nbaData[2]
            dbField.firstThreePoint = nbaData[3]
            dbField.firstFreeThrow = nbaData[4]

            sdRepository.save(dbField)
        }
        updateStat()

        log.info("#log - sepcial/nba/update DB save")
    }


    fun updateStat(){

        val teamList = sdRepository.findTeamList()
        log.info("#log - $teamList")

    }

}