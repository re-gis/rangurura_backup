package com.backend.proj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

@Configuration
@EnableSwagger2
@EnableWebMvc
@RequiredArgsConstructor
public class SwaggerConfiguration extends WebMvcConfigurationSupport {
    private final ServletContext servletContext;

    @Value("${server.host}")
    private String host;

    @Value("${swagger.app_name}")
    private String appName;

    @Value("${swagger.app_description}")
    private String appDescription;

    @Value("${swagger.end_point}")
    private String swaggerEndpoint;

    @Value("${swagger.base_controller_path}")
    private String baseControllerPath;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(swaggerEndpoint).addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler(swaggerEndpoint).addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
        };
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).directModelSubstitute(LocalDate.class, Date.class)
                .host(host)
                .pathProvider(new RelativePathProvider(servletContext) {
                    @Override
                    public String getApplicationBasePath() {
                        return "";
                    }
                }).select().apis(RequestHandlerSelectors.basePackage(baseControllerPath))
                .paths(PathSelectors.any()).build().apiInfo(apiInfo()).securitySchemes(Arrays.asList(apiKey()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    private ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/.*")).build();
    }

    private List<SecurityReference> defaultAuth() {
        final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[] { authorizationScope };
        return Collections.singletonList(new SecurityReference("Bearer", authorizationScopes));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(appName,
                appDescription,
                "1.0",
                "Terms of service",
                new Contact("SIBOMANA Edouard && Irumva Regis Dieu Merci",
                        "sibomanaedouard974@gmail.com", "irumvaregisdmc@gmail.com"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

    // @Bean
    // public Docket api() {
    // return new Docket(DocumentationType.SWAGGER_2)
    // .apiInfo(apiInfo())
    // .select()
    // .apis(RequestHandlerSelectors.any())
    // .paths(PathSelectors.any())
    // .build();
    // }
}
