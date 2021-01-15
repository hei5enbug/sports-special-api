package com.bongmany.sportsspecialapi.model

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class SDController {

    @GetMapping("/")
    fun hello(): String {
        return "현재 서버 시간:${Date()}"
    }

    @GetMapping("/special/kbl")
    fun getKblData() {
        return
    }
}