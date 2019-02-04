package com.blogosphere.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {
	
	public static final String MISSING_CONTENT_MESSAGE = "Please provide some content for your comment";
	public static final String MISSING_POSTID_MESSAGE = "Please provide postId";
	
	public static final String BADFORMAT_CONTENT_MESSAGE = "Please provide a title between 2 to 300 characters";
	
	@NotBlank(message = MISSING_CONTENT_MESSAGE)
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE) // We are only allowing plain text
	@Size(message=BADFORMAT_CONTENT_MESSAGE, min = 2, max = 300)
	private String content;
	
	@NotNull(message=MISSING_POSTID_MESSAGE)
	private PostDetailDto post;
}
