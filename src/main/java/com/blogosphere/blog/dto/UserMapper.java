package com.blogosphere.blog.dto;

import java.text.ParseException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blogosphere.blog.model.Post;
import com.blogosphere.blog.model.User;

@Component
public class UserMapper {

	@Autowired
	private ModelMapper modelMapper;

	public UserDetailDto convertToDto(User entity) {
		UserDetailDto dto = modelMapper.map(entity, UserDetailDto.class);
		return dto;
	}

	public User convertToEntity(UserCreateDto dto) throws ParseException {
		User entity = modelMapper.map(dto, User.class);
		return entity;
	}

	public PostDetailDto convertToDto(Post entity) {
		PostDetailDto dto = modelMapper.map(entity, PostDetailDto.class);
		return dto;
	}
}
