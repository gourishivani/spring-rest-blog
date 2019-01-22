package com.blogosphere.blog.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.blogosphere.blog.model.Post;

@Component
public class PostResourceAssembler implements ResourceAssembler<Post, Resource<Post>> {

	@Override
	public Resource<Post> toResource(Post post) {
		return new Resource<>(post,
			linkTo(methodOn(UserController.class).getUser(post.getId())).withSelfRel(),
			linkTo(methodOn(UserController.class).getAllUsers()).withRel("posts"));
	}
}