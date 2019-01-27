package com.blogosphere.blog.biz;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.blogosphere.blog.dao.CommentRepository;
import com.blogosphere.blog.model.Comment;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Override
	public Comment createComment(@Valid Comment comment) {
		return this.commentRepository.save(comment);
	}

	@Override
	public List<Comment> findAllByPost(Long postId, Sort sort) {
		return this.commentRepository.findByPost_Id(postId, sort);
	}

	@Override
	public Optional<Comment> find(Long postId) {
		return this.commentRepository.findById(postId);
	}
}
