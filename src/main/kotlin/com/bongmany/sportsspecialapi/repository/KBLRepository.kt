package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.KBLField
import com.bongmany.sportsspecialapi.model.WKBLField
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface KBLRepository : JpaRepository<KBLField, Long> {

    fun findFirstByOrderByIdDesc(): KBLField?
    fun findAllByHomeTeamOrAwayTeamLike(homeTeam: String, awayTeam: String): List<KBLField>

    @Query("select distinct home_team from nba", nativeQuery = true)
    fun findTeamList(): List<String>

}