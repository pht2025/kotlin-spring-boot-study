package com.pht.kotlinstudy.model.dto

import com.pht.kotlinstudy.model.GlobalProperty

class GlobalSettingDto {
    data class Request(val interval: Long, val onOff: GlobalProperty.OnOff = GlobalProperty.OnOff.OFF)
    data class Response(val result: Boolean, val serverStatus: GlobalProperty.ServerStatus)
}
