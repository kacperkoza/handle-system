package com.kkoza.starter.graphs

import com.kkoza.starter.graphs.handlemapper.HandleMapper
import com.kkoza.starter.graphs.nodemapper.NodeMapper
import com.kkoza.starter.handles.GraphRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class GraphFacadeConfiguration {

    @Bean
    fun graphFacade(
            mongoTemplate: MongoTemplate,
            handleMappers: List<HandleMapper>,
            nodeMappers: List<NodeMapper>
    ): GraphFacade {
        val graphRepository = GraphRepository(mongoTemplate)
        val graphDataProvider =  GraphDataProvider(graphRepository, handleMappers, nodeMappers)
        return GraphFacade(graphDataProvider)
    }
}