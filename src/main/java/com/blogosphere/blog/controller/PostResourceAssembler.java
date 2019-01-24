package com.blogosphere.blog.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.blogosphere.blog.dto.PostCreateDto;
import com.blogosphere.blog.dto.PostDetailDto;
import com.blogosphere.blog.model.Post;

@Component
public class PostResourceAssembler implements ResourceAssembler<PostDetailDto, Resource<PostDetailDto>> {

	@Override
	public Resource<PostDetailDto> toResource(PostDetailDto post) {
		return new Resource<>(post,
			linkTo(methodOn(PostController.class).getPost(post.getId())).withSelfRel(),
			linkTo(methodOn(UserController.class).findAllPostsForUser(post.getAuthor().getId())).withRel("posts"));
	}
}