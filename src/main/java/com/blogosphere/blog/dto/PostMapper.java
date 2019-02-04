package com.blogosphere.blog.dto;

import java.text.ParseException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blogosphere.blog.model.Comment;
import com.blogosphere.blog.model.Post;

@Component
public class PostMapper {

	@Autowired
	private ModelMapper modelMapper;


	public PostDetailDto convertToDto(Post entity) {
		PostDetailDto dto = modelMapper.map(entity, PostDetailDto.class);
		return dto;
	}
	
	public Post convertToEntity(PostCreateDto dto) throws ParseException {
		Post entity = modelMapper.map(dto, Post.class);
		return entity;
	}
	
	public CommentDetailDto convertToDto(Comment entity) {
		CommentDetailDto dto = modelMapper.map(entity, CommentDetailDto.class);
		return dto;
	}
}
