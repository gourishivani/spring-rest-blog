package com.blogosphere.blog.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
//import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResourceAccessException;

import com.blogosphere.blog.SpringRestBlogApplication;
import com.blogosphere.blog.config.IntegrationTestConfig;
import com.blogosphere.blog.dao.CommentRepository;
import com.blogosphere.blog.dao.PostRepository;
import com.blogosphere.blog.dao.UserRepository;
import com.blogosphere.blog.dto.CommentCreateDto;
import com.blogosphere.blog.dto.CommentDetailDto;
import com.blogosphere.blog.dto.CommentMapper;
import com.blogosphere.blog.dto.PostMapper;
import com.blogosphere.blog.dto.RestApiErrorDto;
import com.blogosphere.blog.dto.UserDetailDto;
import com.blogosphere.blog.jwt.resource.JwtTokenRequest;
import com.blogosphere.blog.jwt.resource.JwtTokenResponse;
import com.blogosphere.blog.knownobjects.KnownObjects;
import com.blogosphere.blog.model.Comment;
import com.blogosphere.blog.model.Post;
import com.blogosphere.blog.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringRestBlogApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { IntegrationTestConfig.class })
public class CommentResourceIntegrationTest {

	// Use large number as our test does not do a whole lot of entity creation
	private static final Long nonExistantId = Long.MAX_VALUE;

	@LocalServerPort
	private int port;

	private TestRestTemplate restTemplate;

	@Autowired
	private ModelMapper objectMapper;
	
	@Autowired
	private PostMapper postMapper;

	@Autowired
	private CommentMapper commentMapper;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PostRepository postRepository;

	@Autowired
	CommentRepository commentRepository;

	private String[] fieldsToIgnore = new String[] { "id", "created", "lastModified" };

	private User user1;
	private User user2;

	private Post post1;
	private Post post2;
	
	private Comment comment1;
	private Comment comment2;
	private Comment comment3;
	

	private Long loggedInUserId;
	private String token;

	@Before
	public void setup() {
		/**
		 * NOTE: This test requires minimal setup. So instead of re-creating the spring
		 * context every time by setting up the DirtiesContext, I am doing manual
		 * cleanup through the repository. This will also make the tests run faster.
		 */
		// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
		this.commentRepository.deleteAll();
		this.postRepository.deleteAll();
		this.userRepository.deleteAll();

		restTemplate = new TestRestTemplate();

		perTestSetup();
	}

	
	private void perTestSetup() {
		this.user1 = this.userRepository.save(KnownObjects.knownUser1());
		this.user2 = this.userRepository.save(KnownObjects.knownUser2());

		assertThat(2l).isEqualTo(this.userRepository.count());

		this.post1 = this.postRepository.save(KnownObjects.knownPost1(this.user1));
		this.post2 = this.postRepository.save(KnownObjects.knownPost2(this.user2));
		assertThat(2l).isEqualTo(this.postRepository.count());
		
		this.comment1 = this.commentRepository.save(KnownObjects.knownComment1(this.post1, this.user1));
		this.comment2 = this.commentRepository.save(KnownObjects.knownComment2(this.post2, this.user1));
		this.comment3 = this.commentRepository.save(KnownObjects.knownComment3(this.post2, this.user1));
		
		assertThat(3l).isEqualTo(this.commentRepository.count());
		
		authenticateUser(this.user1);
	}

	private void authenticateUser(User user1) {
		JwtTokenRequest tokenRequest = new JwtTokenRequest(user1.getEmail(), KnownObjects.KNOWN_USER1_PASSWORD);
		ResponseEntity<JwtTokenResponse> result = this.restTemplate.postForEntity(
				createURLWithPort("/authenticate"), tokenRequest,
				JwtTokenResponse.class);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody().getToken()).isNotNull();
		assertThat(result.getBody().getUser()).isNotNull();
		
		this.token = result.getBody().getToken();
		this.loggedInUserId = result.getBody().getUser().getId();
	}

	public void getCommentShould_returnUnknown_whenCommentIdIsNonExistant() throws JSONException {
		ResponseEntity<CommentDetailDto> response = restTemplate.exchange(createURLWithPort("/comments/3210"), HttpMethod.GET,
				null, CommentDetailDto.class);
		assertThat(HttpStatus.NOT_FOUND).isEqualTo(response.getStatusCode());
	}

	@Test
	public void getCommentShould_returnComment_whenUserExists() throws JSONException {
		ResponseEntity<CommentDetailDto> response = restTemplate
				.exchange(createURLWithPort("/comments/" + this.comment1.getId()), HttpMethod.GET, null, CommentDetailDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(commentMapper.convertToDto(this.comment1)).isEqualToIgnoringGivenFields(response.getBody(),
				fieldsToIgnore);
	}

	@Test
	public void getAllCommentsForPostShould_returnList_whenPostExists() throws JSONException {
		ResponseEntity<List<CommentDetailDto>> response = restTemplate.exchange(
				createURLWithPort("/posts/" + this.post2.getId() + "/comments"), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<CommentDetailDto>>() {
				});
		CommentDetailDto dto1 = this.commentMapper.convertToDto(this.comment2);
		CommentDetailDto dto2 = this.commentMapper.convertToDto(this.comment3);
		assertThat(response.getBody()).hasSize(2);
		assertThat(response.getBody()).contains(dto1, dto2);
	}

	@Test
	public void getAllCommentsForUserShould_returnEmptyList_whenUserIsNonExistant() throws JSONException {
		ResponseEntity<List<CommentDetailDto>> response = restTemplate.exchange(
				createURLWithPort("/posts/" + nonExistantId + "/comments"), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<CommentDetailDto>>() {
				});
		assertThat(response.getBody()).hasSize(0);
	}

	@Test (expected=ResourceAccessException.class)
	public void postCommentShould_return401_whenUnauthenticated() throws JSONException {
		CommentCreateDto post = new CommentCreateDto("New Comment Title", this.postMapper.convertToDto(this.post1));

		HttpEntity<CommentCreateDto> request = new HttpEntity<>(post, new HttpHeaders());
		
		restTemplate.exchange(
				createURLWithPort("/comments/"), HttpMethod.POST, request, Object.class);
	}

	
	@Test
	public void postCommentShould_returnCreated_whenCreationSuccesful() throws JSONException {
		CommentCreateDto post = new CommentCreateDto("New Comment Title", this.postMapper.convertToDto(this.post1));

		CommentDetailDto expected = convertToDto(post);
		
		UserDetailDto author = new UserDetailDto();
		author.setId(this.loggedInUserId);
		expected.setCommentor(author);
		
		HttpHeaders headers = setUpAuthHeaders();
		
		HttpEntity<CommentCreateDto> request = new HttpEntity<>(post, headers);
		ResponseEntity<CommentDetailDto> result = this.restTemplate.postForEntity(createURLWithPort("/comments"), request,
				CommentDetailDto.class);

		assertThat(HttpStatus.CREATED).isEqualTo(result.getStatusCode());
		assertThat(expected).isEqualToIgnoringGivenFields(result.getBody(), fieldsToIgnore);
	}

	private CommentDetailDto convertToDto(CommentCreateDto post) {
		CommentDetailDto dto = objectMapper.map(post, CommentDetailDto.class);
		return dto;
	}

	@Test
	public void postCommentShould_returnError_whenMissingTitle() throws JSONException {
		CommentCreateDto post = new CommentCreateDto(null, this.postMapper.convertToDto(this.post1));

		ResponseEntity<RestApiErrorDto> result = postCommentCreateAndExpect400(post);
		assertThat(CommentCreateDto.MISSING_CONTENT_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postCommentShould_returnError_whenMissingDescription() throws JSONException {
		CommentCreateDto post = new CommentCreateDto("New Comment Title", null);

		ResponseEntity<RestApiErrorDto> result = postCommentCreateAndExpect400(post);
		assertThat(CommentCreateDto.MISSING_POSTID_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postCommentShould_returnError_whenTitleExceedsMax() throws JSONException {
		CommentCreateDto post = new CommentCreateDto(RandomStringUtils.random(301), this.postMapper.convertToDto(this.post1));

		ResponseEntity<RestApiErrorDto> result = postCommentCreateAndExpect400(post);
		assertThat(CommentCreateDto.BADFORMAT_CONTENT_MESSAGE).isEqualTo(result.getBody().getMessage());
	}
	
	private ResponseEntity<RestApiErrorDto> postCommentCreateAndExpect400(CommentCreateDto user) {
		return postCommentCreateAndExpectError(user, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<RestApiErrorDto> postCommentCreateAndExpectError(CommentCreateDto post, HttpStatus status) {
		HttpHeaders headers = setUpAuthHeaders();
		
		HttpEntity<CommentCreateDto> request = new HttpEntity<>(post, headers);
		ResponseEntity<RestApiErrorDto> result = this.restTemplate.postForEntity(createURLWithPort("/comments"), request,
				RestApiErrorDto.class);

		assertThat(status).isEqualTo(result.getStatusCode());
		return result;
	}

	private HttpHeaders setUpAuthHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.token);
		return headers;
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
}
