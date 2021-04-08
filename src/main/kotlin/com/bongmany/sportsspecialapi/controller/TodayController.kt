package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.TodayGame
import com.bongmany.sportsspecialapi.repository.TodayRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TodayController(private val todayRepository: TodayRepository) {

    @GetMapping("/")
    fun gotoHome(): String {
        return "Sports SpecialData Api"
    }

    @GetMapping("/special/nba/today")
    fun getNBAToday(): List<TodayGame> {
        return todayRepository.findAllByLeague("nba")
    }

    @GetMapping("/special/kbl/today")
    fun getKBLToday(): List<TodayGame> {
        return todayRepository.findAllByLeague("kbl")
    }

}