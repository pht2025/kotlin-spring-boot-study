package com.pht.kotlinstudy

import com.pht.kotlinstudy.model.Message
import com.pht.kotlinstudy.model.Property
import com.pht.kotlinstudy.repository.MessageRepository
import com.pht.kotlinstudy.repository.PropertyRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

    @Test
    fun contextLoads() {
        val message = Message("테스트 메세지", "테스트 메세지")
        messageRepository.save(message)

        val property = Property("total_count", "횟수", "1")
        message.addProperty(property)

        propertyRepository.save(property)

        assertThat(message.id).isNotEqualTo(null)
        val savedMessage = messageRepository.findById(message.id!!)
        if (savedMessage.isPresent)
            assertThat(savedMessage.get().properties.size).isEqualTo(1)
    }

    @Test
    fun httpClientTest() :Unit {
        val url = "https://www.naver.com"
        val getMethod = ""
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder().uri(URI.create("${url}/${getMethod}")).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.body())
    }
}
