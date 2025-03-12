package com.stardust.autojs.runtime

import java.util.Date

object DeviceAdminReceiverMsg {
    var isEnabled = false
    var lastPasswordSucceededDate: Date? = null
    var lastPasswordFailedDate: Date? = null
    var lastPasswordChangedDate: Date? = null
}