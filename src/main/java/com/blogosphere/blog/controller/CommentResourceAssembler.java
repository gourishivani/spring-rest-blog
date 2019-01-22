package com.blogosphere.blog.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.blogosphere.blog.model.Comment;

@Component
public class CommentResourceAssembler implements ResourceAssembler<Comment, Resource<Comment>> {

	@Override
	public Resource<Comment> toResource(Comment comment) {
		return new Resource<>(comment,
			linkTo(methodOn(CommentController.class).getComment(comment.getPost().getId())).withSelfRel(),
			linkTo(methodOn(PostController.class).getAllComments(comment.getPost().getId())).withRel("comments"));
	}
}