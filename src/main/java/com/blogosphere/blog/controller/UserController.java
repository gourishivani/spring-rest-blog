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
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blogosphere.blog.biz.PostService;
import com.blogosphere.blog.biz.UserService;
import com.blogosphere.blog.dto.PostDetailDto;
import com.blogosphere.blog.dto.UserCreateDto;
import com.blogosphere.blog.dto.UserDetailDto;
import com.blogosphere.blog.dto.UserMapper;
import com.blogosphere.blog.exception.EntityNotFoundException;
import com.blogosphere.blog.model.Post;
import com.blogosphere.blog.model.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin(origins="http://localhost:4200")
//@RequestMapping(path = "/users")
@Api(value="/users")
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
	private UserMapper userMapper;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * In the spirit of being Restful, this path is not named as /register.
	 * @param userDto
	 * @return
	 * @throws URISyntaxException
	 * @throws ParseException
	 */
	@ApiOperation(value = "Create in the system", tags= {"users"}, response = ResponseEntity.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 201, message = "Successfully created"),
	        @ApiResponse(code = 400, message = "Validation check failed"),
	        @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 409, message = "Duplicate entry for email. Email already exists"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
	        @ApiResponse(code = 500, message = "Server error")
	})
	@PostMapping("/users")
	public ResponseEntity<?> createUser(@RequestBody @Valid UserCreateDto userDto) throws URISyntaxException, ParseException {
		User user = this.userMapper.convertToEntity(userDto);
		user.setPasswordHash(bCryptPasswordEncoder.encode(user.getPasswordHash()));
		User created = userService.createUser(user);
		Resource<UserDetailDto> resource = userAssembler.toResource(this.userMapper.convertToDto(created));
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}

	@ApiOperation(value = "View a list of users in the system",
			response = Resources.class, tags= {"users"},
			notes="No pagination support yet, The user list is reverse chronologically sorted by date created")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
	        @ApiResponse(code = 500, message = "Server error")
	})
	@GetMapping("/users")
	public ResponseEntity<List<UserDetailDto>> getAllUsers() {
		Sort sort = new Sort(Sort.Direction.DESC, "created");
		List<UserDetailDto> users = userService.findAll(sort).stream().map(user -> this.userMapper.convertToDto(user)).collect(Collectors.toList());
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@ApiOperation(value = "View a Restful/HATEOAS compliant list of users in the system",
			response = Resources.class, tags= {"users"},
			notes="Demo Notes: The resource URL is not compliant with Restful API definitions. However, it is here just to demonstrate HATEOAS compliant restful list request."
					+ "Also, this will not be used by the front end to keep things simple")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
	        @ApiResponse(code = 500, message = "Server error")
	})
	@GetMapping("/users/restful")
	public Resources<Resource<UserDetailDto>> getAllUsersRestful() {
		Sort sort = new Sort(Sort.Direction.DESC, "created");
		List<Resource<UserDetailDto>> users = userService.findAll(sort).stream().map(user -> this.userMapper.convertToDto(user)).map(userAssembler::toResource)
				.collect(Collectors.toList());
		/**
		 *  NOTE: mapping over this list twice may not be optimal for performance. However, keeping this simple for now.
		 */
		
		return new Resources<>(users, linkTo(methodOn(UserController.class).getAllUsersRestful()).withSelfRel());
	}
	
	@ApiOperation(value = "View a user detail",tags= {"users"},
			response = Resource.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved resource"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
	        @ApiResponse(code = 500, message = "Server error")
	})
	@GetMapping("/users/{userId}")
	Resource<UserDetailDto> getUser(@PathVariable Long userId) {
		User user = userService.find(userId).orElseThrow(() -> new EntityNotFoundException(userId, User.class));
		return userAssembler.toResource(this.userMapper.convertToDto(user));
	}	
}
