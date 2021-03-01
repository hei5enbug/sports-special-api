package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.KBLField
import com.bongmany.sportsspecialapi.repository.KBLRepository
import org.apache.juli.logging.LogFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class KBLController(private val kblRepository: KBLRepository) {

    private final val log = LogFactory.getLog(KBLController::class.java)

    @RequestMapping("/special/kbl/{teamName}")
    fun getKBLByTeamName(@PathVariable("teamName") teamName: String): List<KBLField> {
        return kblRepository.findAllByHomeTeamOrAwayTeamLike(teamName, teamName)
    }

    @RequestMapping("/special/kbl")
    fun getKBLAll(): MutableList<KBLField> {
        return kblRepository.findAll()
    }

}