package com.pht.kotlinstudy.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
class Message {

    @Id
    @GeneratedValue
    var id: Long? = null

    @NotNull
    var key: String? = null

    @NotNull
    var title: String? = null

    @NotNull
    @Column(length = 1024)
    var message: String? = null

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var properties: MutableList<Property> = arrayListOf()

    @CreationTimestamp
    var created: Date = Date()

    @UpdateTimestamp
    var updated: Date = Date()

    fun addProperty(property: Property) {
        this.properties.add(property)
        property.message = this
    }
}
