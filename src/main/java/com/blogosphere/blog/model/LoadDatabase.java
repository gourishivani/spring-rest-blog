package com.blogosphere.blog.model;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blogosphere.blog.dao.CommentRepository;
import com.blogosphere.blog.dao.PostRepository;
import com.blogosphere.blog.dao.UserRepository;
import com.blogosphere.blog.knownobjects.KnownObjects;

import lombok.extern.slf4j.Slf4j;

/**
 * Idea from here: https://spring.io/guides/tutorials/rest/
 * @author Shivani
 *
 */
@Configuration
@Slf4j
class LoadDatabase {
	
	// TO DISABLE this, comment out the @Bean declaration
	@Bean
	CommandLineRunner initDatabase(UserRepository repository, PostRepository postRepository, CommentRepository commentRepository) {
		return args -> {
			
			User user1 = repository.save(KnownObjects.knownUser1());// Original Password: test123 

			User user2 = repository.save(KnownObjects.knownUser2()); // Original Password: tester
			log.info("Preloading User " + user1);
			log.info("Preloading User " + user2);
			
			Post post1 = KnownObjects.knownPost1(user1);
			postRepository.save(post1);

			Post post2 = KnownObjects.knownPost1(user1);
			postRepository.save(post2);
			log.info("Preloading Post " + post1);
			log.info("Preloading Post " + post2);
			
			Comment comment1 = KnownObjects.knownComment1(post1, user2);
			commentRepository.save(comment1);
			
			Comment comment2 = KnownObjects.knownComment1(post1, user1);
			commentRepository.save(comment2);
			
			
			Comment comment3 = KnownObjects.knownComment1(post1, user1);
			commentRepository.save(comment3);
			
			log.info("Preloading Comment " + comment1);
			log.info("Preloading Comment " + comment2);
		};
	}

}
