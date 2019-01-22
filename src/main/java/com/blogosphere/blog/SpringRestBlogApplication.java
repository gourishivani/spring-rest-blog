package com.blogosphere.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringRestBlogApplication {

	public static void main(String[] args) {
		//Fires up a Servlet container and serve Blog service
		SpringApplication.run(SpringRestBlogApplication.class, args);
	}

}

