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
			// Original Password: test123
			User user1 = repository.save(new User("John Doe", "johndoe@example.com", "$2a$10$X1o2YKGHHj9XYnjv7rQEaeqDpaMUr3w/p88F7XRjJSCkDjQWly.cK", "My Test Blog Space")); 

			// Original Password: tester
			User user2 = repository.save(new User("Tester Albert", "tester@example.com", "$2a$10$Rx19UHVc6MPIPhrj0dIQb.M/NTFZkMhAMT6NBPx2Qtyj2jXM6rfxy", "frodo"));
			log.info("Preloading User" + user1);
			log.info("Preloading User" + user2);
			
			Post post1 = new Post();
			post1.setAuthor(user1);
			post1.setDescription("The 35-year-old politician declared himself acting president this week, and has been recognized as the country's president by the Trump administration.");
			post1.setTitle("Who Is Venezuela's Juan Guaid?");
			
			postRepository.save(post1);

			Post post2 = new Post();
			post2.setAuthor(user1);
			post2.setDescription("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.");
			post2.setTitle("What is Lorem Ipsum?");
			
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
			comment2.setContent("Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.");
			comment2.setPost(post1);
			commentRepository.save(comment2);
			
			
			Comment comment3 = new Comment();
			comment3.setCommentor(user1);
			comment3.setContent("Where does it come from?");
			comment3.setPost(post1);
			commentRepository.save(comment3);
			
			log.info("Preloading " + comment1);
			log.info("Preloading " + comment2);
		};
	}

}
