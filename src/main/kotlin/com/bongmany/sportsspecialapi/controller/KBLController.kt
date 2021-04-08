package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.KBLField
import com.bongmany.sportsspecialapi.repository.KBLRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class KBLController(private val kblRepository: KBLRepository) {

    @GetMapping("/special/kbl/{teamName}")
    fun getKBLByTeamName(@PathVariable("teamName") teamName: String): ArrayList<KBLField> {
        return kblRepository.findAllByHomeTeamOrAwayTeamLikeOrderByGameDateAsc(teamName, teamName)
    }

    @GetMapping("/special/kbl")
    fun getKBLAll(): ArrayList<KBLField> {
        return kblRepository.findAllByOrderByGameDateAsc()
    }

}