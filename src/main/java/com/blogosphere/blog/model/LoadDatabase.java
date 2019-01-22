package com.blogosphere.blog.model;


import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blogosphere.blog.dao.CommentRepository;
import com.blogosphere.blog.dao.PostRepository;
import com.blogosphere.blog.dao.UserRepository;

/**
 * Idea from here: https://spring.io/guides/tutorials/rest/
 * @author varambal
 *
 */
@Configuration
@Slf4j
class LoadDatabase {
	
	@Bean
	CommandLineRunner initDatabase(UserRepository repository, PostRepository postRepository, CommentRepository commentRepository) {
		return args -> {
			User user1 = repository.save(new User("Bilbo Baggins", "burglar@example.com", "dash", "Kumbaya"));
			User user2 = repository.save(new User("Frodo Gurgles", "frodo@example.com", "food", "frodo"));
			log.info("Preloading " + user1);
			log.info("Preloading " + user2);
			
			Post post1 = new Post();
			post1.setAuthor(user1);
			post1.setDesciption("creates the property traversal x.address.zipCode. The resolution algorithm starts with interpreting the entire part");
			post1.setTitle("Spring JPA Docs");
			
			postRepository.save(post1);

			Post post2 = new Post();
			post2.setAuthor(user1);
			post2.setDesciption("Although this should work for most cases, it is possible for the algorithm to select the wrong property.");
			post2.setTitle("Spring Data Hibernate Docs");
			
			postRepository.save(post2);
			log.info("Preloading " + post1);
			log.info("Preloading " + post2);
			
			Comment comment1 = new Comment();
			comment1.setCommentor(user2);
			comment1.setContent("Great theory!");
			comment1.setPost(post1);
			commentRepository.save(comment1);
			
			Comment comment2 = new Comment();
			comment2.setCommentor(user1);
			comment2.setContent("To handle parameters in your query you simply define method parameters as already seen in the examples above. Besides that the infrastructure will recognize certain specific types like Pageable and Sort to apply pagination and sorting to your queries dynamically.");
			comment2.setPost(post1);
			commentRepository.save(comment2);
			
			
			Comment comment3 = new Comment();
			comment3.setCommentor(user1);
			comment3.setContent("To resolve this ambiguity you can use");
			comment3.setPost(post1);
			commentRepository.save(comment3);
			
			log.info("Preloading " + comment1);
			log.info("Preloading " + comment2);
		};
	}

}
