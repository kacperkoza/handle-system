package com.kkoza.starter.nodes

import com.kkoza.starter.devices.DeviceDocument
import com.kkoza.starter.devices.DeviceFacade
import com.kkoza.starter.devices.api.DeviceDto
import com.kkoza.starter.handles.exception.InvalidPagingParameterException
import com.kkoza.starter.handles.exception.InvalidSortTypeException
import com.kkoza.starter.nodes.api.NodeFilter
import com.kkoza.starter.nodes.dto.NodeMeasurement
import com.kkoza.starter.nodes.dto.NodeMeasurementList
import com.kkoza.starter.util.dropIfNotNull
import com.kkoza.starter.util.takeIfNotNull
import org.apache.log4j.Logger
import org.springframework.data.domain.Sort
import java.lang.invoke.MethodHandles

class NodeMeasurementOperation(
        private val nodeMeasurementRepository: NodeMeasurementRepository,
//        private val dangerEventNotifier: DangerEventNotifier,
        private val deviceFacade: DeviceFacade
//        private val userFacade: UserFacade
) {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    fun addNodeMeasurement(nodeMeasurementDocument: NodeMeasurementDocument): String {
        val device: DeviceDocument? = deviceFacade.findById(nodeMeasurementDocument.nodeId)
        if (device != null) {
            //todo: notify?
//            notifyIfNecessaryAboutEvent(device, nodeMeasurementDocument)
        }
        return nodeMeasurementRepository.addNodeMeasurement(nodeMeasurementDocument)
    }


    //todo remove?
//    private fun notifyIfNecessaryAboutEvent(device: DeviceDocument, handleMeasurementDocument: HandleMeasurementDocument) {
//        val handleOwner: UserDocument? = userFacade.findUserById(device.userId)
//        if (handleOwner != null) {
//            dangerEventNotifier.notify(handleMeasurementDocument, handleOwner.phoneNumber)
//        }
//    }

    fun getNodeMeasurement(userId: String, sort: NodeSortType, offset: Int?, limit: Int?, nodes: List<String>?, fieldFilters: List<NodeFilter>?): NodeMeasurementList {
        logger.info("get node measurement list with sort = $sort, offset = $offset, limit = $limit, devices = $nodes for userId = $userId")
        if (offset != null && offset < 0) throw InvalidPagingParameterException("offset")
        if (limit != null && limit < 0) throw InvalidPagingParameterException("limit")
        val userNodes = deviceFacade.findByUserId(userId)
        val userNodesIds = userNodes.map { it.id }
        val filteredNodes = nodes?.filter { it in userNodesIds } ?: userNodesIds
        val list = nodeMeasurementRepository.getNodeMeasurements(filteredNodes, getSortOrder(sort), fieldFilters)
        return NodeMeasurementList(
                list.size,
                limit,
                offset,
                mapToMeasurement(list, userNodes).dropIfNotNull(offset).takeIfNotNull(limit),
                userNodes)
    }

    private fun mapToMeasurement(list: List<NodeMeasurementDocument>, nodes: List<DeviceDto>): List<NodeMeasurement> {
        val nodeIdToName = nodes.associateBy({ it.id }, { it.name })
        return list.map {
            it.toNodeMeasurement(nodeIdToName[it.nodeId] ?: "Brak nazwy")
        }
    }

    private fun getSortOrder(sortType: NodeSortType): Sort {
        return when (sortType) {
            NodeSortType.DATE_LATEST -> Sort(Sort.Direction.DESC, NodeMeasurementDocument.DATE)
            NodeSortType.DATE_OLDEST -> Sort(Sort.Direction.ASC, NodeMeasurementDocument.DATE)
            NodeSortType.TEMP_ASC -> Sort(Sort.Direction.ASC, NodeMeasurementDocument.TEMPERATURE)
            NodeSortType.TEMP_DESC -> Sort(Sort.Direction.DESC, NodeMeasurementDocument.TEMPERATURE)
            NodeSortType.HUM_ASC -> Sort(Sort.Direction.ASC, NodeMeasurementDocument.HUMIDITY)
            NodeSortType.HUM_DESC -> Sort(Sort.Direction.DESC, NodeMeasurementDocument.HUMIDITY)
            NodeSortType.LIGHT_ASC -> Sort(Sort.Direction.ASC, NodeMeasurementDocument.LIGHT_INTENSITY)
            NodeSortType.LIGHT_DESC -> Sort(Sort.Direction.DESC, NodeMeasurementDocument.LIGHT_INTENSITY)
            NodeSortType.CARBON_ASC -> Sort(Sort.Direction.ASC, NodeMeasurementDocument.CARBON_DIOXIDE)
            NodeSortType.CARBON_DESC -> Sort(Sort.Direction.DESC, NodeMeasurementDocument.CARBON_DIOXIDE)
        }
    }

    fun deleteNodeMeasurementById(id: String) {
        logger.info("delete node measurement id = $id")
        nodeMeasurementRepository.deleteNodeMeasurementById(id)
    }

    fun findOneFromEveryUserNode(userId: String): List<NodeMeasurement> {
        val handles = deviceFacade.findByUserId(userId)
        return handles.mapNotNull { nodeMeasurementRepository.findMostRecentNodeMeasurement(userId, it.id)?.toNodeMeasurement(it.name) }
    }

}

enum class NodeSortType {
    DATE_LATEST,
    DATE_OLDEST,
    TEMP_ASC,
    TEMP_DESC,
    HUM_ASC,
    HUM_DESC,
    LIGHT_ASC,
    LIGHT_DESC,
    CARBON_ASC,
    CARBON_DESC;


    companion object {
        fun from(source: String?): NodeSortType {
            source ?: return DATE_LATEST
            return try {
                NodeSortType.valueOf(source.toUpperCase())
            } catch (ex: IllegalArgumentException) {
                throw InvalidSortTypeException(source, NodeSortType.values().joinToString(separator = ", ", prefix = "[", postfix = "]"))
            }
        }
    }
}


