package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.WKBLField
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WKBLRepository : JpaRepository<WKBLField, Long> {

    fun findFirstByOrderByIdDesc(): WKBLField?
    fun findAllByHomeTeamOrAwayTeamLike(homeTeam: String, awayTeam: String): List<WKBLField>

    @Query("select distinct home_team from nba", nativeQuery = true)
    fun findTeamList(): List<String>

}