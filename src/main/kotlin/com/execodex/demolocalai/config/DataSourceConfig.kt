package com.execodex.demolocalai.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import javax.sql.DataSource

@Configuration
class DataSourceConfig(private val env: Environment) {

    @Bean
    fun dataSource(): DataSource {
        return DataSourceBuilder.create()
            .url(env.getProperty("spring.datasource.url"))
            .username(env.getProperty("spring.datasource.username"))
            .password(env.getProperty("spring.datasource.password"))
            .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
            .build()
    }
}