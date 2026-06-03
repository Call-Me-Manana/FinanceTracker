package com.example.financetracker.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()

    val startOfToday = getStartOfDay(now)
    val startOfYesterday = startOfToday - 24 * 60 * 60 * 1000

    return when {
        timestamp >= startOfToday -> "Сегодня"
        timestamp >= startOfYesterday -> "Вчера"
        else -> {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

fun getStartOfDay(time: Long): Long {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = time
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}