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
import org.springframework.data.domain.Sort;
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

import com.blogosphere.blog.biz.PostService;
import com.blogosphere.blog.biz.UserService;
import com.blogosphere.blog.dto.PostCreateDto;
import com.blogosphere.blog.dto.PostDetailDto;
import com.blogosphere.blog.dto.UserCreateDto;
import com.blogosphere.blog.dto.UserDetailDto;
import com.blogosphere.blog.exception.EntityNotFoundException;
import com.blogosphere.blog.model.Post;
import com.blogosphere.blog.model.User;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping(path = "/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PostService postService;

	@Autowired
	private UserResourceAssembler userAssembler;
	
	@Autowired
	private PostResourceAssembler postAssembler;

	@Autowired
    private ModelMapper modelMapper;
 
	@PostMapping
	public ResponseEntity<?> createUser(@RequestBody @Valid UserCreateDto userDto) throws URISyntaxException, ParseException {
		User user = convertToEntity(userDto);
		User created = userService.createUser(user);
		Resource<UserDetailDto> resource = userAssembler.toResource(convertToDto(created));
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}

	@GetMapping
//	Goes inside method @RequestParam String sort, @RequestParam String order
	public Resources<Resource<UserDetailDto>> getAllUsers() {
//		@PathVariable("page") int page,
//        @PathVariable("size") int size, 
//        @PathVariable("sortDir") String sortDir, 
//        @PathVariable("sort") String sort
		Sort sort = new Sort(Sort.Direction.DESC, "created");
		List<Resource<UserDetailDto>> users = userService.findAll(sort).stream().map(user -> convertToDto(user)).map(userAssembler::toResource)
				.collect(Collectors.toList());
		// NOTE: mapping over this list twice may not be optimal for performance. However, keeping this simple for now.
		return new Resources<>(users, linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
	}

	@GetMapping("/{userId}")
	Resource<UserDetailDto> getUser(@PathVariable Long userId) {
		User user = userService.find(userId).orElseThrow(() -> new EntityNotFoundException(userId, User.class));
		return userAssembler.toResource(convertToDto(user));
	}
	
	// Post related
	@GetMapping(path = "/{userId}/posts")
	public Resources<Resource<PostDetailDto>> findAllPostsForUser(@PathVariable Long userId) {

		// NOTE: mapping over this list twice may not be optimal for performance. However, keeping this simple for now.
		List<Resource<PostDetailDto>> userPosts = postService.findAllByAuthor(userId, new PageRequest(1, 100)).stream().map(entity -> convertToDto(entity)).map(postAssembler::toResource)
				.collect(Collectors.toList());
		return new Resources<>(userPosts, linkTo(methodOn(UserController.class).findAllPostsForUser(userId)).withSelfRel());
	}

	private UserDetailDto convertToDto(User entity) {
		UserDetailDto dto = modelMapper.map(entity, UserDetailDto.class);
//	    postDto.setSubmissionDate(user.getSubmissionDate(), 
//	        userService.getCurrentUser().getPreference().getTimezone());
	    return dto;
	}
	
	private User convertToEntity(UserCreateDto dto) throws ParseException {
		User entity = modelMapper.map(dto, User.class);
		return entity;
	}
	
	private PostDetailDto convertToDto(Post entity) {
		PostDetailDto dto = modelMapper.map(entity, PostDetailDto.class);
//	    postDto.setSubmissionDate(user.getSubmissionDate(), 
//	        userService.getCurrentUser().getPreference().getTimezone());
		return dto;
	}
	
	private Post convertToEntity(PostCreateDto dto) throws ParseException {
		Post entity = modelMapper.map(dto, Post.class);
		return entity;
	}
}
