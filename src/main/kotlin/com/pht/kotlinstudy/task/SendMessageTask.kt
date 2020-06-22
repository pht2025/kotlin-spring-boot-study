package com.pht.kotlinstudy.task

import com.fasterxml.jackson.databind.ObjectMapper
import com.pht.kotlinstudy.Global
import com.pht.kotlinstudy.model.*
import com.pht.kotlinstudy.repository.GlobalPropertyRepository
import com.pht.kotlinstudy.repository.MessageRepository
import com.pht.kotlinstudy.repository.SendHistoryRepository
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.StopWatch
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Consumer

class SendMessageTask(
        private val messageRepository: MessageRepository,
        private val globalPropertyRepository: GlobalPropertyRepository,
        private val sendHistoryRepository: SendHistoryRepository,
        private var environment: Environment
) : Runnable {

    private val testBaseUrl: String = "http://localhost:8088"
    private val prodBaseUrl: String = "https://v2-api.adventurer.co.kr"
    private val baseUrl: String
    private val getMethod: String = "/user/rx/all/count/createdAt"
    private val sendMethod: String = "/message/send"
    private val objectMapper = ObjectMapper()

    private var stopWatch: StopWatch

    init {
        baseUrl = if (environment.activeProfiles.contains("prod")) {
            prodBaseUrl
        } else {
            testBaseUrl
        }
        stopWatch = StopWatch("sendMessage")
        stopWatch.start()
    }

    private fun sendMessage(token: String?, fromUserId: String?, toUserId: String?, title: String?, content: String?, type: PropertyType?, value: String?) {
        val findByToUserIdAndConditionValue = sendHistoryRepository.findByToUserIdAndConditionValue(toUserId, value)
        if (findByToUserIdAndConditionValue?.id == null) {
            val sendHistory = SendHistory()
            sendHistory.fromUserId = fromUserId
            sendHistory.toUserId = toUserId
            sendHistory.messageTitle = title
            sendHistory.messageContent = content
            sendHistory.conditionType = type
            sendHistory.conditionValue = value
            sendHistoryRepository.save(sendHistory)

            val formData = HashMap<String, Any?>()
            formData["fromUserId"] = fromUserId
            formData["toUserId"] = toUserId
            formData["messageTitle"] = title
            formData["messageContent"] = content

            val webClient = WebClient.create("${baseUrl}${sendMethod}")
            webClient.post()
                    .header("advAccessTokenId", fromUserId)
                    .header("advAccessTokenKey", token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(formData))
                    .retrieve().bodyToMono(String::class.java).log()
                    .subscribe()
        }
    }

    override fun run() {
        if (stopWatch.isRunning) {
            stopWatch.stop()
        }

        val countMessage = messageRepository.findByKey(PropertyType.COUNT.name).orElse(Message())
        val moneyMessage = messageRepository.findByKey(PropertyType.MONEY.name).orElse(Message())
        val senderId = globalPropertyRepository.findByKey(Global.KEY_SENDER_ID).orElse(GlobalProperty())
        val token = globalPropertyRepository.findByKey(Global.KEY_ACCESS_TOKEN).orElse(GlobalProperty())
        val dayProperty = globalPropertyRepository.findByKey(Global.KEY_DAY_BEFORE).orElse(null)

        val minusDay = if (dayProperty == null || dayProperty.value == "") {
            10
        } else {
            dayProperty.value?.toLong()
        }

        val countProperty = countMessage.properties[0]
        val moneyProperty = moneyMessage.properties[0]

        val today = LocalDateTime.now(ZoneOffset.UTC);
        val beforeDay = today.minusDays(minusDay!!)
        val createdAt = beforeDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))

        val url = "${baseUrl}${getMethod}?counts=${countProperty.value}&createdAt=${createdAt}"
        println("Parsed url : $url")
        val webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("advAccessTokenId", senderId.value)
                .defaultHeader("advAccessTokenKey", token.value)
                .build()
        webClient.get()
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve().bodyToFlux(BetCount::class.java)
                .log()
                .subscribe(Consumer { betCount -> sendMessage(token.value, senderId.value, betCount.userId, countMessage.title, countMessage.message, PropertyType.COUNT, Objects.toString(betCount.count)) })

        println("send message : ${stopWatch.totalTimeSeconds}s")
        println("Get method : $getMethod")
        println("Count message : ${countMessage.message}")
        println("Count message title : ${countMessage.title}")
        println("Count property : ${countMessage.properties[0].value}")

        println("Count message title : ${moneyMessage.title}")
        println("Money message : ${moneyMessage.message}")
        println("Money property : ${moneyMessage.properties[0].value}")

        stopWatch = StopWatch("sendMessage")
        stopWatch.start()
    }
}
