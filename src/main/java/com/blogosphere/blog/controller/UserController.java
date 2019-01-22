package com.blogosphere.blog.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RestController;

import com.blogosphere.blog.biz.PostService;
import com.blogosphere.blog.biz.UserService;
import com.blogosphere.blog.exception.EntityNotFoundException;
import com.blogosphere.blog.model.Post;
import com.blogosphere.blog.model.User;

@RestController
@CrossOrigin(origins="http://localhost:4200")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PostService postService;

	@Autowired
	private UserResourceAssembler userAssembler;
	
	@Autowired
	private PostResourceAssembler postAssembler;

	@PostMapping(value = "/users")
	// Convert a predefined exception to an HTTP Status code
	// This exception is always thrown. But it also creates the user. Yikes!
//	@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Email Already Exists") // 409
//	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> createUser(@RequestBody @Valid User user) throws URISyntaxException {
		User created = userService.createUser(user);
		Resource<User> resource = userAssembler.toResource(created);
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}

	@GetMapping(path = "/users")
//	Goes inside method @RequestParam String sort, @RequestParam String order
	public Resources<Resource<User>> getAllUsers() {
		List<Resource<User>> employees = userService.findAll(new PageRequest(1, 100)).stream().map(userAssembler::toResource)
				.collect(Collectors.toList());
		return new Resources<>(employees, linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
	}

	@GetMapping("/users/{id}")
	Resource<User> getUser(@PathVariable Long id) {
		User user = userService.find(id).orElseThrow(() -> new EntityNotFoundException(id, User.class));
		return userAssembler.toResource(user);
	}
	
	// Post related
	@GetMapping(path = "/users/{id}/posts")
	public Resources<Resource<Post>> getUsersPosts(@PathVariable Long id) {
		List<Resource<Post>> userPosts = postService.findAllByAuthor(id, new PageRequest(1, 100)).stream().map(postAssembler::toResource)
				.collect(Collectors.toList());
		return new Resources<>(userPosts, linkTo(methodOn(UserController.class).getUsersPosts(id)).withSelfRel());
	}

	@PostMapping(value = "/users/{userId}/posts")
	public ResponseEntity<?> createPost(@RequestBody @Valid Post post) throws URISyntaxException {
		Post created = postService.createPost(post);
		Resource<Post> resource = postAssembler.toResource(created);
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}
}
