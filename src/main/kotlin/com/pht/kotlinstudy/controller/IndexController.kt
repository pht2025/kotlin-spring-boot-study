package com.pht.kotlinstudy.controller

import com.pht.kotlinstudy.Global
import com.pht.kotlinstudy.model.Message
import com.pht.kotlinstudy.model.Property
import com.pht.kotlinstudy.model.PropertyType
import com.pht.kotlinstudy.repository.GlobalPropertyRepository
import com.pht.kotlinstudy.repository.MessageRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import reactor.core.publisher.Mono

@Controller
class IndexController(
        val messageRepository: MessageRepository,
        val globalPropertyRepository: GlobalPropertyRepository
) {

    @GetMapping("/")
    fun index(modelAndView: ModelAndView): Mono<ModelAndView> {
        val interval = globalPropertyRepository.findByKey(Global.KEY_INTERVAL).orElse(null)
        val serverStatus = globalPropertyRepository.findByKey(Global.KEY_SERVER_STATUS).orElse(null)
        val onOff = globalPropertyRepository.findByKey(Global.KEY_ON_OFF).orElse(null)
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
        modelAndView.addObject("serverStatus", serverStatus.value)
        modelAndView.addObject("interval", interval.value)
        modelAndView.addObject("onOff", onOff.value)
        modelAndView.addObject("countMessageId", countMessage.id)
        modelAndView.addObject("moneyMessageId", moneyMessage.id)

        modelAndView.addObject("countMessage", countMessage.message)
        modelAndView.addObject("moneyMessage", moneyMessage.message)

        modelAndView.addObject("countProperty", countProperty.value)
        modelAndView.addObject("moneyProperty", moneyProperty.value)
        return Mono.just(modelAndView)
    }
}
