package com.kkoza.starter.measurements

import com.kkoza.starter.measurements.api.MeasurementList
import com.kkoza.starter.measurements.api.SortType
import com.kkoza.starter.measurements.exception.InvalidPagingParameterException
import com.kkoza.starter.util.dropIfNotNull
import com.kkoza.starter.util.takeIfNotNull
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

class MeasurementOperation(private val mongoTemplate: MongoTemplate) {

    fun add(measurement: Measurement) {
        mongoTemplate.save(measurement)
    }

    fun get(sort: String?, offset: Int?, limit: Int?): MeasurementList {
        if (offset != null && offset < 0) throw InvalidPagingParameterException("offset")
        if (limit != null && limit < 0) throw InvalidPagingParameterException("limit")
        val sortType = SortType.from(sort)
        val list = queryWithSort(sortType)
        return MeasurementList(
                list.size,
                limit,
                offset,
                list.dropIfNotNull(offset).takeIfNotNull(limit))
    }

    private fun queryWithSort(sortType: SortType): List<Measurement> {
        return mongoTemplate.find(
                Query().with(Sort(getSortOrder(sortType))),
                Measurement::class.java
        )
    }

    private fun getSortOrder(sortType: SortType): Sort.Order {
        return when (sortType) {
            SortType.DATE_LATEST -> order(Sort.Direction.DESC, Measurement.DATE)
            SortType.DATE_OLDEST -> order(Sort.Direction.ASC, Measurement.DATE)
            else -> order(Sort.Direction.DESC, Measurement.DATE)
        }
    }

    private fun order(direction: Sort.Direction, fieldName: String) = Sort.Order(direction, fieldName)

}

