package com.bongmany.sportsspecialapi.model

import java.sql.Date
import javax.persistence.*

@MappedSuperclass
open class TeamStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Int = 0

    var teamName: String = ""
    var gameWin: Int = 0
    var gameLose: Int = 0
    var threePointWin: Int = 0
    var threePointLose: Int = 0
    var freeThrowWin: Int = 0
    var freeThrowLose: Int = 0
}