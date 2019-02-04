package com.blogosphere.blog.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
	public static final String MISSING_PASSWORD_MESSAGE = "Please provide a password";
	public static final String MISSING_NAME_MESSAGE = "Please provide a name";
	public static final String MISSING_EMAIL_MESSAGE = "Please provide your email";
	public static final String MISSING_SPACENAME_MESSAGE = "Please provide a space name";
	
	public static final String BADFORMAT_EMAIL_MESSAGE = "Email is in invalid format";
	public static final String BADFORMAT_SPACENAME_MESSAGE = "Please provide a spaceName between 2 to 30 characters";
	public static final String BADFORMAT_NAME_MESSAGE = "Please provide a name between 1 to 100 characters";
	
	
	@NotBlank(message = MISSING_PASSWORD_MESSAGE)
	private String passwordHash;
	
	@NotBlank(message = MISSING_NAME_MESSAGE)
	@Size(message=BADFORMAT_NAME_MESSAGE,min = 1, max = 100)
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	private String name;
	
	@NotBlank(message = MISSING_EMAIL_MESSAGE)
	@Email(message = BADFORMAT_EMAIL_MESSAGE)
	private String email;
	
	@NotBlank(message = MISSING_SPACENAME_MESSAGE)
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Size(message=BADFORMAT_SPACENAME_MESSAGE, min = 2, max = 30)
	private String spaceName;
}
