package com.pht.kotlinstudy.listener

import com.pht.kotlinstudy.Global
import com.pht.kotlinstudy.model.GlobalProperty
import com.pht.kotlinstudy.model.Message
import com.pht.kotlinstudy.model.Property
import com.pht.kotlinstudy.model.PropertyType
import com.pht.kotlinstudy.repository.GlobalPropertyRepository
import com.pht.kotlinstudy.repository.MessageRepository
import com.pht.kotlinstudy.scheduler.ScheduleTaskService
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ApplicationEventListener(
        val globalPropertyRepository: GlobalPropertyRepository,
        val messageRepository: MessageRepository,
        val scheduleTaskService: ScheduleTaskService
) : ApplicationListener<ApplicationStartedEvent> {

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        var interval = globalPropertyRepository.findByKey(Global.KEY_INTERVAL).orElse(null)
        var countMessage = messageRepository.findByKey(PropertyType.COUNT.name).orElse(null)
        var moneyMessage = messageRepository.findByKey(PropertyType.MONEY.name).orElse(null)

        if (interval == null) {
            val intervalProperty = GlobalProperty()
            intervalProperty.key = Global.KEY_INTERVAL
            intervalProperty.name = Global.KEY_INTERVAL
            intervalProperty.value = "60"
            interval = globalPropertyRepository.save(intervalProperty)
        }

        if (countMessage == null) {
            val property = Property()
            property.key = PropertyType.COUNT.name
            property.name = PropertyType.COUNT.name
            property.value = "1,5,10"
            property.type = PropertyType.COUNT

            countMessage = Message()
            countMessage.key = PropertyType.COUNT.name
            countMessage.title = "${PropertyType.COUNT} Message"
            countMessage.message = "COUNT Message"
            countMessage.addProperty(property)
            messageRepository.save(countMessage)
        }
        if (moneyMessage == null) {
            val property = Property()
            property.key = PropertyType.MONEY.name
            property.name = PropertyType.MONEY.name
            property.value = "60000,200000"
            property.type = PropertyType.MONEY

            moneyMessage = Message()
            moneyMessage.key = PropertyType.MONEY.name
            moneyMessage.title = "${PropertyType.MONEY} Message"
            moneyMessage.message = "MONEY message"
            moneyMessage.addProperty(property)
            messageRepository.save(moneyMessage)
        }

        scheduleTaskService.startSendMessageTask(Duration.ofSeconds(interval.value?.toLong()!!))
    }
}
