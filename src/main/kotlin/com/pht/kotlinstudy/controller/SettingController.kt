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
        val serverStatus = globalPropertyRepository.findByKey(Global.KEY_SERVER_STATUS).orElse(null)
        val intervalSetting = globalPropertyRepository.findByKey("interval").orElse(GlobalProperty())
        val onOffSetting = globalPropertyRepository.findByKey("onOff").orElse(GlobalProperty())

        if (!scheduleTaskService.isRunning() || interval.toString() != intervalSetting.value) {
            if (onOffSetting.value == GlobalProperty.OnOff.ON.name) {
                scheduleTaskService.startSendMessageTask(Duration.ofSeconds(interval))
            } else {
                scheduleTaskService.stopSendMessageTask()
            }
        }

        if (intervalSetting.id == null) {
            intervalSetting.key = "interval"
            intervalSetting.name = "Interval"
            intervalSetting.value = Objects.toString(interval)
        } else {
            intervalSetting.value = Objects.toString(interval)
        }

        globalPropertyRepository.save(intervalSetting)

        val serverStatusValue = GlobalProperty.ServerStatus.valueOf(serverStatus.value.toString())
        return GlobalSettingDto.Response(true, serverStatusValue)
    }

    @PostMapping("global/onOff")
    fun onOffSetting(@RequestBody request: GlobalSettingDto.Request): GlobalSettingDto.Response {
        val onOff = request.onOff
        val serverStatus = globalPropertyRepository.findByKey(Global.KEY_SERVER_STATUS).orElse(null)
        val intervalSetting = globalPropertyRepository.findByKey("interval").orElse(GlobalProperty())
        val onOffSetting = globalPropertyRepository.findByKey("onOff").orElse(GlobalProperty())

        if (onOffSetting.id == null) {
            onOffSetting.key = "onOff"
            onOffSetting.name = "On Off"
            onOffSetting.value = Objects.toString(onOff)
        } else {
            onOffSetting.value = Objects.toString(onOff)
        }

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
        globalPropertyRepository.save(onOffSetting)

        serverStatus.value = serverStatusValue.toString()
        globalPropertyRepository.save(serverStatus)

        return GlobalSettingDto.Response(true, serverStatusValue)
    }

    @PostMapping("message")
    fun messageSetting(@RequestBody request: MessageDto.Request): MessageDto.Response {
        val messageValue = request.message
        val propertyValue = request.propertyValue
        val propertyType = request.propertyType

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
            message.title = "$propertyType Message"
            message.message = messageValue
            message.addProperty(property)
            messageRepository.save(message)
        } else {
            message.message = messageValue
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
