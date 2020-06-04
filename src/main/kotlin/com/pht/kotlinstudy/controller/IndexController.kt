package com.pht.kotlinstudy.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import reactor.core.publisher.Mono

@Controller
class IndexController {

    @GetMapping("/")
    fun index(): Mono<String> {
        return Mono.just("index")
    }

    @PostMapping("/save/message")
    fun saveMessage() {}
}
