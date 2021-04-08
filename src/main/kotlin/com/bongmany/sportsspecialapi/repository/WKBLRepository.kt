package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.WKBLField
import org.springframework.data.jpa.repository.JpaRepository

interface WKBLRepository : JpaRepository<WKBLField, Long> {

    fun findFirstByOrderByIdDesc(): WKBLField?
    fun findAllByHomeTeamOrAwayTeamLike(homeTeam: String, awayTeam: String): List<WKBLField>

}