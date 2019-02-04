package com.blogosphere.blog.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket api() {
      return new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.basePackage("com.blogosphere.blog"))
//      .apis(RequestHandlerSelectors.basePackage("com.blogosphere.blog.jwt.resource"))
//      .apis(RequestHandlerSelectors.any())
      .paths(PathSelectors.any()).build().apiInfo(apiInfo());
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfo(
          "Restful Blogging Service", 
          "Allows to Create users, comments and blog posts", 
          "Version 1.0", 
          "Terms of service", 
          new Contact("Shivani", "https://github.com/gourishivani/spring-rest-blog", "test@company.com"), 
          "License of API", "API license URL");
    }
}
