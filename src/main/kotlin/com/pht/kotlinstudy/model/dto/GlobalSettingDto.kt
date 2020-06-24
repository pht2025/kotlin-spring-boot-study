package com.pht.kotlinstudy.model.dto

import com.pht.kotlinstudy.model.GlobalProperty

class GlobalSettingDto {
    data class Request(val day: Int, val interval: Long, val onOff: GlobalProperty.OnOff = GlobalProperty.OnOff.OFF, val senderId: String, val token: String)
    data class Response(val result: Boolean, val serverStatus: GlobalProperty.ServerStatus, val onOff: GlobalProperty.OnOff)
}
