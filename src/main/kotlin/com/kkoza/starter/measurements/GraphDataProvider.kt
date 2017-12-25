package com.kkoza.starter.measurements

import com.kkoza.starter.measurements.api.FieldFilter
import com.kkoza.starter.measurements.api.ItemsDto
import com.kkoza.starter.measurements.exception.IllegalQueryDateException
import com.kkoza.starter.measurements.fieldmapper.GraphFieldMapper
import org.apache.log4j.Logger
import org.joda.time.DateTime
import java.lang.invoke.MethodHandles

class GraphDataProvider(
        private val graphRepository: GraphRepository,
        private val graphFieldMappers: List<GraphFieldMapper>
) {
    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
        const val ONE_DAY = 1
    }

    fun getGraphDataList(startDate: DateTime?, endDate: DateTime?, fieldName: FieldFilter, handleId: String): ItemsDto {
        logger.info("Get graph list for $startDate, $endDate, $fieldName, $handleId")
        val start = startDate ?: DateTime.now().minusDays(ONE_DAY)
        val end = endDate ?: DateTime.now()
        throwExceptionIfStartIsAfterEnd(start, end)
        val measurements = graphRepository.getMeasurementsForGraph(start, end, handleId)
        val fieldMapper = graphFieldMappers.find { it.shouldApply(fieldName) }!!
        return ItemsDto(fieldMapper.map(measurements))
    }

    private fun throwExceptionIfStartIsAfterEnd(start: DateTime, end: DateTime) {
        if (start.isAfter(end)) {
            throw IllegalQueryDateException()
        }
    }

}

