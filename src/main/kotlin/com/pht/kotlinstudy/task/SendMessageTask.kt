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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SendMessageTask(
        private val messageRepository: MessageRepository,
        private val globalPropertyRepository: GlobalPropertyRepository,
        private val sendHistoryRepository: SendHistoryRepository,
        private var environment: Environment
) : Runnable {

    private val testBaseUrl: String = "http://localhost:8088"
    private val prodBaseUrl: String = "https://v2-api.adventurer.co.kr"
    private val baseUrl: String
    private val getCountMethod: String = "/api/user/betCounts"
    private val getNewUserMethod: String = "/api/user/newUsers"
    private val sendMethod: String = "/api/message/send/many"
    private val objectMapper = ObjectMapper()
    private val sendMessageQueue = mutableListOf<Map<String, Any?>>()
    private val historyMap = mutableMapOf<String, SendHistory>()

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
        if (findByToUserIdAndConditionValue?.id == null && (sendMessageQueue.isEmpty() || sendMessageQueue.size < 50)) {
            val sendHistory = SendHistory()
            sendHistory.fromUserId = fromUserId
            sendHistory.toUserId = toUserId
            sendHistory.messageTitle = title
            sendHistory.messageContent = content
            sendHistory.conditionType = type
            sendHistory.conditionValue = value
            historyMap["${fromUserId}${toUserId}${value}"] = sendHistory
            sendHistoryRepository.save(sendHistory)

            val formData = HashMap<String, Any?>()
            formData["fromUserId"] = fromUserId
            formData["toUserId"] = toUserId
            formData["messageTitle"] = title
            formData["messageContent"] = content

            sendMessageQueue.add(formData)
        }
    }

    override fun run() {
        if (stopWatch.isRunning) {
            stopWatch.stop()
        }

        val countMessage = messageRepository.findByKey(PropertyType.COUNT.name).orElse(Message())
        val moneyMessage = messageRepository.findByKey(PropertyType.MONEY.name).orElse(Message())
        val newUserMessage = messageRepository.findByKey(PropertyType.NEW_USER.name).orElse(Message())
        val senderId = globalPropertyRepository.findByKey(Global.KEY_SENDER_ID).orElse(GlobalProperty())
        val token = globalPropertyRepository.findByKey(Global.KEY_ACCESS_TOKEN).orElse(GlobalProperty())
        val dayProperty = globalPropertyRepository.findByKey(Global.KEY_DAY_BEFORE).orElse(null)

        val minusDay = if (dayProperty == null || dayProperty.value == "") {
            10
        } else {
            dayProperty.value?.toLong()
        }

        val senderIdValue = if (environment.activeProfiles.contains("prod")) {
            senderId.value
        } else {
            "06c39309-62b3-4d7c-bc92-ad4d821b79ea"
        }

        if (sendMessageQueue.isNotEmpty()) {
            val webClient = WebClient.create("${baseUrl}${sendMethod}")
            webClient.post()
                    .header("advAccessTokenId", senderIdValue)
                    .header("advAccessTokenKey", token.value)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(sendMessageQueue))
                    .retrieve().bodyToMono(List::class.java)
                    .log()
                    .subscribe()
            sendMessageQueue.clear()
        }

        val countProperty = countMessage.properties[0]
        val moneyProperty = moneyMessage.properties[0]
        val newUserProperty = newUserMessage.properties[0]

        val today = LocalDate.now()
        val beforeDay = today.minusDays(minusDay!!)

        sendCountMessage(countProperty, beforeDay, senderIdValue, token, countMessage)
        sendNewMemberMessage(newUserProperty, today, beforeDay, senderIdValue, token, newUserMessage)

        stopWatch = StopWatch("sendMessage")
        stopWatch.start()
    }

    private fun sendCountMessage(
        countProperty: Property,
        beforeDay: LocalDate,
        senderIdValue: String?,
        token: GlobalProperty,
        countMessage: Message
    ) {
        val createdAt = beforeDay.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        val url = "${baseUrl}${getCountMethod}/${countProperty.value}/${createdAt}"
        println("Parsed url : $url")
        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("advAccessTokenId", senderIdValue)
            .defaultHeader("advAccessTokenKey", token.value)
            .build()
        webClient.get()
            .accept(MediaType.APPLICATION_JSON)
            .retrieve().bodyToFlux(BetCount::class.java)
            .subscribe { betCount ->
                sendMessage(
                    token.value,
                    senderIdValue,
                    betCount.userId,
                    countMessage.title,
                    countMessage.message,
                    PropertyType.COUNT,
                    Objects.toString(betCount.count)
                )
            }

        println("send message : ${stopWatch.totalTimeSeconds}s")
        println("Get method : $getCountMethod")
        println("Count message : ${countMessage.message}")
        println("Count message title : ${countMessage.title}")
        println("Count property : ${countMessage.properties[0].value}")
    }

    private fun sendNewMemberMessage(
        newUserProperty: Property,
        today: LocalDate,
        beforeDay: LocalDate,
        senderIdValue: String?,
        token: GlobalProperty,
        newUserMessage: Message
    ) {
        val createdAt = if (newUserProperty.value.toBoolean()) { // only today
            today.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        } else {
            beforeDay.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        }
        val url = "${baseUrl}${getNewUserMethod}/${createdAt}"
        println("Parsed url : $url")
        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("advAccessTokenId", senderIdValue)
            .defaultHeader("advAccessTokenKey", token.value)
            .build()
        webClient.get()
            .accept(MediaType.APPLICATION_JSON)
            .retrieve().bodyToFlux(User::class.java)
            .subscribe { user ->
                sendMessage(
                    token.value,
                    senderIdValue,
                    user.id,
                    newUserMessage.title,
                    newUserMessage.message,
                    PropertyType.NEW_USER,
                    user.nick,
                )
            }

        println("New user send message : ${stopWatch.totalTimeSeconds}s")
        println("New user Get method : $getCountMethod")
        println("New user message : ${newUserMessage.message}")
        println("New user message title : ${newUserMessage.title}")
        println("New user property : ${newUserMessage.properties[0].value}")
    }
}
