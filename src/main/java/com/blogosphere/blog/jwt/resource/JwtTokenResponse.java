package com.blogosphere.blog.jwt.resource;

import java.io.Serializable;

import com.blogosphere.blog.dto.UserDetailDto;

public class JwtTokenResponse implements Serializable {

	private static final long serialVersionUID = 8317676219297719109L;

	private final String token;

	private final UserDetailDto user;

	public JwtTokenResponse(UserDetailDto user, String token) {
		this.token = token;
		this.user = user;
	}

	public String getToken() {
		return this.token;
	}
	
	public UserDetailDto getUser() {
		return user;
	}
}
