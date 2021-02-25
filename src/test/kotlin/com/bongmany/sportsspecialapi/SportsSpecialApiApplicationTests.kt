package com.bongmany.sportsspecialapi

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@SpringBootTest
class SportsSpecialApiApplicationTests {

	@Test
	fun contextLoads() {
	}

	@Test
	fun formatter(){
		val scheduleDate = "202103"
		val gameDate = "24(일)"
//		val toDate = SimpleDateFormat("MM/dd(EE)", Locale.KOREAN).parse(gameDate)
//		val dateForm = SimpleDateFormat("yyyy-MM-dd").format(toDate)

		val toDate = SimpleDateFormat("yyyyMMdd(EE)", Locale.KOREAN).parse(scheduleDate + gameDate)
		val dateForm = SimpleDateFormat("yyyy-MM-dd").format(toDate)
		println("toDate : $toDate")
		println("dateForm : $dateForm")
	}

}
