package com.blogosphere.blog.dto;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;

import com.blogosphere.blog.core.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(content = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestApiErrorDto {
	private HttpStatus errorCode;
	private String message;
	private ZonedDateTime timestamp;
	private String details;
	
	public RestApiErrorDto(HttpStatus errorCode, String message, String details) {
		this(errorCode, message, Utils.now(), details);
	}
	
	public RestApiErrorDto(HttpStatus errorCode, String message) {
		this(errorCode, message, Utils.now(), null);
	}
}
