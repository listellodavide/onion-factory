package com.execodex.demolocalai.config

import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class LiquibaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.liquibase")
    fun liquibaseProperties(): LiquibaseProperties {
        return LiquibaseProperties()
    }

    @Bean
    @Primary
    fun liquibase(dataSource: DataSource, liquibaseProperties: LiquibaseProperties): SpringLiquibase {
        val liquibase = SpringLiquibase()
        liquibase.dataSource = dataSource
        liquibase.changeLog = liquibaseProperties.changeLog
        liquibase.setContexts(liquibaseProperties.contexts?.joinToString(","))
        liquibase.defaultSchema = liquibaseProperties.defaultSchema
        liquibase.isDropFirst = liquibaseProperties.isDropFirst
        liquibase.setShouldRun(liquibaseProperties.isEnabled)
        return liquibase
    }
}