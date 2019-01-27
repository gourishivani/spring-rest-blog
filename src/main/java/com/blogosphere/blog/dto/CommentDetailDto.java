package com.blogosphere.blog.dto;

import java.time.ZonedDateTime;

import org.springframework.hateoas.core.Relation;

import lombok.Data;

@Data
@Relation(collectionRelation="data")
public class CommentDetailDto {
	private Long id;
	private String content;

	private UserDetailDto commentor;
	private PostDetailDto post;
	
	private ZonedDateTime created;
	private ZonedDateTime lastModified;
}
