package com.blogosphere.blog.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import com.blogosphere.blog.model.User;

@RunWith(SpringRunner.class) // Needed when running Spring Boot Test features in Junit
@DataJpaTest // Provides standard setup to test persistence layer
public class UserRepositoryIntegrationTest {
	
	/**
	 * This can be used to setup data for tests
	 */
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void whenwhatever() {
		User user = new User();
		user.setEmail("foo@bar.com");
		user.setName("Foo");
		user.setPasswordHash("password");
		user = userRepository.save(user);
		
		User actual = entityManager.find(User.class, user.getId());
		
		System.out.println(user);
		System.out.println(actual);
		assertThat(user).isEqualTo(actual);
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	public void failOnDuplicateEmail() {
		User user = new User();
		user.setEmail("foo@bar.com");
		user.setName("Foo");
		user.setPasswordHash("password");
		user = userRepository.save(user);
		System.out.println(user);
		
		User user2 = new User();
		user2.setEmail(user.getEmail());
		user2.setName(user.getName());
		user2.setPasswordHash(user.getPasswordHash());
		try {
			user2 = userRepository.save(user2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(user2);
		assertThatExceptionOfType(RuntimeException.class);
	}
}
