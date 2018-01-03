package com.kkoza.starter.handles.exception

class InvalidPagingParameterException(paramName: String) : RuntimeException("$paramName can't be less than 0")
