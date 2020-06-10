package com.pht.kotlinstudy.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
class Property(val key: String, val name: String, val value: String) {

    @Id
    @GeneratedValue
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    var type: PropertyType? = null

    var desc: String? = null

    @ManyToOne(fetch = FetchType.EAGER)
    var message: Message? = null

    @CreationTimestamp
    var created: Date? = null

    @UpdateTimestamp
    var updated: Date? = null
}
