package com.kkoza.starter.measurements.exception

import com.kkoza.starter.measurements.api.SortType

class InvalidSortTypeException(value: String) : RuntimeException("Sort: $value is not allowed." +
        " Possible options: ${SortType.values().joinToString(separator = ", ", prefix = "[", postfix = "]")}")
