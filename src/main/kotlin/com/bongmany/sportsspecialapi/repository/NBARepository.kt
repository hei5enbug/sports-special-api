package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.NBAField
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NBARepository : JpaRepository<NBAField, Long> {

    fun findFirstByOrderByIdDesc(): NBAField?
    fun findAllByHomeTeamOrAwayTeamLike(homeTeam: String, awayTeam: String): List<NBAField>

    @Query("select distinct home_team from nba", nativeQuery = true)
    fun findTeamList(): List<String>

}