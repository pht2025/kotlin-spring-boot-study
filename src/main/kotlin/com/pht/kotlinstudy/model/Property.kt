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
class Property() {

    @Id
    @GeneratedValue
    var id: Long? = null

    @NotNull
    var key: String? = null

    var name: String? = null

    @NotNull
    @Column(length = 1024)
    var value: String? = null

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
