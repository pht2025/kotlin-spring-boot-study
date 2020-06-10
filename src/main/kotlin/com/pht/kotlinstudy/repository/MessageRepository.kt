package com.pht.kotlinstudy.repository

import com.pht.kotlinstudy.model.Message
import org.springframework.data.repository.CrudRepository
import java.util.*

interface MessageRepository : CrudRepository<Message, Long> {
    fun findByKey(key: String): Optional<Message>
}
