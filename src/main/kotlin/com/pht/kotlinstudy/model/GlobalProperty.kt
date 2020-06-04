package com.pht.kotlinstudy.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*

@Entity
class GlobalProperty(val key: String, val name: String, val value: String) {

    @Id
    @GeneratedValue
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    var type: PropertyType? = null

    var desc: String? = null

    @CreationTimestamp
    var created: Date? = null

    @UpdateTimestamp
    var updated: Date? = null
}
