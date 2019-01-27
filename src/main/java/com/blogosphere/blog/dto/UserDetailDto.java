package com.blogosphere.blog.dto;

import java.time.ZonedDateTime;

import org.springframework.hateoas.core.Relation;

import lombok.Data;

@Data
@Relation(collectionRelation="data")
public class UserDetailDto {
	private String name;
	private String email;
	private String spaceName;
	private Long id;
	private ZonedDateTime created;
	private ZonedDateTime lastModified;
}
