package com.blogosphere.blog.biz;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.blogosphere.blog.dao.UserRepository;
import com.blogosphere.blog.model.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public User createUser(User user) {
		return this.userRepository.save(user);
	}

	@Override
	public Page<User> findAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}
	
	@Override
	public List<User> findAll(Sort sort) {
		return userRepository.findAll(sort);
	}

	@Override
	public Optional<User> find(Long id) {
		return this.userRepository.findById(id);
	}

	@Override
	public User findByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}
}
