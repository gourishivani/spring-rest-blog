package com.blogosphere.blog.biz;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.blogosphere.blog.dao.UserRepository;
import com.blogosphere.blog.model.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	public User createUser(User user) {
		return this.userRepository.save(user);
	}

	@Override
	public List<User> findAll(Pageable pageable) {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> find(Long id) {
		return this.userRepository.findById(id);
	}

	@Override
	public boolean authenticate(String email, String passwordHash) {
		return true;
	}

	@Override
	public boolean unAuthenticate(String email, String passwordHash) {
		// TODO Auto-generated method stub
		return true;
	}
}
