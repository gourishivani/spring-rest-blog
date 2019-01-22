package com.blogosphere.blog.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogosphere.blog.model.Comment;
import com.blogosphere.blog.model.Post;

// Spring will provide implementation for all the methods defined in the JpaRepository 
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByPost_Id(Long postId);
}
