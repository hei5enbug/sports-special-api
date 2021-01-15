package com.bongmany.sportsspecialapi.config

import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

class WebMvcConfig : WebMvcConfigurer {

    // Allow all Cors
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
    }
}