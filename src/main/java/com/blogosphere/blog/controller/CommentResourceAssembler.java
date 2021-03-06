package com.blogosphere.blog.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.blogosphere.blog.dto.CommentDetailDto;

@Component
public class CommentResourceAssembler implements ResourceAssembler<CommentDetailDto, Resource<CommentDetailDto>> {

	@Override
	public Resource<CommentDetailDto> toResource(CommentDetailDto comment) {
		return new Resource<>(comment,
			linkTo(methodOn(CommentController.class).getComment(comment.getPost().getId())).withSelfRel(),
			linkTo(methodOn(CommentController.class).getAllCommentsRestful(comment.getPost().getId())).withRel("comments"));
	}
}