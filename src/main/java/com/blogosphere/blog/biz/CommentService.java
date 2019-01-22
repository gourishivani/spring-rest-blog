package com.blogosphere.blog.biz;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;

import com.blogosphere.blog.model.Comment;

public interface CommentService {
	
	public Comment createComment(@Valid Comment Comment);
	
	public List<Comment> findAllByPost(Long postId, Pageable pageable);
	
	public Optional<Comment> find(Long postId);
	
}
