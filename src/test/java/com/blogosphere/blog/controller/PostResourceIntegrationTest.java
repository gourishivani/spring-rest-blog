package com.blogosphere.blog.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.HttpRetryException;
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
import com.blogosphere.blog.dao.PostRepository;
import com.blogosphere.blog.dao.UserRepository;
import com.blogosphere.blog.dto.PostCreateDto;
import com.blogosphere.blog.dto.PostDetailDto;
import com.blogosphere.blog.dto.PostMapper;
import com.blogosphere.blog.dto.RestApiErrorDto;
import com.blogosphere.blog.dto.UserDetailDto;
import com.blogosphere.blog.jwt.resource.JwtTokenRequest;
import com.blogosphere.blog.jwt.resource.JwtTokenResponse;
import com.blogosphere.blog.knownobjects.KnownObjects;
import com.blogosphere.blog.model.Post;
import com.blogosphere.blog.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringRestBlogApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { IntegrationTestConfig.class })
public class PostResourceIntegrationTest {

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
	UserRepository userRepository;

	@Autowired
	PostRepository postRepository;

	private String[] fieldsToIgnore = new String[] { "id", "created", "lastModified" };

	private User user1;
	private User user2;

	private Post post1;
	private Post post2;

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
		this.post2 = this.postRepository.save(KnownObjects.knownPost2(this.user1));
		assertThat(2l).isEqualTo(this.postRepository.count());
		
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


	@Test
	public void getPostShould_returnUnknown_whenPostIdIsNonExistant() throws JSONException {
		ResponseEntity<PostDetailDto> response = restTemplate.exchange(createURLWithPort("/posts/3210"), HttpMethod.GET,
				null, PostDetailDto.class);
		assertThat(HttpStatus.NOT_FOUND).isEqualTo(response.getStatusCode());
	}

	@Test
	public void getPostShould_returnPost_whenUserExists() throws JSONException {
		ResponseEntity<PostDetailDto> response = restTemplate
				.exchange(createURLWithPort("/posts/" + this.post1.getId()), HttpMethod.GET, null, PostDetailDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(postMapper.convertToDto(this.post1)).isEqualToIgnoringGivenFields(response.getBody(),
				fieldsToIgnore);
	}

	@Test
	public void getAllPostsForUserShould_returnList_whenUserExists() throws JSONException {
		ResponseEntity<List<PostDetailDto>> response = restTemplate.exchange(
				createURLWithPort("/users/" + this.user1.getId() + "/posts"), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<PostDetailDto>>() {
				});
		PostDetailDto postDto1 = this.postMapper.convertToDto(this.post1);
		PostDetailDto postDto2 = this.postMapper.convertToDto(this.post2);
		assertThat(response.getBody()).hasSize(2);
		assertThat(response.getBody()).contains(postDto1, postDto2);
	}

	@Test
	public void getAllPostsForUserShould_returnEmptyList_whenUserIsNonExistant() throws JSONException {
		ResponseEntity<List<PostDetailDto>> response = restTemplate.exchange(
				createURLWithPort("/users/" + nonExistantId + "/posts"), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<PostDetailDto>>() {
				});
		assertThat(response.getBody()).hasSize(0);
	}

	@Test (expected=ResourceAccessException.class)
	public void postPostShould_return401_whenUnauthenticated() throws JSONException {
		PostCreateDto post = new PostCreateDto("New Post Title", "New Post Description");
		
		HttpEntity<PostCreateDto> request = new HttpEntity<>(post, new HttpHeaders());
		
		restTemplate.exchange(
				createURLWithPort("/posts/"), HttpMethod.POST, request, Object.class);
	}

	@Test
	public void postPostShould_returnCreated_whenCreationSuccesful() throws JSONException {
		PostCreateDto post = new PostCreateDto("New Post Title", "New Post Description");

		PostDetailDto expected = convertToDto(post);
		UserDetailDto author = new UserDetailDto();
		author.setId(this.loggedInUserId);
		expected.setAuthor(author);
		
		HttpHeaders headers = setUpAuthHeaders();
		
		HttpEntity<PostCreateDto> request = new HttpEntity<>(post, headers);
		ResponseEntity<PostDetailDto> result = this.restTemplate.postForEntity(createURLWithPort("/posts"), request,
				PostDetailDto.class);

		assertThat(HttpStatus.CREATED).isEqualTo(result.getStatusCode());
		assertThat(expected).isEqualToIgnoringGivenFields(result.getBody(), fieldsToIgnore);
	}

	private PostDetailDto convertToDto(PostCreateDto post) {
		PostDetailDto dto = objectMapper.map(post, PostDetailDto.class);
		return dto;
	}

	@Test
	public void postPostShould_returnError_whenMissingTitle() throws JSONException {
		PostCreateDto post = new PostCreateDto(null, "New Post Description");

		ResponseEntity<RestApiErrorDto> result = postPostCreateAndExpect400(post);
		assertThat(PostCreateDto.MISSING_TITLE_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postPostShould_returnError_whenMissingDescription() throws JSONException {
		PostCreateDto post = new PostCreateDto("Title", null);

		ResponseEntity<RestApiErrorDto> result = postPostCreateAndExpect400(post);
		assertThat(PostCreateDto.MISSING_DESCRIPTION_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postPostShould_returnError_whenTitleExceedsMax() throws JSONException {
		PostCreateDto post = new PostCreateDto(RandomStringUtils.random(101), "New Post Description");

		ResponseEntity<RestApiErrorDto> result = postPostCreateAndExpect400(post);
		assertThat(PostCreateDto.BADFORMAT_TITLE_MESSAGE).isEqualTo(result.getBody().getMessage());
	}
	
	@Test
	public void postPostShould_returnError_whenDescriptionExceedsMax() throws JSONException {
		PostCreateDto post = new PostCreateDto(RandomStringUtils.random(11), RandomStringUtils.random(301));
		
		ResponseEntity<RestApiErrorDto> result = postPostCreateAndExpect400(post);
		assertThat(PostCreateDto.BADFORMAT_DESCRIPTION_MESSAGE).isEqualTo(result.getBody().getMessage());
	}


	private ResponseEntity<RestApiErrorDto> postPostCreateAndExpect400(PostCreateDto user) {
		return postPostCreateAndExpectError(user, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<RestApiErrorDto> postPostCreateAndExpectError(PostCreateDto post, HttpStatus status) {
		HttpHeaders headers = setUpAuthHeaders();
		
		HttpEntity<PostCreateDto> request = new HttpEntity<>(post, headers);
		ResponseEntity<RestApiErrorDto> result = this.restTemplate.postForEntity(createURLWithPort("/posts"), request,
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
