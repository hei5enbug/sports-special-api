package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.WesternConf
import org.springframework.data.jpa.repository.JpaRepository

interface WesternRepository : JpaRepository<WesternConf, Long> {
}