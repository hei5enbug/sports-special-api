package com.bongmany.sportsspecialapi.repository

import com.bongmany.sportsspecialapi.model.EasternConf
import org.springframework.data.jpa.repository.JpaRepository

interface EasternRepository : JpaRepository<EasternConf, Long> {

}