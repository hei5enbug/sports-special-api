package com.bongmany.sportsspecialapi.model

import java.sql.Date
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class SpecialData(
    var gameDate: Date,
    var homeTeam: String,
    var awayTeam: String,
    var firstThreePoint: String,
    var firstFreeThrow: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
}