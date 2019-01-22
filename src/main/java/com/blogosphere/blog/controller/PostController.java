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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogosphere.blog.biz.CommentService;
import com.blogosphere.blog.biz.PostService;
import com.blogosphere.blog.exception.EntityNotFoundException;
import com.blogosphere.blog.model.Comment;
import com.blogosphere.blog.model.Post;

@RequestMapping(path = "/posts")
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

	@GetMapping(path = "/{postId}")
	public Resource<Post> getPost(@PathVariable Long postId) {
		Post post = postService.find(postId).orElseThrow(() -> new EntityNotFoundException(postId, Post.class));
		return postAssembler.toResource(post);
	}
	
	@PostMapping(value = "/{postId}/comments")
	// Convert a predefined exception to an HTTP Status code
	// This exception is always thrown. But it also creates the user. Yikes!
//	@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Email Already Exists") // 409
//	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> createComment(@RequestBody @Valid Comment user) throws URISyntaxException {
		Comment created = commentService.createComment(user);
		Resource<Comment> resource = commentAssembler.toResource(created);
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}

	@GetMapping(path = "/{postId}/comments")
//	Goes inside method @RequestParam String sort, @RequestParam String order
	public Resources<Resource<Comment>> getAllComments(@PathVariable Long postId) {
		List<Resource<Comment>> employees = commentService.findAllByPost(postId, new PageRequest(1, 100)).stream().map(commentAssembler::toResource)
				.collect(Collectors.toList());
		return new Resources<>(employees, linkTo(methodOn(PostController.class).getAllComments(postId)).withSelfRel());
	}
}
