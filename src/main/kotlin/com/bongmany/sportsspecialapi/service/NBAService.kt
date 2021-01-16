package com.bongmany.sportsspecialapi.service

import org.jsoup.Jsoup

class NBAService {

    private val home = "https://www.basketball-reference.com"

    fun runCrawler(): ArrayList<List<String>> {

        val nbaData = arrayListOf<List<String>>()
        val month = listOf(
            listOf("december", "12"), listOf("january", "01"),
            listOf("february", "02"), listOf("march", "03")
        )
        var monthIndex = 0
        val url = "${home}/leagues/NBA_2021_games-${month[monthIndex][0]}.html"

        val doc = Jsoup.connect(url).get()
        val scheduleList = doc.select("#schedule tbody").first().getElementsByTag("tr")

        for (tr in scheduleList) {
            val dateGame = tr.getElementsByAttributeValue("data-stat", "date_game").text()
            val dateForm = "${dateGame.substring(13, 17)}-${month[monthIndex][1]}-${dateGame.substring(9, 11)}"

            val homeTeam = tr.getElementsByAttributeValue("data-stat", "home_team_name").text()
            val awayTeam = tr.getElementsByAttributeValue("data-stat", "visitor_team_name").text()

            val boxScore = tr.getElementsByAttributeValue("data-stat", "box_score_text").first()
                .getElementsByTag("a")
                .attr("href")
            val specialData = getSpecialData(homeTeam, awayTeam, boxScore)

            if (specialData != null) {
                val dbField = listOf(
                    dateForm, homeTeam, awayTeam,
                    "${specialData[0]}(${specialData[1]})", "${specialData[2]}(${specialData[3]})"
                )
                nbaData.add(dbField)
            } else {
                println("$home/$boxScore")
            }
        }

        return nbaData
    }

    private fun getSpecialData(homeTeam: String, awayTeam: String, boxScore: String): List<String>? {
        val url = home + boxScore.replace("/boxscores/", "/boxscores/pbp/")
        val doc = Jsoup.connect(url).get()

        var threePointTeam = ""
        var freeThrowTeam = ""
        var threePointPlayer = ""
        var freeThrowPlayer = ""

        val pbp = doc.select("#pbp tbody").first().getElementsByTag("tr")
        for (tr in pbp) {
            val td = tr.getElementsByTag("td")
            var winner = awayTeam
            td.forEach {
                if (freeThrowPlayer == "" && it.text().contains("makes free throw")) {
                    freeThrowTeam = winner
                    freeThrowPlayer = it.getElementsByTag("a").first().text()
                } else if (threePointPlayer == "" && it.text().contains("makes 3-pt")) {
                    threePointTeam = winner
                    threePointPlayer = it.getElementsByTag("a").first().text()
                } else if (it.className().contains("center")) {
                    winner = homeTeam
                }
                if (freeThrowTeam != "" && threePointTeam != "") {
                    return listOf(threePointTeam, threePointPlayer, freeThrowTeam, freeThrowPlayer)
                }
            }
        }
        return null
    }
}