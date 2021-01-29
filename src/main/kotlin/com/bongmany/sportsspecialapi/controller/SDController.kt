package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.repository.SDRepository
import org.apache.juli.logging.LogFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SDController(private val sdRepository: SDRepository) {

    private final val log = LogFactory.getLog(SDController::class.java)

    @GetMapping("/")
    fun gotoHome(): String {
        return "Sports SpecialData Api"
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