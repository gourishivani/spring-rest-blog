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
import org.springframework.web.bind.annotation.RestController;

import com.blogosphere.blog.biz.CommentService;
import com.blogosphere.blog.dto.CommentCreateDto;
import com.blogosphere.blog.dto.CommentDetailDto;
import com.blogosphere.blog.dto.CommentMapper;
import com.blogosphere.blog.exception.EntityNotFoundException;
import com.blogosphere.blog.jwt.AuthenticatedUser;
import com.blogosphere.blog.jwt.JwtUserDetails;
import com.blogosphere.blog.model.Comment;
import com.blogosphere.blog.model.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@Api(value="/comments")
public class CommentController {
	@Autowired
	public CommentService commentService;
	@Autowired
	public CommentResourceAssembler commentAssembler;
	@Autowired
	public CommentMapper modelMapper;


	@ApiOperation(value = "View a comment detail",tags= {"comments"},
			response = Resource.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved resource"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
	        @ApiResponse(code = 500, message = "Server error")
	})
	@GetMapping(path = "/comments/{commentId}")
	public Resource<CommentDetailDto> getComment(@PathVariable Long commentId) {
		Comment comment = commentService.find(commentId).orElseThrow(() -> new EntityNotFoundException(commentId, Comment.class));
		return commentAssembler.toResource(this.modelMapper.convertToDto(comment));
	}
	
	@ApiOperation(value = "Create in the system", tags= {"comments"}, response = ResponseEntity.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 201, message = "Successfully created"),
	        @ApiResponse(code = 400, message = "Validation check failed"),
	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
	        @ApiResponse(code = 500, message = "Server error")
	})
	@PostMapping("/comments")
	public ResponseEntity<?> createComment(@RequestBody @Valid CommentCreateDto commentDto) throws URISyntaxException, ParseException {
		Comment comment = this.modelMapper.convertToEntity(commentDto);
		JwtUserDetails user = AuthenticatedUser.getAuthenticatedUser();
		comment.setCommentor(new User(user.getId()));

		Comment created = commentService.createComment(comment);
		Resource<CommentDetailDto> resource = commentAssembler.toResource(this.modelMapper.convertToDto(created));
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}
	
	@ApiOperation(value = "View a Restful/HATEOAS compliant list of comments for the given postId",
			response = Resources.class, tags= {"posts"},
			notes="Demo Notes: The resource URL is not compliant with Restful API definitions. However, it is here just to demonstrate HATEOAS compliant restful list request."
					+ "Also, this will not be used by the front end to keep things simple")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Server error")
	})
	@GetMapping(path = "/posts/{postId}/comments")
	public ResponseEntity<List<CommentDetailDto>> getAllComments(@PathVariable Long postId) {
		Sort sort = new Sort(Sort.Direction.DESC, "created");
		List<CommentDetailDto> comments = commentService.findAllByPost(postId, sort).stream()
				.map(comment-> this.modelMapper.convertToDto(comment))
				.collect(Collectors.toList());
		
		return new ResponseEntity<>(comments, HttpStatus.OK);
	}
	
	@ApiOperation(value = "View a Restful/HATEOAS compliant list of comments for the given postId",
			response = Resources.class, tags= {"posts"},
			notes="Demo Notes: The resource URL is not compliant with Restful API definitions. However, it is here just to demonstrate HATEOAS compliant restful list request."
					+ "Also, this will not be used by the front end to keep things simple")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
	        @ApiResponse(code = 500, message = "Server error")
	})
	@GetMapping(path = "/posts/{postId}/comments/restful")
	public Resources<Resource<CommentDetailDto>> getAllCommentsRestful(@PathVariable Long postId) {
		Sort sort = new Sort(Sort.Direction.DESC, "created");
		List<Resource<CommentDetailDto>> comments = commentService.findAllByPost(postId, sort).stream()
				.map(comment-> this.modelMapper.convertToDto(comment)).map(commentAssembler::toResource)
				.collect(Collectors.toList());
		// NOTE: mapping over this list twice may not be optimal for performance. However, keeping this simple for now.
		return new Resources<>(comments, linkTo(methodOn(CommentController.class).getAllCommentsRestful(postId)).withSelfRel());
	}
}
