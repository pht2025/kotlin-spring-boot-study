package com.pht.kotlinstudy.controller

import com.pht.kotlinstudy.model.Message
import com.pht.kotlinstudy.repository.MessageRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("message")
class MessageController(private val messageRepository: MessageRepository) {

    @GetMapping("all")
    fun getAll(): Flux<Message> {
        return Flux.fromIterable(messageRepository.findAll())
    }
}
