package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.repository.SDRepository
import com.bongmany.sportsspecialapi.service.NBAService
import org.apache.juli.logging.LogFactory
import org.springframework.web.bind.annotation.*
import java.sql.Date

@RestController
class SDController(private val sdRepository: SDRepository) {

    private final val log = LogFactory.getLog(SDController::class.java)

    @GetMapping("/")
    fun gotoHome(): String {
        return "he5enbug's page"
    }

    @PatchMapping("/special/update")
    fun updateDB(): String {

        log.info("#log - special/nba/update request")

        val lastData = getLastField()?.gameDate
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
        log.info("#log - sepcial/nba/update DB save")

        return "업데이트 완료"
    }

    @RequestMapping("/special/nba/last")
    fun getLastField(): NBAField? {
        return sdRepository.findFirstByOrderByIdDesc()
    }

    @RequestMapping("/special/nba/{teamName}")
    fun getFieldByTeamName(@PathVariable("teamName") teamName: String): List<NBAField> {
        return sdRepository.findAllByHomeTeamOrAwayTeamLike(teamName, teamName)
    }

    @RequestMapping("/special/nba")
    fun getAllField(): MutableList<NBAField> {
        return sdRepository.findAll()
    }

}