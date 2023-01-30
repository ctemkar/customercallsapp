package com.smartshehar.customercallingv2.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {
    companion object {
        fun getDateFormatted(timeInMillis: Long): String {
            // Creating date format

            // Creating date format
            val simple: DateFormat = SimpleDateFormat("HH:mm dd/MMM/yyyy")
            val result = Date(timeInMillis)

            // Formatting Date according to the
            // given format

            // Formatting Date according to the
            // given format
            return simple.format(result)

        }
    }
}