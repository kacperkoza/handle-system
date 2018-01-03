package com.kkoza.starter.handles.exception

class IllegalQueryDateException : RuntimeException("StartDate can't be after EndDate")