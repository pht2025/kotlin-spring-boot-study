package com.pht.kotlinstudy.repository

import com.pht.kotlinstudy.model.Property
import org.springframework.data.repository.CrudRepository

interface PropertyRepository : CrudRepository<Property, Long> {
}
