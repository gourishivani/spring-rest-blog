package com.blogosphere.blog.jwt.resource;

import java.io.Serializable;

import com.blogosphere.blog.dto.UserDetailDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor // ObjectMapper needs this
@Getter
public class JwtTokenResponse implements Serializable {

	private static final long serialVersionUID = 8317676219297719109L;

	private String token;
	private UserDetailDto user;
}
