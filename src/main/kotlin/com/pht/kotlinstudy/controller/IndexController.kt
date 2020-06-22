package com.pht.kotlinstudy.controller

import com.pht.kotlinstudy.Global
import com.pht.kotlinstudy.model.GlobalProperty
import com.pht.kotlinstudy.model.Message
import com.pht.kotlinstudy.model.Property
import com.pht.kotlinstudy.model.PropertyType
import com.pht.kotlinstudy.repository.GlobalPropertyRepository
import com.pht.kotlinstudy.repository.MessageRepository
import com.pht.kotlinstudy.scheduler.ScheduleTaskService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import reactor.core.publisher.Mono

@Controller
class IndexController(
        val messageRepository: MessageRepository,
        val globalPropertyRepository: GlobalPropertyRepository,
        val scheduleTaskService: ScheduleTaskService
) {

    @GetMapping("/")
    fun index(modelAndView: ModelAndView): Mono<ModelAndView> {
        val interval = globalPropertyRepository.findByKey(Global.KEY_INTERVAL).orElse(GlobalProperty())
        val senderId = globalPropertyRepository.findByKey(Global.KEY_SENDER_ID).orElse(GlobalProperty())
        val accessToken = globalPropertyRepository.findByKey(Global.KEY_ACCESS_TOKEN).orElse(GlobalProperty())
        val dayBefore = globalPropertyRepository.findByKey(Global.KEY_DAY_BEFORE).orElse(GlobalProperty())
        val countMessage = messageRepository.findByKey(PropertyType.COUNT.name).orElse(Message())
        val moneyMessage = messageRepository.findByKey(PropertyType.MONEY.name).orElse(Message())
        val countProperty = if (countMessage.properties.isEmpty()) {
            Property()
        } else {
            countMessage.properties[0]
        }
        val moneyProperty = if (moneyMessage.properties.isEmpty()) {
            Property()
        } else {
            moneyMessage.properties[0]
        }

        modelAndView.viewName = "index"

        val onOff: GlobalProperty.OnOff
        val serverStatus: GlobalProperty.ServerStatus;
        if (scheduleTaskService.isRunning()) {
            serverStatus = GlobalProperty.ServerStatus.RUNNING
            onOff = GlobalProperty.OnOff.ON
        } else {
            serverStatus = GlobalProperty.ServerStatus.STOP
            onOff = GlobalProperty.OnOff.OFF
        }

        modelAndView.addObject("serverStatus", serverStatus.name)
        modelAndView.addObject("interval", interval.value)
        modelAndView.addObject("onOff", onOff.name)
        modelAndView.addObject("senderId", senderId.value)
        modelAndView.addObject("accessToken", accessToken.value)
        modelAndView.addObject("dayBefore", dayBefore.value)

        modelAndView.addObject("countMessageId", countMessage.id)
        modelAndView.addObject("moneyMessageId", moneyMessage.id)

        modelAndView.addObject("countMessageTitle", countMessage.title)
        modelAndView.addObject("moneyMessageTitle", moneyMessage.title)

        modelAndView.addObject("countMessage", countMessage.message)
        modelAndView.addObject("moneyMessage", moneyMessage.message)

        modelAndView.addObject("countProperty", countProperty.value)
        modelAndView.addObject("moneyProperty", moneyProperty.value)
        return Mono.just(modelAndView)
    }
}
