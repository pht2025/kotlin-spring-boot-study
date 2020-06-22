package com.pht.kotlinstudy.controller

import com.pht.kotlinstudy.Global
import com.pht.kotlinstudy.model.GlobalProperty
import com.pht.kotlinstudy.model.Message
import com.pht.kotlinstudy.model.Property
import com.pht.kotlinstudy.model.dto.GlobalSettingDto
import com.pht.kotlinstudy.model.dto.MessageDto
import com.pht.kotlinstudy.repository.GlobalPropertyRepository
import com.pht.kotlinstudy.repository.MessageRepository
import com.pht.kotlinstudy.repository.PropertyRepository
import com.pht.kotlinstudy.scheduler.ScheduleTaskService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.util.*

@RestController
@RequestMapping("setting")
class SettingController(
        val globalPropertyRepository: GlobalPropertyRepository,
        val messageRepository: MessageRepository,
        val propertyRepository: PropertyRepository,
        val scheduleTaskService: ScheduleTaskService
) {

    @PostMapping("global/interval")
    fun intervalSetting(@RequestBody request: GlobalSettingDto.Request): GlobalSettingDto.Response {
        var interval = request.interval
        if (interval < 10) {
            interval = 30
        }
        val intervalSetting = globalPropertyRepository.findByKey(Global.KEY_INTERVAL).orElse(GlobalProperty())

        if (scheduleTaskService.isRunning() && interval.toString() != intervalSetting.value) {
            scheduleTaskService.stopSendMessageTask()
            scheduleTaskService.startSendMessageTask(Duration.ofSeconds(interval))
        }

        if (intervalSetting.id == null) {
            intervalSetting.key = Global.KEY_INTERVAL
            intervalSetting.name = Global.KEY_INTERVAL
            intervalSetting.value = Objects.toString(interval)
        } else {
            intervalSetting.value = Objects.toString(interval)
        }

        globalPropertyRepository.save(intervalSetting)

        val onOff: GlobalProperty.OnOff
        val serverStatus: GlobalProperty.ServerStatus;
        if (scheduleTaskService.isRunning()) {
            serverStatus = GlobalProperty.ServerStatus.RUNNING
            onOff = GlobalProperty.OnOff.ON
        } else {
            serverStatus = GlobalProperty.ServerStatus.STOP
            onOff = GlobalProperty.OnOff.OFF
        }

        return GlobalSettingDto.Response(true, serverStatus, onOff)
    }

    @PostMapping("global/onOff")
    fun onOffSetting(@RequestBody request: GlobalSettingDto.Request): GlobalSettingDto.Response {
        val onOff = request.onOff
        val intervalSetting = globalPropertyRepository.findByKey(Global.KEY_INTERVAL).orElse(GlobalProperty())

        val serverStatusValue = if (onOff == GlobalProperty.OnOff.ON) {
            if (!scheduleTaskService.isRunning()) {
                val interval = if (intervalSetting == null) {
                    60L
                } else {
                    intervalSetting.value?.toLong()
                }
                scheduleTaskService.startSendMessageTask(Duration.ofSeconds(interval as Long))
            }
            GlobalProperty.ServerStatus.RUNNING
        } else {
            scheduleTaskService.stopSendMessageTask()
            GlobalProperty.ServerStatus.STOP
        }

        globalPropertyRepository.save(intervalSetting)

        return GlobalSettingDto.Response(true, serverStatusValue, onOff)
    }

    @PostMapping("global/userInfo")
    fun userInfoSetting(@RequestBody request: GlobalSettingDto.Request): GlobalSettingDto.Response {
        val senderId = request.senderId
        val token = request.token;


        val senderIdProperty = globalPropertyRepository.findByKey(Global.KEY_SENDER_ID).orElse(GlobalProperty())
        val tokenProperty = globalPropertyRepository.findByKey(Global.KEY_ACCESS_TOKEN).orElse(GlobalProperty())

        if (senderIdProperty.id == null) {
            senderIdProperty.key = Global.KEY_SENDER_ID
            senderIdProperty.name = Global.KEY_SENDER_ID
            senderIdProperty.value = senderId
        } else {
            senderIdProperty.value = senderId
        }

        globalPropertyRepository.save(senderIdProperty)

        if (tokenProperty.id == null) {
            tokenProperty.key = Global.KEY_ACCESS_TOKEN
            tokenProperty.name = Global.KEY_ACCESS_TOKEN
            tokenProperty.value = token
        } else {
            tokenProperty.value = token
        }

        globalPropertyRepository.save(tokenProperty)

        val onOff: GlobalProperty.OnOff
        val serverStatus: GlobalProperty.ServerStatus;
        if (scheduleTaskService.isRunning()) {
            serverStatus = GlobalProperty.ServerStatus.RUNNING
            onOff = GlobalProperty.OnOff.ON
        } else {
            serverStatus = GlobalProperty.ServerStatus.STOP
            onOff = GlobalProperty.OnOff.OFF
        }

        return GlobalSettingDto.Response(true, serverStatus, onOff)
    }

    @PostMapping("message")
    fun messageSetting(@RequestBody request: MessageDto.Request): MessageDto.Response {
        val messageValue = request.message
        val propertyValue = request.propertyValue
        val propertyType = request.propertyType
        val title = request.title

        println("message : $messageValue")
        println("propertyValue: $propertyValue")
        println("propertyType : $propertyType")

        val message = messageRepository.findByKey(Objects.toString(propertyType)).orElse(Message())
        if (message.id == null) {
            val property = Property()
            property.key = Objects.toString(propertyType)
            property.type = propertyType
            property.value = propertyValue
            propertyRepository.save(property)

            message.key = Objects.toString(propertyType)
            message.title = title
            message.message = messageValue
            message.addProperty(property)
            messageRepository.save(message)
        } else {
            message.message = messageValue
            message.title = title
            message.properties.forEach { property: Property ->
                run {
                    property.value = propertyValue
                    property.type = propertyType
                }
            }
            messageRepository.save(message)
        }

        return MessageDto.Response(true)
    }
}
