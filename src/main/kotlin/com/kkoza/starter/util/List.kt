package com.kkoza.starter.util

fun <T> List<T>.dropIfNotNull(n: Int?): List<T> {
    return if (n != null) this.drop(n) else this
}

fun <T> List<T>.takeIfNotNull(n: Int?): List<T> {
    return if (n != null) this.take(n) else this
}
