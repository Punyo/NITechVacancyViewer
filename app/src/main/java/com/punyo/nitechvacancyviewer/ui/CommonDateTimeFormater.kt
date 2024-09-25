package com.punyo.nitechvacancyviewer.ui

import java.time.format.DateTimeFormatter

object CommonDateTimeFormater {
    val formatter: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern("HH:mm")
}