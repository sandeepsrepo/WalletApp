package com.contour.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket eDesignApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).enable(true).select().apis(RequestHandlerSelectors.basePackage("com.contour.wallet"))
                .paths(PathSelectors.any()).build().pathMapping("/").directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class).useDefaultResponseMessages(false)
                .enableUrlTemplating(false);
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder().deepLinking(true).displayOperationId(false)
                .defaultModelsExpandDepth(1).defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE).displayRequestDuration(false).docExpansion(DocExpansion.NONE)
                .filter(false).maxDisplayedTags(0).operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false).tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS).validatorUrl(null).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Wallet API").description("Assignment Wallet API documentation and can be used to test the API calls.")
                .version("1.0-SNAPSHOT").build();
    }
}