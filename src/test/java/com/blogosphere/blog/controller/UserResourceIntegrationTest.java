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

import com.blogosphere.blog.SpringRestBlogApplication;
import com.blogosphere.blog.config.IntegrationTestConfig;
import com.blogosphere.blog.dao.UserRepository;
import com.blogosphere.blog.dto.PostDetailDto;
import com.blogosphere.blog.dto.PostMapper;
import com.blogosphere.blog.dto.RestApiErrorDto;
import com.blogosphere.blog.dto.UserCreateDto;
import com.blogosphere.blog.dto.UserDetailDto;
import com.blogosphere.blog.dto.UserMapper;
import com.blogosphere.blog.knownobjects.KnownObjects;
import com.blogosphere.blog.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringRestBlogApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { IntegrationTestConfig.class })

public class UserResourceIntegrationTest {

	@LocalServerPort
	private int port;

	private TestRestTemplate restTemplate;

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private PostMapper postMapper;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	UserRepository repository;

	private String[] fieldsToIgnore = new String[] { "id", "created", "lastModified" };

	private User user1;
	private User user2;

	@Before
	public void setup() {
		/**
		 * NOTE: This test requires minimal setup. So instead of re-creating the spring
		 * context every time by setting up the DirtiesContext, I am doing manual
		 * cleanup through the repository. This will also make the tests run faster.
		 */
		// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
		this.repository.deleteAll();

		restTemplate = new TestRestTemplate();

		perTestSetup();
	}

	private void perTestSetup() {
		this.user1 = this.repository.save(KnownObjects.knownUser1());
		this.user2 = this.repository.save(KnownObjects.knownUser2());

		assertThat(2l).isEqualTo(this.repository.count());
	}

	@Test
	public void getUserShould_returnUnknown_whenUserIdIsNonExistant() throws JSONException {
		ResponseEntity<UserDetailDto> response = restTemplate.exchange(createURLWithPort("/users/321"), HttpMethod.GET,
				null, UserDetailDto.class);
		assertThat(HttpStatus.NOT_FOUND).isEqualTo(response.getStatusCode());
	}

	@Test
	public void getUserShould_returnUser_whenUserIdIsExistant() throws JSONException {
		ResponseEntity<UserDetailDto> response = restTemplate
				.exchange(createURLWithPort("/users/" + this.user1.getId()), HttpMethod.GET, null, UserDetailDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(userMapper.convertToDto(KnownObjects.knownUser1())).isEqualToIgnoringGivenFields(response.getBody(),
				fieldsToIgnore);
	}

	@Test
	public void getAllUsersShould_returnList_whenUsersExist() throws JSONException {
		ResponseEntity<List<UserDetailDto>> response = restTemplate.exchange(createURLWithPort("/users"),
				HttpMethod.GET, null, new ParameterizedTypeReference<List<UserDetailDto>>() {
				});
		UserDetailDto userDto1 = this.userMapper.convertToDto(this.user1);
		UserDetailDto userDto2 = this.userMapper.convertToDto(this.user2);
		assertThat(response.getBody()).hasSize(2);
		assertThat(response.getBody()).contains(userDto1, userDto2);
	}

	@Test
	public void postUserShould_returnCreated_whenUserCreationSuccesful() throws JSONException {
		UserCreateDto user = new UserCreateDto("StarkIsComing", "arya", "arya1@winterfell.com", "A really cold place");
		UserDetailDto expected = convertToDto(user);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<UserCreateDto> request = new HttpEntity<>(user, headers);
		ResponseEntity<UserDetailDto> result = this.restTemplate.postForEntity(createURLWithPort("/users"), request,
				UserDetailDto.class);

		assertThat(HttpStatus.CREATED).isEqualTo(result.getStatusCode());
		assertThat(expected).isEqualToIgnoringGivenFields(result.getBody(), fieldsToIgnore);
	}

	@Test
	public void postUserShould_returnError_whenDuplicateEmailIsUsed() throws JSONException {
		UserCreateDto user = new UserCreateDto("StarkIsComing", "arya", "aryadupe@winterfell.com",
				"A really cold place");

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<UserCreateDto> request = new HttpEntity<>(user, headers);
		ResponseEntity<UserDetailDto> result = this.restTemplate.postForEntity(createURLWithPort("/users"), request,
				UserDetailDto.class);
		assertThat(HttpStatus.CREATED).isEqualTo(result.getStatusCode());

		ResponseEntity<RestApiErrorDto> errorResponse = postUserCreateAndExpectError(user, HttpStatus.CONFLICT);
		assertThat(errorResponse.getBody().getMessage().toLowerCase().contains("email"));
	}

	@Test
	public void postUserShould_returnError_whenMissingPassword() throws JSONException {
		UserCreateDto user = new UserCreateDto(null, "arya", "arya2@winterfell.com", "A really cold place");

		ResponseEntity<RestApiErrorDto> result = postUserCreateAndExpect400(user);
		assertThat(UserCreateDto.MISSING_PASSWORD_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postUserShould_returnError_whenMissingName() throws JSONException {
		UserCreateDto user = new UserCreateDto("mypass", null, "arya3@winterfell.com", "A really cold place");

		ResponseEntity<RestApiErrorDto> result = postUserCreateAndExpect400(user);
		assertThat(UserCreateDto.MISSING_NAME_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postUserShould_returnError_whenNameExceedsMax() throws JSONException {

		UserCreateDto user = new UserCreateDto("mypass", RandomStringUtils.random(101), "arya4@winterfell.com",
				"A really cold place");

		ResponseEntity<RestApiErrorDto> result = postUserCreateAndExpect400(user);
		assertThat(UserCreateDto.BADFORMAT_NAME_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postUserShould_returnError_whenMissingEmail() throws JSONException {
		UserCreateDto user = new UserCreateDto("mypass", "arya", null, "A really cold place");

		ResponseEntity<RestApiErrorDto> result = postUserCreateAndExpect400(user);
		assertThat(UserCreateDto.MISSING_EMAIL_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postUserShould_returnError_whenEmailIsBadFormat() throws JSONException {
		UserCreateDto user = new UserCreateDto("mypass", "arya", "arya_no_at_symbol", "A really cold place");

		ResponseEntity<RestApiErrorDto> result = postUserCreateAndExpect400(user);
		assertThat(UserCreateDto.BADFORMAT_EMAIL_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postUserShould_returnError_whenMissingSpaceName() throws JSONException {
		UserCreateDto user = new UserCreateDto("mypass", "arya", "arya6@winterfell.com", null);

		ResponseEntity<RestApiErrorDto> result = postUserCreateAndExpect400(user);
		assertThat(UserCreateDto.MISSING_SPACENAME_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	@Test
	public void postUserShould_returnError_whenPsaceNameViolatesLength() throws JSONException {
		UserCreateDto user = new UserCreateDto("mypass", "arya", "arya7@winterfell.com", "d");

		ResponseEntity<RestApiErrorDto> result = postUserCreateAndExpect400(user);
		assertThat(UserCreateDto.BADFORMAT_SPACENAME_MESSAGE).isEqualTo(result.getBody().getMessage());
	}

	private ResponseEntity<RestApiErrorDto> postUserCreateAndExpect400(UserCreateDto user) {
		return postUserCreateAndExpectError(user, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<RestApiErrorDto> postUserCreateAndExpectError(UserCreateDto user, HttpStatus status) {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<UserCreateDto> request = new HttpEntity<>(user, headers);
		ResponseEntity<RestApiErrorDto> result = this.restTemplate.postForEntity(createURLWithPort("/users"), request,
				RestApiErrorDto.class);

		assertThat(status).isEqualTo(result.getStatusCode());
		return result;
	}

	public UserDetailDto convertToDto(UserCreateDto entity) {
		UserDetailDto dto = modelMapper.map(entity, UserDetailDto.class);
		return dto;
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}
