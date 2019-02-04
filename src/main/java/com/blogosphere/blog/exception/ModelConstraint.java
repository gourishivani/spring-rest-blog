package com.blogosphere.blog.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.blogosphere.blog.dto.RestApiErrorDto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Component
public class ModelConstraint {
	private Map<String, RestApiErrorDto> constraintMap = new HashMap<>();
	
	public ModelConstraint() {
		this.constraintMap.put("fk_comment_post_id", new RestApiErrorDto(HttpStatus.NOT_FOUND, "The post you have specified does not exist."));
		this.constraintMap.put("fk_comment_commentor_id", new RestApiErrorDto(HttpStatus.NOT_FOUND, "The Commentor you have specified does not exist."));
		this.constraintMap.put("fk_post_author_id", new RestApiErrorDto(HttpStatus.NOT_FOUND,"The author you have specified does not exist."));
		
		/*Unique Constraints*/
		this.constraintMap.put("unique_users_email_idx", new RestApiErrorDto(HttpStatus.CONFLICT, "Please provide a different email."));
	}
}
