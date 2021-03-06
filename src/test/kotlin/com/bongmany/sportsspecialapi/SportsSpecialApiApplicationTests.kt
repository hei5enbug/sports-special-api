package com.bongmany.sportsspecialapi

import com.bongmany.sportsspecialapi.service.NBAService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SportsSpecialApiApplicationTests {

    @Autowired private lateinit var nbaService: NBAService

    @Test
    fun TestNBAService() {
        nbaService.runCrawler()
    }

}
