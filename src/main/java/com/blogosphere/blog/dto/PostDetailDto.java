package com.blogosphere.blog.dto;

import java.time.ZonedDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class PostDetailDto {
	private String title;
	private String description;
	
	private Long id;
	
	private UserDetailDto author;
	
	private ZonedDateTime created;
	private ZonedDateTime lastModified;
}
