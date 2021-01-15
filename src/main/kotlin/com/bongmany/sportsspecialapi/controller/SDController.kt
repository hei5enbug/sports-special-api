package com.bongmany.sportsspecialapi.controller

import com.bongmany.sportsspecialapi.model.NBA
import com.bongmany.sportsspecialapi.model.SpecialData
import com.bongmany.sportsspecialapi.repository.SDRepository
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class SDController(private val sdRepository: SDRepository) {

    @GetMapping("/")
    fun hello(): String {
        return "현재 서버 시간:${Date()}"
    }

    @RequestMapping("/special/nba")
    fun getKblData(model:Model): MutableList<NBA> {
//        return "hi"
        return sdRepository.findAll()
    }
}