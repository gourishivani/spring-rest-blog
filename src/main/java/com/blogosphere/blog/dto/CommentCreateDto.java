package com.blogosphere.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import lombok.Data;

@Data
public class CommentCreateDto {
	@NotBlank(message = "Please provide some content for your comment")
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE) // We are only allowing plain text
	@Size(message="Please provide a title between 2 to 300 characters",min = 2, max = 300)
	private String content;
	
	@NotNull(message="Please provide commentorId")
//	private Long commentorId; // ModelMapper has issues mapping this to the id.
	private UserDetailDto commentor;
	
	@NotNull(message="Please provide postId")
//	private Long postId;
	private PostDetailDto post;
}
