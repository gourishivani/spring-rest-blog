package com.blogosphere.blog.biz;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;

import com.blogosphere.blog.model.User;

public interface UserService {
	
	public User createUser(@Valid User user);
	
	public List<User> findAll(Pageable pageable);
	
	public Optional<User> find(Long id);
	
	public boolean authenticate(String email, String passwordHash);
	
	public boolean unAuthenticate(String email, String passwordHash);
}
