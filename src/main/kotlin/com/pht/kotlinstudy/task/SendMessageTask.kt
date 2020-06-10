package com.pht.kotlinstudy.task

import org.springframework.util.StopWatch

class SendMessageTask : Runnable {

    private val url: String = "https://api.adventurer.co.kr"
    private val getMethod: String = "bettings"
    private val sendMethod: String = "sendMessages"

    private var stopWatch: StopWatch

    init {
        stopWatch = StopWatch("sendMessage")
        stopWatch.start()
    }

    override fun run() {
        if (stopWatch.isRunning) {
            stopWatch.stop()
        }

        println("send message : ${stopWatch.totalTimeSeconds}s")
        println("URL: $url")
        println("Get method : $getMethod")

        stopWatch = StopWatch("sendMessage")
        stopWatch.start()
    }
}
