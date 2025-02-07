package com.jetbrains.kmpapp.utils

import kotlin.math.pow

fun Float.format(digits: Int): String {
    val factor = 10.0.pow(digits.toDouble())
    return (kotlin.math.round(this * factor) / factor).toString()
}

fun Int.format(digits: Int): String {
    return this.toString().padStart(digits, '0')
}