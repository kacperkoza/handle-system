package com.kkoza.starter.measurements

import com.kkoza.starter.handles.HandleDocument
import com.kkoza.starter.handles.HandleDto
import com.kkoza.starter.handles.HandleFacade
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
        private val handleFacade: HandleFacade,
        private val userFacade: UserFacade
) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun add(measurementDocument: MeasurementDocument): String {
        val handle: HandleDocument? = handleFacade.findById(measurementDocument.handleId)
        val handleOwner: UserDocument? = userFacade.findUserById(handle!!.userId)
        logger.info("Add new $measurementDocument")
        if (handleOwner != null) {
            dangerEventNotifier.notify(measurementDocument, handleOwner.phoneNumber)
        }
        return measurementRepository.add(measurementDocument)
    }

    fun get(userId: String, sort: String?, offset: Int?, limit: Int?): MeasurementList {
        logger.info("get list with sort = $sort, offset = $offset, limit = $limit for userId = $userId")
        if (offset != null && offset < 0) throw InvalidPagingParameterException("offset")
        if (limit != null && limit < 0) throw InvalidPagingParameterException("limit")
        val sortType = MeasurementSortType.from(sort)
        val handles = handleFacade.findByUserId(userId)
        val list = measurementRepository.get(handles.map { it.id }, getSortOrder(sortType))
        return MeasurementList(
                list.size,
                limit,
                offset,
                mapToMeasurement(list, handles).dropIfNotNull(offset).takeIfNotNull(limit),
                handles)
    }

    private fun mapToMeasurement(list: List<MeasurementDocument>, handles: List<HandleDto>): List<Measurement> {
        return list.map {
            Measurement(it.id,
                    it.date,
                    handles.find { handle -> handle.id == it.handleId }?.name ?: "Brak nazwy",
                    it.handlePosition,
                    it.temperature,
                    it.alarm,
                    it.soundLevel,
                    it.handleTime)
        }
    }

    private fun getSortOrder(sortType: MeasurementSortType): Sort {
        return when (sortType) {
            MeasurementSortType.DATE_LATEST -> Sort(Sort.Direction.DESC, MeasurementDocument.DATE)
            MeasurementSortType.DATE_OLDEST -> Sort(Sort.Direction.ASC, MeasurementDocument.DATE)
            MeasurementSortType.TEMP_ASC -> Sort(Sort.Direction.ASC, MeasurementDocument.TEMPERATURE)
            MeasurementSortType.TEMP_DESC -> Sort(Sort.Direction.DESC, MeasurementDocument.TEMPERATURE)
            MeasurementSortType.SOUND_ASC -> Sort(Sort.Direction.ASC, MeasurementDocument.SOUND_LEVEL)
            MeasurementSortType.SOUND_DESC -> Sort(Sort.Direction.DESC, MeasurementDocument.SOUND_LEVEL)
        }
    }

    fun deleteById(id: String) {
        logger.info("delete measurement id = $id")
        measurementRepository.delete(id)
    }


}

