package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.WKBLField
import com.bongmany.sportsspecialapi.repository.WKBLRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class WKBLController(private val wkblRepository: WKBLRepository) {

    @GetMapping("/special/wkbl/{teamName}")
    fun getWKBLByTeamName(@PathVariable("teamName") teamName: String): List<WKBLField> {
        return wkblRepository.findAllByHomeTeamOrAwayTeamLike(teamName, teamName)
    }

    @GetMapping("/special/wkbl")
    fun getWKBLAll(): MutableList<WKBLField> {
        return wkblRepository.findAll()
    }

}