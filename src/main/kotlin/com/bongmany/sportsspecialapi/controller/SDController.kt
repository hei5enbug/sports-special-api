package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.repository.SDRepository
import com.bongmany.sportsspecialapi.service.NBAService
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Date

@RestController
class SDController(private val sdRepository: SDRepository) {

    @GetMapping("/")
    fun hello(): String {
        return "Hello Home"
    }

    @GetMapping("/special/nba/update")
    fun updateNbaData(): String {
        val lastData =
            if (sdRepository.findAll().isNotEmpty()) {
                sdRepository.findFirstByOrderByIdDesc().gameDate
            } else {
                Date.valueOf("0001-12-01")
            }
        val nbaDatas = NBAService(lastData!!).runCrawler()

        nbaDatas.forEach { nbaData ->
            val dbField = NBAField()
            dbField.gameDate = Date.valueOf(nbaData[0])
            dbField.homeTeam = nbaData[1]
            dbField.awayTeam = nbaData[2]
            dbField.firstThreePoint = nbaData[3]
            dbField.firstFreeThrow = nbaData[4]

            sdRepository.save(dbField)
        }

        return "업데이트 완료"
    }

    @RequestMapping("/special/nba/last")
    fun getlastNbaData(): NBAField? {

        return try {
            sdRepository.findFirstByOrderByIdDesc()
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    @RequestMapping("/special/nba")
    fun getNbaData(): MutableList<NBAField> {
        return sdRepository.findAll()
    }

}