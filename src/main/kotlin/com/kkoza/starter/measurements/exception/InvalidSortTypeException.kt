package com.kkoza.starter.measurements.exception

import com.kkoza.starter.measurements.api.MeasurementSortType

class InvalidSortTypeException(value: String) : RuntimeException("Sort: $value is not allowed." +
        " Possible options: ${MeasurementSortType.values().joinToString(separator = ", ", prefix = "[", postfix = "]")}")
