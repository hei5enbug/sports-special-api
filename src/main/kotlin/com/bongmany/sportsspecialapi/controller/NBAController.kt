package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.NBAField
import com.bongmany.sportsspecialapi.model.TodayGame
import com.bongmany.sportsspecialapi.repository.NBARepository
import com.bongmany.sportsspecialapi.repository.TodayRepository
import org.apache.juli.logging.LogFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NBAController(private val nbaRepository: NBARepository, private val todayRepository: TodayRepository) {

    private final val log = LogFactory.getLog(NBAController::class.java)

    @GetMapping("/")
    fun gotoHome(): String {
        return "Sports SpecialData Api"
    }

    @RequestMapping("/special/nba/{teamName}")
    fun getNBAByTeamName(@PathVariable("teamName") teamName: String): List<NBAField> {
        return nbaRepository.findAllByHomeTeamOrAwayTeamLike(teamName, teamName)
    }

    @RequestMapping("/special/nba")
    fun getNBAAll(): MutableList<NBAField> {
        return nbaRepository.findAll()
    }

    @RequestMapping("/special/nba/today")
    fun getNBAToday(): MutableList<TodayGame> {
        return todayRepository.findAll()
    }

}