package com.pht.kotlinstudy.scheduler

import com.pht.kotlinstudy.repository.GlobalPropertyRepository
import com.pht.kotlinstudy.repository.MessageRepository
import com.pht.kotlinstudy.repository.SendHistoryRepository
import com.pht.kotlinstudy.task.SendMessageTask
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ScheduledFuture

@Service
class ScheduleTaskService(
        val commonScheduler: ThreadPoolTaskScheduler,
        val messageRepository: MessageRepository,
        val globalPropertyRepository: GlobalPropertyRepository,
        val sendHistoryRepository: SendHistoryRepository,
        var environment: Environment
) {
    var scheduleAtFixedRate: ScheduledFuture<*>? = null
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun startSendMessageTask(duration: Duration = Duration.ofSeconds(30)) {
        stopSendMessageTask()
        logger.info("Start send message schedule task : $duration")

        val sendMessageTask = SendMessageTask(
                messageRepository, globalPropertyRepository, sendHistoryRepository,
                environment
        )
        scheduleAtFixedRate = commonScheduler.scheduleAtFixedRate(sendMessageTask, duration)
    }

    fun stopSendMessageTask() {
        logger.info("Stop send message schedule task")
        scheduleAtFixedRate?.cancel(false)
        scheduleAtFixedRate = null
    }

    fun isRunning(): Boolean {
        return scheduleAtFixedRate != null
    }
}
