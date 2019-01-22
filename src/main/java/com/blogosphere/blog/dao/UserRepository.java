package com.blogosphere.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogosphere.blog.model.User;

// Spring will provide implementation for all the methods defined in the JpaRepository 
public interface UserRepository extends JpaRepository<User, Long> {

}
