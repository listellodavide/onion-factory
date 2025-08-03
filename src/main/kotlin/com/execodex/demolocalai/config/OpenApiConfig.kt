package com.execodex.demolocalai.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(apiInfo())
            .addServersItem(Server().url("/").description("Default Server URL"))
    }

    private fun apiInfo(): Info {
        return Info()
            .title("Demo Local AI API")
            .description("API documentation for Demo Local AI application")
            .version("1.0.0")
            .contact(
                Contact()
                    .name("ExecodeX")
                    .email("info@execodex.com")
                    .url("https://execodex.com")
            )
            .license(
                License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")
            )
    }
}