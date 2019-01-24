package com.blogosphere.blog.dto;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class UserDetailDto {
	private String name;
	private String email;
	private String spaceName;
	private Long id;
	private ZonedDateTime created;
	private ZonedDateTime lastModified;
}
