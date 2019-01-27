package com.blogosphere.blog.dto;

import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;

@Data
@Relation(collectionRelation="data")
public class PostDetailDto {
	private String title;
	private String description;
	
	private Long id;
	
	private UserDetailDto author;
	
	private ZonedDateTime created;
	private ZonedDateTime lastModified;
}
