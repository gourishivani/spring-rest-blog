package com.blogosphere.blog.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.blogosphere.blog.dto.UserDetailDto;

@Component
public class UserResourceAssembler implements ResourceAssembler<UserDetailDto, Resource<UserDetailDto>> {

	@Override
	public Resource<UserDetailDto> toResource(UserDetailDto user) {
		return new Resource<>(user,
			linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel(),
			linkTo(methodOn(UserController.class).getAllUsersRestful()).withRel("users"));
	}
}