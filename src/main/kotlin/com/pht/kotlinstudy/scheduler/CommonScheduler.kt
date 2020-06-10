package com.pht.kotlinstudy.scheduler

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component

@Component
class CommonScheduler : ThreadPoolTaskScheduler() {
}
