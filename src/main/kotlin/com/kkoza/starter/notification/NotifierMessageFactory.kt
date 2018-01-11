package com.kkoza.starter.notification

import com.kkoza.starter.handles.HandleMeasurementDocument
import com.kkoza.starter.util.dayFormat
import com.kkoza.starter.util.hourFormat
import org.joda.time.DateTime

class NotifierMessageFactory(
) {

    fun getAllMessages(measurementDocument: HandleMeasurementDocument): List<String> {
        val messages = mutableListOf<String>()
        measurementDocument.alarm.let {
            if (it.fire) {
                messages.add(fire(measurementDocument.date))
            }
            if (it.burglary) {
                messages.add(burglar(measurementDocument.date))
            }
        }
        return messages
    }

    private fun fire(date: DateTime): String = "W twoim mieszkaniu pojawił się ogień " +
            "${dayFormat(date)} o godzinie ${hourFormat(date)}"

    private fun burglar(date: DateTime): String = "Ktoś próbuje się włamać do Twojego mieszkania " +
            "${dayFormat(date)} o godzinie ${hourFormat(date)}"

    @Deprecated("we won't use it anymonre")
    private fun frost(date: DateTime, temperature: Double): String = "Temperatura w Twoim mieszkaniu drastycznie się zmieniła." +
            " Wynosi $temperature stopni o godzinie ${hourFormat(date)}."

    fun belowSet(date: DateTime, temperature: Double, settingsTemperature: Double) = "Temperatura w Twoim mieszkaniu spadła poniżej ustawionej." +
            " Wynosi $temperature stopni o godzinie ${hourFormat(date)}. Minimalna wartość: $settingsTemperature"

    fun motionDetected(date: DateTime, name: String): String = "Właśnie wykryto ruch w Twoim pomieszczeniu: $name"

}