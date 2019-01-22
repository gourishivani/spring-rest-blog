package com.blogosphere.blog.vo;

import java.time.ZonedDateTime;

import com.blogosphere.blog.core.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonInclude(content = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RestApiErrorVo {
	private String errorCode;
	private String message;
	private ZonedDateTime timestamp;
	private String details;
	
	public RestApiErrorVo(String errorCode, String message, String details) {
		this.errorCode = errorCode;
		this.message = message;
		this.details = details;
		this.timestamp = Utils.now();
	}

	public RestApiErrorVo() {
	}
}
