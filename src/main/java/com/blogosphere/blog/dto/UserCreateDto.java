package com.blogosphere.blog.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import lombok.Data;

@Data
public class UserCreateDto {
	@NotBlank(message = "Please provide a password")
	private String passwordHash;
	
	@NotBlank(message = "name cannot be blank")
	@Size(message="Please provide a title between 1 to 100 characters",min = 1, max = 100)
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	private String name;
	
	@NotBlank(message = "email cannot be blank")
	@Email(message = "Email is in invalid format")
	private String email;
	
	@NotBlank(message = "Please provide a name for your space")
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Size(message="Please provide a title between 2 to 30 characters", min = 2, max = 30)
	private String spaceName;
}
