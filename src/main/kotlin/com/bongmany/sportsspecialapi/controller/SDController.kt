package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.repository.SDRepository
import com.bongmany.sportsspecialapi.service.NBAService
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.util.Date

@RestController
class SDController(private val sdRepository: SDRepository) {

    @GetMapping("/")
    fun hello(): String {
        return "현재 서버 시간:${Date()}"
    }

    @GetMapping("/special/nba/update")
    fun updateNbaData(): String {

        val lastData = sdRepository.findFirstByOrderByIdDesc()

        val nbaDatas = NBAService().runCrawler()
        nbaDatas.forEach { nbaData ->
            val dbField = NBAField()
            val tempDate = SimpleDateFormat("yyyy-MM-dd").parse(nbaData[0])
            val sqlDate = java.sql.Date(tempDate.time)
            dbField.gameDate = sqlDate
            dbField.homeTeam = nbaData[1]
            dbField.awayTeam = nbaData[2]
            dbField.firstThreePoint = nbaData[3]
            dbField.firstFreeThrow = nbaData[4]

            sdRepository.save(dbField)
        }

        return "저장 완료"
    }

    @RequestMapping("/special/nba")
    fun getNbaData(model: Model): MutableList<NBAField> {
        return sdRepository.findAll()
    }

}