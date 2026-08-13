package com.laskarfkapp.zakathutang.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {

    private val indonesialocale = Locale.forLanguageTag("id", "ID")

    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(indonesialocale)
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    fun formatRupiah(amount: Long): String {
        return formatRupiah(amount.toDouble())
    }

    fun formatDate(timestamp: Long): String {
        if (timestamp <= 0) return "-"
        val sdf = SimpleDateFormat("dd MMM yyyy", indonesialocale)
        return sdf.format(Date(timestamp))
    }

    fun formatDateWithTime(timestamp: Long): String {
        if (timestamp <= 0) return "-"
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", indonesialocale)
        return sdf.format(Date(timestamp))
    }

    fun parseNumberInput(input: String): Double {
        val cleaned = input.replace("[^0-9.]".toRegex(), "")
        return cleaned.toDoubleOrNull() ?: 0.0
    }
}
