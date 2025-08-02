package com.ozobi

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long, pattern: String): String {
    val date = Date(timestamp) // 将时间戳转换为 Date 对象
    val formatter = SimpleDateFormat(pattern, Locale.getDefault()) // 创建时间格式化器
    return formatter.format(date) // 格式化为字符串
}