package com.pht.kotlinstudy.task

import com.pht.kotlinstudy.model.PropertyType
import com.pht.kotlinstudy.repository.MessageRepository
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.util.StopWatch

class SendMessageTask(private val messageRepository: MessageRepository, val restTemplateBuilder: RestTemplateBuilder) : Runnable {

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

        val countMessage = messageRepository.findByKey(PropertyType.COUNT.name)
        val moneyMessage = messageRepository.findByKey(PropertyType.MONEY.name)

        println("send message : ${stopWatch.totalTimeSeconds}s")
        println("URL: $url")
        println("Get method : $getMethod")
        println("Count message : ${countMessage.get().message}")
        println("Count property : ${countMessage.get().properties[0].value}")
        println("Money message : ${moneyMessage.get().message}")
        println("Money property : ${moneyMessage.get().properties[0].value}")

        stopWatch = StopWatch("sendMessage")
        stopWatch.start()
    }
}
