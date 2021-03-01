package com.bongmany.sportsspecialapi

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SportsSpecialApiApplicationTests {

	@Test
	fun contextLoads() {
	}
//
//	@Test
//	fun formatter(){
//		val txtDate = "01.24 (일)"
//		val yearCheck = if (txtDate.substring(0, 2).toInt() in 10..12) "2020" else "2021"
//		val toDate = SimpleDateFormat("yyyyMM.dd (EE)", Locale.KOREAN).parse(yearCheck + txtDate)
//		val dateForm = SimpleDateFormat("yyyy-MM-dd").format(toDate)
//		println(dateForm)
//	}

}
