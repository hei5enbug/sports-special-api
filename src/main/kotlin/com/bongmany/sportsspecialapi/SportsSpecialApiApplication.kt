package com.bongmany.sportsspecialapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class SportsSpecialApiApplication

fun main(args: Array<String>) {
    runApplication<SportsSpecialApiApplication>(*args)
}
