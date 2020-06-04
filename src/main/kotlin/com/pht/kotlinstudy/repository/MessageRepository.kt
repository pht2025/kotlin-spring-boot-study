package com.pht.kotlinstudy.repository

import com.pht.kotlinstudy.model.Message
import org.springframework.data.repository.CrudRepository

interface MessageRepository : CrudRepository<Message, Long> {
}
