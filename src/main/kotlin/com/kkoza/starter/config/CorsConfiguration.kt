package com.kkoza.starter.config

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.*
import java.lang.invoke.MethodHandles


@EnableWebMvc
@Configuration
class CorsConfiguration(
        @Value("\${origins.github}") val github: String,
        @Value("\${origins.localhost}") val localhost: String
) : WebMvcConfigurerAdapter() {

    companion object {
        private val logger = Logger.getLogger(MethodHandles.lookup().lookupClass())
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        logger.info("Add cors for $github")
        logger.info("Add cors for $localhost")
        registry.addMapping("/**")
                .allowedOrigins(github, localhost)
                .allowedMethods("PUT", "GET", "DELETE", "POST")
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        logger.info("add redirect for /")
        registry.addRedirectViewController("/", "/swagger-ui.html")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        logger.info("Add swagger resources")
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html")
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
    }

}