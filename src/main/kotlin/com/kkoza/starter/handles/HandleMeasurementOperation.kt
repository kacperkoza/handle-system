package com.kkoza.starter.handles

import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.devices.api.DeviceDto
import com.kkoza.starter.handles.api.AlarmFilter
import com.kkoza.starter.handles.api.HandleSortType
import com.kkoza.starter.handles.dto.HandleMeasurement
import com.kkoza.starter.handles.dto.MeasurementList
import com.kkoza.starter.handles.dto.SoundLevel
import com.kkoza.starter.handles.dto.Temperature
import com.kkoza.starter.handles.exception.InvalidPagingParameterException
import com.kkoza.starter.notification.NotificationService
import org.apache.log4j.Logger
import org.springframework.data.domain.Sort
import java.lang.invoke.MethodHandles

class HandleMeasurementOperation(
        private val handleMeasurementRepository: HandleMeasurementRepository,
        private val deviceFacade: DeviceFacade,
        private val notificationService: NotificationService
) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun addHandleMeasurement(handleMeasurementDocument: HandleMeasurementDocument): String {
        notificationService.notifyIfNecessary(handleMeasurementDocument)
        return handleMeasurementRepository.add(handleMeasurementDocument)
    }

    fun getHandleMeasurement(userId: String, sort: HandleSortType, offset: Int?, limit: Int?, alarms: List<AlarmFilter>?, handles: List<String>?): MeasurementList {
        logger.info("get list with sort = $sort, offset = $offset, limit = $limit, alarms = $alarms, devices = $handles for userId = $userId")
        validateInputParameters(offset, limit)
        val userHandles = deviceFacade.findByUserId(userId)
        val userHandlesIds = userHandles.map { it.id }
        val userQueriedHandles = handles?.filter { it in userHandlesIds } ?: userHandlesIds
        val sortOrder = getSortOrder(sort)
        val list = handleMeasurementRepository.get(userQueriedHandles, sortOrder, alarms, offset ?: 0, limit ?: 0)
        return MeasurementList(
                handleMeasurementRepository.count(userQueriedHandles, sortOrder, alarms),
                limit,
                offset,
                mapToMeasurement(list, userHandles)
        )
    }

    private fun validateInputParameters(offset: Int?, limit: Int?) {
        if (offset != null && offset < 0) throw InvalidPagingParameterException("offset")
        if (limit != null && limit < 0) throw InvalidPagingParameterException("limit")
    }

    private fun mapToMeasurement(list: List<HandleMeasurementDocument>, handles: List<DeviceDto>): List<HandleMeasurement> {
        val handleIdToName = handles.associateBy({ it.id }, { it.name })
        return list.map {
            HandleMeasurement(it.id,
                    it.date,
                    handleIdToName[it.handleId] ?: "Brak nazwy",
                    it.handleId,
                    it.handlePosition,
                    Temperature(it.temperature),
                    it.alarm,
                    SoundLevel(it.soundLevel),
                    it.handleTime)
        }
    }

    private fun getSortOrder(sortType: HandleSortType): Sort {
        return when (sortType) {
            HandleSortType.DATE_LATEST -> Sort(Sort.Direction.DESC, HandleMeasurementDocument.DATE)
            HandleSortType.DATE_OLDEST -> Sort(Sort.Direction.ASC, HandleMeasurementDocument.DATE)
            HandleSortType.TEMP_ASC -> Sort(Sort.Direction.ASC, HandleMeasurementDocument.TEMPERATURE)
            HandleSortType.TEMP_DESC -> Sort(Sort.Direction.DESC, HandleMeasurementDocument.TEMPERATURE)
            HandleSortType.SOUND_ASC -> Sort(Sort.Direction.ASC, HandleMeasurementDocument.SOUND_LEVEL)
            HandleSortType.SOUND_DESC -> Sort(Sort.Direction.DESC, HandleMeasurementDocument.SOUND_LEVEL)
        }
    }

    fun deleteHandleMeasurementById(id: String) {
        logger.info("delete measurement id = $id")
        handleMeasurementRepository.delete(id)
    }

    fun findOneFromEveryHandle(userId: String): List<HandleMeasurement> {
        val handles = deviceFacade.findByUserId(userId)
        return handles.mapNotNull { handleMeasurementRepository.findMostRecent(userId, it.id)?.toHandleMeasurement(it.name) }
    }

}