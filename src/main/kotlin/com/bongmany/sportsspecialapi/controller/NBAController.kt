package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.model.TodayGame
import com.bongmany.sportsspecialapi.repository.NBARepository
import com.bongmany.sportsspecialapi.repository.TodayRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NBAController(private val nbaRepository: NBARepository, private val todayRepository: TodayRepository) {

    @GetMapping("/special/nba/{teamName}")
    fun getNBAByTeamName(@PathVariable("teamName") teamName: String): List<NBAField> {
        return nbaRepository.findAllByHomeTeamOrAwayTeamLike(teamName, teamName)
    }

    @GetMapping("/special/nba")
    fun getNBAAll(): MutableList<NBAField> {
        return nbaRepository.findAll()
    }

}