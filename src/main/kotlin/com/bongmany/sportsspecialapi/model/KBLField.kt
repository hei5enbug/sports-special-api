package com.bongmany.sportsspecialapi.model

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "kbl")
class KBLField(
    gameDate: Date,
    homeTeam: String,
    awayTeam: String,
    firstThreePoint: String,
    firstFreeThrow: String
) : SpecialData(gameDate, homeTeam, awayTeam, firstThreePoint, firstFreeThrow)