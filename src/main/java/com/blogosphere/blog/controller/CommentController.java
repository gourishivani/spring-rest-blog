package com.blogosphere.blog.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogosphere.blog.biz.CommentService;
import com.blogosphere.blog.dto.CommentCreateDto;
import com.blogosphere.blog.dto.CommentDetailDto;
import com.blogosphere.blog.exception.EntityNotFoundException;
import com.blogosphere.blog.model.Comment;
import com.blogosphere.blog.model.Post;
import com.blogosphere.blog.model.User;

@RequestMapping(path = "/comments")
@RestController
@CrossOrigin(origins="http://localhost:4200")
public class CommentController {
	@Autowired
	public CommentService commentService;
	@Autowired
	public CommentResourceAssembler commentAssembler;
	@Autowired
	public ModelMapper modelMapper;

	@GetMapping(path = "/{commentId}")
	public Resource<CommentDetailDto> getComment(@PathVariable Long commentId) {
		Comment comment = commentService.find(commentId).orElseThrow(() -> new EntityNotFoundException(commentId, Comment.class));
		return commentAssembler.toResource(convertToDto(comment));
	}
	
	@PostMapping
	public ResponseEntity<?> createComment(@RequestBody @Valid CommentCreateDto commentDto) throws URISyntaxException, ParseException {
		Comment comment = convertToEntity(commentDto);
		Comment created = commentService.createComment(comment);
		Resource<CommentDetailDto> resource = commentAssembler.toResource(convertToDto(created));
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}

	private CommentDetailDto convertToDto(Comment entity) {
		CommentDetailDto dto = modelMapper.map(entity, CommentDetailDto.class);
		return dto;
	}
	
	private Comment convertToEntity(CommentCreateDto dto) throws ParseException {
		Comment entity = modelMapper.map(dto, Comment.class);
		return entity;
	}
}
