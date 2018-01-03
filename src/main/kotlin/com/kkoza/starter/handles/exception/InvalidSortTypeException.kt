package com.kkoza.starter.handles.exception

class InvalidSortTypeException(value: String, validValues: String) : RuntimeException("Sort: $value is not allowed." +
        " Possible options: $validValues}")
