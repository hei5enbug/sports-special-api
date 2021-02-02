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
    var id:Int = 0

    var gameDate: Date = Date(0)
    var homeTeam: String = ""
    var awayTeam: String = ""
    var firstThreePoint: String = ""
    var firstFreeThrow: String = ""

}