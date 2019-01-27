package com.blogosphere.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import lombok.Data;

@Data
public class PostCreateDto {
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Size(message="Please provide a title between 2 to 30 characters", min = 2, max = 30)
	@NotBlank(message = "Please provide a title for your post")
	private String title;
	
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Size(message="Please provide a title between 2 to 300 characters",min = 2, max = 300)
	@NotBlank(message = "Please provide a desciption for your post")
	private String description;
	
//	private UserDetailDto author;
	@NotNull
	private Long authorId;
}
