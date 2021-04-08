package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.NBAField
import org.springframework.data.jpa.repository.JpaRepository

interface NBARepository : JpaRepository<NBAField, Long> {

    fun findFirstByOrderByIdDesc(): NBAField?
    fun findAllByHomeTeamOrAwayTeamLike(homeTeam: String, awayTeam: String): List<NBAField>

}