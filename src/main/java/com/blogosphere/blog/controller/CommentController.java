package com.blogosphere.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogosphere.blog.biz.CommentService;
import com.blogosphere.blog.exception.EntityNotFoundException;
import com.blogosphere.blog.model.Comment;

@RequestMapping(path = "/comments")
@RestController
public class CommentController {

	@Autowired
	private CommentService commentService;

	@Autowired
	private CommentResourceAssembler commentAssembler;
	
	@GetMapping(path = "/{commentId}")
	public Resource<Comment> getComment(@PathVariable Long commentId) {
		Comment comment = commentService.find(commentId).orElseThrow(() -> new EntityNotFoundException(commentId, Comment.class));
		return commentAssembler.toResource(comment);
	}
}
