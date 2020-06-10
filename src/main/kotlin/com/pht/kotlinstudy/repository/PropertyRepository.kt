package com.pht.kotlinstudy.repository

import com.pht.kotlinstudy.model.Property
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PropertyRepository : CrudRepository<Property, Long> {
    fun findByKey(key: String): Optional<Property>
}
