package com.bongmany.sportsspecialapi.model

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "nba_western")
class WesternConf: TeamStat()