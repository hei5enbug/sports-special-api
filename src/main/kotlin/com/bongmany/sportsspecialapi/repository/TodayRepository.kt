package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.TodayGame
import org.springframework.data.jpa.repository.JpaRepository

interface TodayRepository : JpaRepository<TodayGame, Long> {
    fun findAllByLeague(league: String): List<TodayGame>
}