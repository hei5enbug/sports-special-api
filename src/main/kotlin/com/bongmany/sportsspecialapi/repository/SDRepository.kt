package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.NBA
import org.springframework.data.jpa.repository.JpaRepository

interface SDRepository : JpaRepository<NBA, Long> {


}