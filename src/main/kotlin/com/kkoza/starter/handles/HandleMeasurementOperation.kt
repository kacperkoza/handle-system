package com.kkoza.starter.handles

import com.kkoza.starter.devices.DeviceDocument
import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.devices.api.DeviceDto
import com.kkoza.starter.handles.api.AlarmFilter
import com.kkoza.starter.handles.api.HandleMeasurement
import com.kkoza.starter.handles.api.MeasurementList
import com.kkoza.starter.handles.api.HandleSortType
import com.kkoza.starter.handles.exception.InvalidPagingParameterException
import com.kkoza.starter.user.UserDocument
import com.kkoza.starter.user.UserFacade
import com.kkoza.starter.util.dropIfNotNull
import com.kkoza.starter.util.takeIfNotNull
import org.apache.log4j.Logger
import org.springframework.data.domain.Sort
import java.lang.invoke.MethodHandles

class HandleMeasurementOperation(
        private val handleMeasurementRepository: HandleMeasurementRepository,
        private val dangerEventNotifier: DangerEventNotifier,
        private val deviceFacade: DeviceFacade,
        private val userFacade: UserFacade
) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun addHandleMeasurement(handleMeasurementDocument: HandleMeasurementDocument): String {
        val device: DeviceDocument? = deviceFacade.findById(handleMeasurementDocument.handleId)
        if (device != null) {
            notifyIfNecessaryAboutEvent(device, handleMeasurementDocument)
        }
        return handleMeasurementRepository.add(handleMeasurementDocument)
    }

    private fun notifyIfNecessaryAboutEvent(device: DeviceDocument, handleMeasurementDocument: HandleMeasurementDocument) {
        val handleOwner: UserDocument? = userFacade.findUserById(device.userId)
        if (handleOwner != null) {
            dangerEventNotifier.notify(handleMeasurementDocument, handleOwner.phoneNumber)
        }
    }

    fun getHandleMeasurement(userId: String, sort: HandleSortType, offset: Int?, limit: Int?, alarms: List<AlarmFilter>?, handles: List<String>?): MeasurementList {
        logger.info("getHandleMeasurements list with sort = $sort, offset = $offset, limit = $limit, alarms = $alarms, devices = $handles for userId = $userId")
        if (offset != null && offset < 0) throw InvalidPagingParameterException("offset")
        if (limit != null && limit < 0) throw InvalidPagingParameterException("limit")
        val userHandles = deviceFacade.findByUserId(userId)
        val userHandlesIds = userHandles.map { it.id }
        val filteredHandles = handles?.filter { it in userHandlesIds } ?: userHandlesIds
        val list = handleMeasurementRepository.get(filteredHandles, getSortOrder(sort), alarms)
        return MeasurementList(
                list.size,
                limit,
                offset,
                mapToMeasurement(list, userHandles).dropIfNotNull(offset).takeIfNotNull(limit),
                userHandles)
    }

    private fun mapToMeasurement(list: List<HandleMeasurementDocument>, handles: List<DeviceDto>): List<HandleMeasurement> {
        val handleIdToName = handles.associateBy({ it.id }, { it.name })
        return list.map {
            HandleMeasurement(it.id,
                    it.date,
                    handleIdToName[it.handleId] ?: "Brak nazwy",
                    it.handlePosition,
                    it.temperature,
                    it.alarm,
                    it.soundLevel,
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