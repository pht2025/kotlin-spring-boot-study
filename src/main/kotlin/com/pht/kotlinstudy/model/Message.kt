package com.pht.kotlinstudy.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
class Message(val title: String, val message: String) {

    @Id
    @GeneratedValue
    var id: Long? = null

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER)
    var properties: MutableSet<Property> = sortedSetOf()

    @CreationTimestamp
    var created: Date = Date()

    @UpdateTimestamp
    var updated: Date = Date()

    fun addProperty(property: Property) {
        this.properties.plus(property)
        property.message = this
    }
}
