package com.blogosphere.blog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=true)
@Entity
public class Comment extends BaseEntity {
	@Column(nullable = false, length=300)
	@NotNull
//	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE) // We are only allowing plain text
	@Size(min = 2, max = 300)
	private String content;
	
	@ManyToOne
	@JoinColumn(name="commentor_id", referencedColumnName="id", nullable=false)
	@NotNull
	private User commentor;
	
	@ManyToOne
	@JoinColumn(name="post_id", referencedColumnName="id", nullable=false)
	@NotNull
	private Post post;
}
