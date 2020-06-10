package com.pht.kotlinstudy.model.dto

import com.pht.kotlinstudy.model.PropertyType

class MessageDto {

    data class Request(val messageId: Long, val message: String, val propertyValue: String, val propertyType: PropertyType)

    data class Response(val result: Boolean)
}
