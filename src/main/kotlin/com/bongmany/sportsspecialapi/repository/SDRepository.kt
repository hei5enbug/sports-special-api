package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.NBAField
import org.springframework.data.domain.Example
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SDRepository : JpaRepository<NBAField, Long> {

    fun findFirstByOrderByIdDesc()
}