package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.KBLField
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface KBLRepository : JpaRepository<KBLField, Long> {

    @Query()
    fun findFirstByOrderByGameDateDesc(): KBLField?
    fun findAllByHomeTeamOrAwayTeamLikeOrderByGameDateAsc(homeTeam: String, awayTeam: String): ArrayList<KBLField>
    fun findAllByOrderByGameDateAsc(): ArrayList<KBLField>

    @Query("select distinct home_team from nba", nativeQuery = true)
    fun findTeamList(): List<String>

}