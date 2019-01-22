package com.blogosphere.blog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
@Entity
public class Post extends BaseEntity {
	
	@Column(nullable=false, length = 30)
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Size(min = 2, max = 30)
	private String title;
	
	@Lob
//	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE) // We are only allowing plain text 
	@Column(nullable=false, length = 300)
	@Size(min = 2, max = 300)
	private String desciption;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User author;
	
}
