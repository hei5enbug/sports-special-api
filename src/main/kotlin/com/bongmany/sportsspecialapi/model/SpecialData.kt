package com.bongmany.sportsspecialapi.model

import java.sql.Date
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class SpecialData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    var gameDate : Date? = null
    var homeTeam: String? = null
    var awayTeam: String? = null
    var firstThreePoint: String? = null
    var firstFreeThrow: String? = null

}