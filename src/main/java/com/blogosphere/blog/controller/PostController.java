package com.blogosphere.blog.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
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
import com.blogosphere.blog.dto.PostMapper;
import com.blogosphere.blog.dto.UserDetailDto;
import com.blogosphere.blog.exception.EntityNotFoundException;
import com.blogosphere.blog.jwt.AuthenticatedUser;
import com.blogosphere.blog.jwt.JwtUserDetails;
import com.blogosphere.blog.model.Post;
import com.blogosphere.blog.model.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

//@RequestMapping(path = "/posts")
@CrossOrigin(origins="http://localhost:4200")
@RestController
@Api(value="/posts")
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
    private PostMapper modelMapper;
 
	@ApiOperation(value = "View a post detail",tags= {"posts"},
			response = Resource.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved resource"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
	        @ApiResponse(code = 500, message = "Server error")
	})
	@GetMapping(path = "/posts/{postId}")
	public Resource<PostDetailDto> getPost(@PathVariable Long postId) {
		Post post = postService.find(postId).orElseThrow(() -> new EntityNotFoundException(postId, Post.class));
		return postAssembler.toResource(this.modelMapper.convertToDto(post));
	}
	
	@ApiOperation(value = "Create post", tags= {"posts"}, response = ResponseEntity.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 201, message = "Successfully created"),
	        @ApiResponse(code = 400, message = "Validation check failed"),
	        @ApiResponse(code = 401, message = "You are not authorized create the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 409, message = "Duplicate entry for email. Email already exists"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
	        @ApiResponse(code = 500, message = "Server error")
	})
	@PostMapping("/posts")
	public ResponseEntity<?> createPost(@RequestBody @Valid PostCreateDto dto) throws URISyntaxException, ParseException {
		Post post = this.modelMapper.convertToEntity(dto);
		JwtUserDetails user = AuthenticatedUser.getAuthenticatedUser();
		post.setAuthor(new User(user.getId()));
		Post created = postService.createPost(post);
		Resource<PostDetailDto> resource = postAssembler.toResource(this.modelMapper.convertToDto(created));
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}
	
	@ApiOperation(value = "View ALL posts for a given user",tags= {"users"},
			response = Resources.class,
			notes="No pagination support yet, The post list is reverse chronologically sorted by date created")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 500, message = "Server error"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	@GetMapping(path = "/users/{userId}/posts")
	public ResponseEntity<List<PostDetailDto>> findAllPostsForUser(@PathVariable Long userId) {
		Sort sort = new Sort(Sort.Direction.DESC, "created");
		// NOTE: mapping over this list twice may not be optimal for performance. However, keeping this simple for now.
		List<PostDetailDto> userPosts = postService.findAllByAuthor(userId, sort).stream().map(entity -> this.modelMapper.convertToDto(entity)).
				collect(Collectors.toList());
		return new ResponseEntity<>(userPosts, HttpStatus.OK);
	}
	
	@ApiOperation(value = "View a Restful/HATEOAS compliant list of posts for a given user",tags= {"users"},
			response = Resources.class,
			notes="Demo Notes: The resource URL is not compliant with Restful API definitions. However, it is here just to demonstrate HATEOAS compliant restful list request."
					+ "Also, this will not be used by the front end to keep things simple")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	@GetMapping(path = "/users/{userId}/posts/restful")
	public Resources<Resource<PostDetailDto>> findAllPostsForUserRestful(@PathVariable Long userId) {
		
		Sort sort = new Sort(Sort.Direction.DESC, "created");
		// NOTE: mapping over this list twice may not be optimal for performance. However, keeping this simple for now.
		List<Resource<PostDetailDto>> userPosts = postService.findAllByAuthor(userId, sort).stream().map(entity -> this.modelMapper.convertToDto(entity)).map(postAssembler::toResource)
				.collect(Collectors.toList());
		return new Resources<>(userPosts, linkTo(methodOn(PostController.class).findAllPostsForUserRestful(userId)).withSelfRel());
	}
}
