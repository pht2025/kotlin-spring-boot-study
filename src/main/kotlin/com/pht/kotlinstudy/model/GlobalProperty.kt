package com.pht.kotlinstudy.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
class GlobalProperty {

    @Id
    @GeneratedValue
    var id: Long? = null

    @NotNull
    var key: String? = null

    var name: String? = null

    @NotNull
    @Column(length = 512)
    var value: String? = null

    @Column(length = 1024)
    var desc: String? = null

    @CreationTimestamp
    var created: Date? = null

    @UpdateTimestamp
    var updated: Date? = null

    enum class OnOff {
        ON, OFF
    }

    enum class ServerStatus {
        RUNNING, STOP
    }
}
