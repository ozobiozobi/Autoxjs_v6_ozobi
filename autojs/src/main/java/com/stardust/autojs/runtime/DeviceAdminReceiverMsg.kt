package com.stardust.autojs.runtime

import java.util.Date
// Created by ozobi - 2024/11/10
object DeviceAdminReceiverMsg {
    var isEnabled = false
    var lastPasswordSucceededDate: Date? = null
    var lastPasswordFailedDate: Date? = null
    var lastPasswordChangedDate: Date? = null
}