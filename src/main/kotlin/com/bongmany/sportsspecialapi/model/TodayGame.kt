package com.bongmany.sportsspecialapi.model

import java.io.Serializable
import java.sql.Date
import javax.persistence.*

@Embeddable
data class TodayGameId(
    var gameDate: Date,
    var homeTeam: String,
    var awayTeam: String
) : Serializable

@Entity
@IdClass(TodayGameId::class)
@Table(name = "today_game")
data class TodayGame(
    @get:Id
    var gameDate: Date,
    @get:Id
    var homeTeam: String,
    @get:Id
    var awayTeam: String,
    var league: String
)
