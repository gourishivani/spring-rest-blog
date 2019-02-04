package com.blogosphere.blog.dto;

import java.text.ParseException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blogosphere.blog.model.Comment;

@Component
public class CommentMapper {

	@Autowired
	private ModelMapper modelMapper;


	public Comment convertToEntity(CommentCreateDto dto) throws ParseException {
		Comment entity = modelMapper.map(dto, Comment.class);
		return entity;
	}
	
	public CommentDetailDto convertToDto(Comment entity) {
		CommentDetailDto dto = modelMapper.map(entity, CommentDetailDto.class);
		return dto;
	}
}
