package com.kkoza.starter.measurements.exception

class InvalidPagingParameterException(paramName: String) : RuntimeException("$paramName can't be less than 0")
