package com.bongmany.sportsspecialapi.service

import com.bongmany.sportsspecialapi.controller.NBAController
import com.bongmany.sportsspecialapi.repository.TodayRepository
import org.apache.juli.logging.LogFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
class AutoSDUpdate(
    private val nbaService: NBAService,
    private val wkblService: WKBLService,
    private val kblService: KBLService,
    private val todayRepository: TodayRepository
) {

    private final val log = LogFactory.getLog(NBAController::class.java)

    @PostConstruct
    @Scheduled(cron = "0 00 21 * * *", zone = "Asia/Seoul")
    fun updateAllSD() {
        // update PM 09:00 everyday
        todayRepository.deleteAll()
        updateNBA()
        updateKBL()
        // updateWKBL()
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