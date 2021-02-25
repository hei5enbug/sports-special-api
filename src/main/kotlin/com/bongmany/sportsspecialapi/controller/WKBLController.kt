package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.model.WKBLField
import com.bongmany.sportsspecialapi.repository.SDRepository
import com.bongmany.sportsspecialapi.repository.WKBLRepository
import org.apache.juli.logging.LogFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WKBLController(private val wkblRepository: WKBLRepository) {

    private final val log = LogFactory.getLog(WKBLController::class.java)

    @RequestMapping("/special/wkbl/{teamName}")
    fun getWKBLByTeamName(@PathVariable("teamName") teamName: String): List<WKBLField> {
        return wkblRepository.findAllByHomeTeamOrAwayTeamLike(teamName, teamName)
    }

    @RequestMapping("/special/wkbl")
    fun getWKBLAll(): MutableList<WKBLField> {
        return wkblRepository.findAll()
    }

}