package com.blogosphere.blog.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.blogosphere.blog")
@EnableTransactionManagement
@PropertySource("application.properties")
@EnableAutoConfiguration // Let spring do Auto Configuration. This allows spring to auto configure spring Data JPA
@EntityScan(basePackages = "com.blogosphere.blog.model") // Where are our entities?
//@ComponentScan("com.blogosphere.blog")
public class IntegrationTestConfig {
}
