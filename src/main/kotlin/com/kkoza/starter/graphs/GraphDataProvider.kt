package com.kkoza.starter.graphs

import com.kkoza.starter.handles.exception.IllegalQueryDateException
import com.kkoza.starter.graphs.handlemapper.HandleMapper
import com.kkoza.starter.graphs.nodemapper.NodeMapper
import com.kkoza.starter.handles.GraphRepository
import org.apache.log4j.Logger
import org.joda.time.DateTime
import java.lang.invoke.MethodHandles

class GraphDataProvider(
        private val graphRepository: GraphRepository,
        private val handleMappers: List<HandleMapper>,
        private val nodeMappers: List<NodeMapper>
) {
    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
        private const val ONE_DAY = 1
    }

    fun getGraphDataFromHandle(startDate: DateTime?, endDate: DateTime?, handleFieldName: HandleFieldFilter, handleId: String): ItemsDto {
        logger.info("Get graph list for $startDate, $endDate, $handleFieldName, $handleId")
        val (start, end) = evaluateDates(startDate, endDate)
        val measurements = graphRepository.getMeasurementsForGraph(start, end, handleId)
        val fieldMapper = handleMappers.find { it.shouldApply(handleFieldName) }!!
        return ItemsDto(fieldMapper.map(measurements))
    }

    fun getGraphDataFromNode(startDate: DateTime?, endDate: DateTime?, nodeFieldFilter: NodeFieldFilter, nodeId: String): ItemsDto {
        logger.info("Get node graph data for start = $startDate, end = $endDate, field = $nodeFieldFilter, nodeId = $nodeId")
        val (start, end) = evaluateDates(startDate, endDate)
        val measurements = graphRepository.getNodeMeasurements(start, end, nodeId)
        val fieldMapper = nodeMappers.find { it.shouldApply(nodeFieldFilter) }!!
        return ItemsDto(fieldMapper.map(measurements))
    }

    private fun evaluateDates(startDate: DateTime?, endDate: DateTime?): Pair<DateTime, DateTime> {
        val start = startDate ?: DateTime.now().minusDays(ONE_DAY)
        val end = endDate ?: DateTime.now()
        throwExceptionIfStartIsAfterEnd(start, end)
        return Pair(start, end)
    }

    private fun throwExceptionIfStartIsAfterEnd(start: DateTime, end: DateTime) {
        if (start.isAfter(end)) {
            throw IllegalQueryDateException()
        }
    }

}

