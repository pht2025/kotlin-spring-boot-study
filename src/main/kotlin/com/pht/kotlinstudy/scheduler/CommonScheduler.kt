package com.pht.kotlinstudy.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class CommonScheduler {

    private val url: String = "https://api.adventurer.co.kr"
    private val getMethod: String = "bettings"
    private val sendMethod: String = "sendMessages"

    @Scheduled(fixedDelayString = "60", fixedRateString = "60")
    fun getSchedule(): Unit {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder().uri(URI.create("${url}/${getMethod}")).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.body())
    }
}
