package com.kkoza.starter.measurements

import com.kkoza.starter.measurements.api.MeasurementList
import com.kkoza.starter.measurements.api.SortType
import com.kkoza.starter.measurements.exception.InvalidPagingParameterException
import com.kkoza.starter.util.dropIfNotNull
import com.kkoza.starter.util.takeIfNotNull
import org.apache.log4j.Logger
import org.springframework.data.domain.Sort
import java.lang.invoke.MethodHandles

class MeasurementOperation(
        private val measurementRepository: MeasurementRepository,
        private val dangerEventNotifier: DangerEventNotifier
) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun add(measurement: Measurement): String {
        logger.info("Add new $measurement")
        dangerEventNotifier.notify(measurement, "jakis numer telefonu")
        return measurementRepository.add(measurement)
    }

    fun get(sort: String?, offset: Int?, limit: Int?): MeasurementList {
        logger.info("get list with sort = $sort, offset = $offset, limit = $limit,")
        if (offset != null && offset < 0) throw InvalidPagingParameterException("offset")
        if (limit != null && limit < 0) throw InvalidPagingParameterException("limit")
        val sortType = SortType.from(sort)
        val list = measurementRepository.get(getSortOrder(sortType))
        return MeasurementList(
                list.size,
                limit,
                offset,
                list.dropIfNotNull(offset).takeIfNotNull(limit))
    }

    private fun getSortOrder(sortType: SortType): Sort {
        return when (sortType) {
            SortType.DATE_LATEST -> Sort(Sort.Direction.DESC, Measurement.DATE)
            SortType.DATE_OLDEST -> Sort(Sort.Direction.ASC, Measurement.DATE)
            SortType.TEMPERATURE_ASCENDING -> TODO()
            SortType.TEMPERATURE_DESCENDING -> TODO()
            SortType.SOUND_LEVEL_ASCENDING -> TODO()
            SortType.SOUND_LEVEL_DESCENDING -> TODO()
        }
    }

    fun deleteById(id: String) {
        logger.info("delete measurement id = $id")
        measurementRepository.deleteById(id)
    }


}

