package com.kkoza.starter.measurements

import com.kkoza.starter.devices.DeviceDocument
import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.devices.api.DeviceDto
import com.kkoza.starter.measurements.api.AlarmFilter
import com.kkoza.starter.measurements.api.Measurement
import com.kkoza.starter.measurements.api.MeasurementList
import com.kkoza.starter.measurements.api.MeasurementSortType
import com.kkoza.starter.measurements.exception.InvalidPagingParameterException
import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.UserFacade
import com.kkoza.starter.util.dropIfNotNull
import com.kkoza.starter.util.takeIfNotNull
import org.apache.log4j.Logger
import org.springframework.data.domain.Sort
import java.lang.invoke.MethodHandles

class MeasurementOperation(
        private val measurementRepository: MeasurementRepository,
        private val dangerEventNotifier: DangerEventNotifier,
        private val deviceFacade: DeviceFacade,
        private val userFacade: UserFacade
) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun add(handleMeasurementDocument: HandleMeasurementDocument): String {
        val device: DeviceDocument? = deviceFacade.findHandleById(handleMeasurementDocument.handleId)
        if (device != null) {
            notifyIfNecessaryAboutEvent(device, handleMeasurementDocument)
        }
        return measurementRepository.add(handleMeasurementDocument)
    }

    private fun notifyIfNecessaryAboutEvent(device: DeviceDocument, handleMeasurementDocument: HandleMeasurementDocument) {
        val handleOwner: UserDocument? = userFacade.findUserById(device.userId)
        if (handleOwner != null) {
            dangerEventNotifier.notify(handleMeasurementDocument, handleOwner.phoneNumber)
        }
    }

    fun get(userId: String, sort: MeasurementSortType, offset: Int?, limit: Int?, alarms: List<AlarmFilter>?, handles: List<String>?): MeasurementList {
        logger.info("get list with sort = $sort, offset = $offset, limit = $limit, alarms = $alarms, devices = $handles for userId = $userId")
        if (offset != null && offset < 0) throw InvalidPagingParameterException("offset")
        if (limit != null && limit < 0) throw InvalidPagingParameterException("limit")
        val userHandles = deviceFacade.findHandleByUserId(userId)
        val userHandlesIds = userHandles.map { it.id }
        val filteredHandles = handles?.filter { it in userHandlesIds } ?: userHandlesIds
        val list = measurementRepository.get(filteredHandles, getSortOrder(sort), alarms)
        return MeasurementList(
                list.size,
                limit,
                offset,
                mapToMeasurement(list, userHandles).dropIfNotNull(offset).takeIfNotNull(limit),
                userHandles)
    }


    private fun mapToMeasurement(list: List<HandleMeasurementDocument>, handles: List<DeviceDto>): List<Measurement> {
        val handleIdToName = handles.associateBy({ it.id }, { it.name })
        return list.map {
            Measurement(it.id,
                    it.date,
                    handleIdToName[it.handleId] ?: "Brak nazwy",
                    it.handlePosition,
                    it.temperature,
                    it.alarm,
                    it.soundLevel,
                    it.handleTime)
        }
    }

    private fun getSortOrder(sortType: MeasurementSortType): Sort {
        return when (sortType) {
            MeasurementSortType.DATE_LATEST -> Sort(Sort.Direction.DESC, HandleMeasurementDocument.DATE)
            MeasurementSortType.DATE_OLDEST -> Sort(Sort.Direction.ASC, HandleMeasurementDocument.DATE)
            MeasurementSortType.TEMP_ASC -> Sort(Sort.Direction.ASC, HandleMeasurementDocument.TEMPERATURE)
            MeasurementSortType.TEMP_DESC -> Sort(Sort.Direction.DESC, HandleMeasurementDocument.TEMPERATURE)
            MeasurementSortType.SOUND_ASC -> Sort(Sort.Direction.ASC, HandleMeasurementDocument.SOUND_LEVEL)
            MeasurementSortType.SOUND_DESC -> Sort(Sort.Direction.DESC, HandleMeasurementDocument.SOUND_LEVEL)
        }
    }

    fun deleteById(id: String) {
        logger.info("delete measurement id = $id")
        measurementRepository.delete(id)
    }


}

class InvalidHandleException(userId: String, handleId: String) : RuntimeException("User = $userId doesn't have handleAlarmFilterEx with $handleId")

