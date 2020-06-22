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
class SendHistory {

    @Id
    @GeneratedValue
    var id: Long? = null

    @NotNull
    var fromUserId: String? = null

    @NotNull
    var toUserId: String? = null

    var messageTitle: String? = null

    @NotNull
    @Column(length = 1024)
    var messageContent: String? = null

    @NotNull
    @Enumerated(EnumType.STRING)
    var conditionType: PropertyType? = null

    @NotNull
    var conditionValue: String? = null

    @CreationTimestamp
    var created: Date? = null

    @UpdateTimestamp
    var updated: Date? = null
}
