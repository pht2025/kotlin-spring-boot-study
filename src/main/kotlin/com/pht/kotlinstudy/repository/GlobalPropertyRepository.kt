package com.pht.kotlinstudy.repository

import com.pht.kotlinstudy.model.GlobalProperty
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GlobalPropertyRepository : CrudRepository<GlobalProperty, Long> {

    fun findByKey(key: String): Optional<GlobalProperty>
}
