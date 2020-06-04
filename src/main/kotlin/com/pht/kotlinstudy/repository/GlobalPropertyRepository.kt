package com.pht.kotlinstudy.repository

import com.pht.kotlinstudy.model.GlobalProperty
import org.springframework.data.repository.CrudRepository

interface GlobalPropertyRepository: CrudRepository<GlobalProperty, Long> {
}
