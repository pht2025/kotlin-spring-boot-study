package com.pht.kotlinstudy.repository

import com.pht.kotlinstudy.model.SendHistory
import org.springframework.data.repository.CrudRepository

interface SendHistoryRepository : CrudRepository<SendHistory, Long> {

    fun findByToUserIdAndConditionValue(toUserId: String?, conditionValue: String?): SendHistory?
}
