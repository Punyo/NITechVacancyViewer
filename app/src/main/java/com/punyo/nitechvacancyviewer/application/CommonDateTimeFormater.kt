package com.punyo.nitechvacancyviewer.application

import java.time.format.DateTimeFormatter

object CommonDateTimeFormater {
    val formatter: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern("HH:mm")
}
