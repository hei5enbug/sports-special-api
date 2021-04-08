package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.KBLField
import org.springframework.data.jpa.repository.JpaRepository

interface KBLRepository : JpaRepository<KBLField, Long> {

    fun findFirstByOrderByGameDateDesc(): KBLField?
    fun findAllByHomeTeamOrAwayTeamLikeOrderByGameDateAsc(homeTeam: String, awayTeam: String): ArrayList<KBLField>
    fun findAllByOrderByGameDateAsc(): ArrayList<KBLField>

}