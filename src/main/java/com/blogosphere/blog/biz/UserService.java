package com.blogosphere.blog.biz;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.blogosphere.blog.dao.UserRepository;
import com.blogosphere.blog.model.User;

public interface UserService {
	
	public User createUser(@Valid User user);
	
	public Page<User> findAll(Pageable pageable);
	
	public List<User> findAll(Sort sort);
	
	public Optional<User> find(Long id);
	
	public User findByEmail(String email);
}
