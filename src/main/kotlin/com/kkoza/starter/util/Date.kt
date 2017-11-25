package com.kkoza.starter.util

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

private val dayFormat = DateTimeFormat.forPattern("dd-MM-yyyy")
private val hourFormat = DateTimeFormat.forPattern("HH:mm")

fun dayFormat(date: DateTime) = dayFormat.print(date)

fun hourFormat(date: DateTime) = hourFormat.print(date)


