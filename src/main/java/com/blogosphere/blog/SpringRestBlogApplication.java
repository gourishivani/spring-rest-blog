package com.blogosphere.blog;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringRestBlogApplication {

	public static void main(String[] args) {
		// Fires up a Servlet container and serve Blog service
		SpringApplication.run(SpringRestBlogApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
