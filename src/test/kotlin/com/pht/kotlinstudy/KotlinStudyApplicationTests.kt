package com.pht.kotlinstudy

import com.pht.kotlinstudy.repository.MessageRepository
import com.pht.kotlinstudy.repository.PropertyRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SpringBootTest
class KotlinStudyApplicationTests {

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Autowired
    private lateinit var propertyRepository: PropertyRepository

    @Autowired
    private lateinit var restTemplateBuilder: RestTemplateBuilder

    @Test
    fun httpClientTest(): Unit {
        val url = "https://www.naver.com"
        val getMethod = ""
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder().uri(URI.create("${url}/${getMethod}")).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.body())
    }

    @Test
    fun restTemplateTest() {
        val body = restTemplateBuilder.build().getForObject("https://www.naver.com", String::class.java)
        println(body)
    }
}
