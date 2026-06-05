package com.example.myaiproject.presentation.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.formatAmount(): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = '.'   // разделитель тысяч — точка
        decimalSeparator = ','    // десятичный разделитель — запятая
    }
    val format = DecimalFormat("#,##0.00", symbols)
    return "${format.format(this)} ₽"
}

fun Double.formatAmountShort(): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    val format = DecimalFormat("#,##0", symbols)
    return "${format.format(this)} ₽"
}