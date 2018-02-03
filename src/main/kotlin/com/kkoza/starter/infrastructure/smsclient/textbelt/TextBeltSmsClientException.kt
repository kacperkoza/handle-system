package com.kkoza.starter.infrastructure.smsclient.textbelt

class TextBeltSmsClientException(sms: Sms, ex: Exception) : RuntimeException("Cant sen't = $sms", ex)