package com.blogosphere.blog.dto;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class CommentDetailDto {
	private Long id;
	private String content;

	private UserDetailDto commentor;
	private PostDetailDto post;
	
	private ZonedDateTime created;
	private ZonedDateTime lastModified;
}
