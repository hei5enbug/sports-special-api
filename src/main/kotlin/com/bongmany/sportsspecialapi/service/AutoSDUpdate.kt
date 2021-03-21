package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.controller.NBAController
import org.apache.juli.logging.LogFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

// Update Schedule 00:10 AM everyday
@Component
class AutoSDUpdate(
    private val nbaService: NBAService,
    private val wkblService: WKBLService,
    private val kblService: KBLService
) {

    private final val log = LogFactory.getLog(NBAController::class.java)

    @PostConstruct
    @Scheduled(cron = "0 10 21 * * *", zone = "Asia/Seoul")
    fun updateAllSD() {
        // update PM 09:10 everyday
        updateNBA()
        updateKBL()
        updateWKBL()
    }

    fun updateNBA() {
        log.info("#AutoSDUpdate - NBA update start")
        nbaService.runCrawler()
        log.info("#AutoSDUpdate - NBA update finish")
    }

    fun updateKBL() {
        log.info("#AutoSDUpdate - KBL update start")
        kblService.runCrawler()
        log.info("#AutoSDUpdate - KBL update finish")
    }

    fun updateWKBL() {
        log.info("#AutoSDUpdate - WKBL update start")
        wkblService.runCrawler()
        log.info("#AutoSDUpdate - WKBL update finish")
    }

}