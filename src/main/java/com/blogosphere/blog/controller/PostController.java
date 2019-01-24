package com.blogosphere.blog.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogosphere.blog.biz.CommentService;
import com.blogosphere.blog.biz.PostService;
import com.blogosphere.blog.dto.CommentDetailDto;
import com.blogosphere.blog.dto.PostCreateDto;
import com.blogosphere.blog.dto.PostDetailDto;
import com.blogosphere.blog.exception.EntityNotFoundException;
import com.blogosphere.blog.model.Comment;
import com.blogosphere.blog.model.Post;

@RequestMapping(path = "/posts")
@CrossOrigin(origins="http://localhost:4200")
@RestController
public class PostController {
	
	@Autowired
	private PostService postService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private CommentResourceAssembler commentAssembler;

	@Autowired
	private PostResourceAssembler postAssembler;

	@Autowired
    private ModelMapper modelMapper;
 
	@GetMapping(path = "/{postId}")
	public Resource<PostDetailDto> getPost(@PathVariable Long postId) {
		Post post = postService.find(postId).orElseThrow(() -> new EntityNotFoundException(postId, Post.class));
		return postAssembler.toResource(convertToDto(post));
	}
	
	@GetMapping(path = "/{postId}/comments")
	public Resources<Resource<CommentDetailDto>> getAllComments(@PathVariable Long postId) {
		List<Resource<CommentDetailDto>> employees = commentService.findAllByPost(postId, new PageRequest(1, 100)).stream()
				.map(comment-> convertToDto(comment)).map(commentAssembler::toResource)
				.collect(Collectors.toList());
		// NOTE: mapping over this list twice may not be optimal for performance. However, keeping this simple for now.
		return new Resources<>(employees, linkTo(methodOn(PostController.class).getAllComments(postId)).withSelfRel());
	}
	
	@PostMapping
	public ResponseEntity<?> createPost(@RequestBody @Valid PostCreateDto dto) throws URISyntaxException, ParseException {
		Post post = convertToEntity(dto);
		Post created = postService.createPost(post);
		Resource<PostDetailDto> resource = postAssembler.toResource(convertToDto(created));
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}
	
	private PostDetailDto convertToDto(Post entity) {
		PostDetailDto dto = modelMapper.map(entity, PostDetailDto.class);
		return dto;
	}
	
	private Post convertToEntity(PostCreateDto dto) throws ParseException {
		Post entity = modelMapper.map(dto, Post.class);
		return entity;
	}
	
	private CommentDetailDto convertToDto(Comment entity) {
		CommentDetailDto dto = modelMapper.map(entity, CommentDetailDto.class);
		return dto;
	}
	
}
