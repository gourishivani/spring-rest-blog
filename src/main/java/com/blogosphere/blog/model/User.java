package com.blogosphere.blog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity // If hibernate.ddl-auto=create, @Entity will suggest Hibernate to make a table
		// out of this class
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "unique_users_email_idx")})
public class User extends BaseEntity {
//	@NotBlank(message = "name cannot be blank")
//	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Column(nullable=false, length = 100)
//	@Size(min = 1, max = 100)
	private String name;

//	@NotBlank(message = "Please provide a password")
	@Column(nullable=false)
	private String passwordHash;

	@Column(nullable=false)
//	@NotBlank(message = "email cannot be blank")
//	@Email(message = "invalid format")
	private String email;

//	@NotBlank(message = "Please provide a name for your space")
//	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Column(nullable=false, length = 30)
	@Size(min = 2, max = 30)
	private String spaceName;

//	private List<BlogPost> posts = new LinkedList<>();

	public User(String name, String email, String passwordHash, String spaceName) {
		this.name = name;
		this.email = email;
		this.passwordHash = passwordHash;
		this.spaceName = spaceName;
	}

	public User() {
	}
}
