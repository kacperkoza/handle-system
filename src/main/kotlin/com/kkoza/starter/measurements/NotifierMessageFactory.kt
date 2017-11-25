package com.kkoza.starter.measurements

import com.kkoza.starter.util.dayFormat
import com.kkoza.starter.util.hourFormat
import org.joda.time.DateTime

class NotifierMessageFactory {

    fun fire(date: DateTime): String = "W twoim mieszkaniu pojawił się ogień " +
            "${dayFormat(date)} o godzinie ${hourFormat(date)}"

    fun burglar(date: DateTime): String = "Ktoś próbuje się włamać do Twojego mieszkania " +
            "${dayFormat(date)} o godzinie ${hourFormat(date)}"

    fun frost(date: DateTime, temperature: Temperature): String = "Temperatura w Twoim mieszkaniu drastycznie się zmieniła." +
            " Wynosi ${temperature.value} ${temperature.unit} o godzinie ${hourFormat(date)}."

}