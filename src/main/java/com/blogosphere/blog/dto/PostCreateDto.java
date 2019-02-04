package com.blogosphere.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateDto {
	
	public static final String MISSING_TITLE_MESSAGE = "Please provide a title for your post";
	public static final String MISSING_DESCRIPTION_MESSAGE = "Please provide a desciption for your post";
	
	public static final String BADFORMAT_TITLE_MESSAGE = "Please provide a title between 2 to 30 characters";
	public static final String BADFORMAT_DESCRIPTION_MESSAGE = "Please provide a title between 2 to 300 characters";
	
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Size(message=BADFORMAT_TITLE_MESSAGE, min = 2, max = 30)
	@NotBlank(message = MISSING_TITLE_MESSAGE)
	private String title;
	
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Size(message=BADFORMAT_DESCRIPTION_MESSAGE, min = 2, max = 300)
	@NotBlank(message = MISSING_DESCRIPTION_MESSAGE)
	private String description;
}
