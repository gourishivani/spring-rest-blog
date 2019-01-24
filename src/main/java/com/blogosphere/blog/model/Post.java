package com.blogosphere.blog.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Data 
@EqualsAndHashCode(callSuper=true)
@Entity
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Post extends BaseEntity {
	
	@Column(nullable=false, length = 30)
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@Size(message="Please provide a title between 2 to 30 characters", min = 2, max = 30)
	@NotBlank(message = "Please provide a title for your post")
	private String title;
	
//	@Lob
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE) // We are only allowing plain text 
	@Column(nullable=false, length = 300)
	@Size(message="Please provide a title between 2 to 300 characters",min = 2, max = 300)
	@NotBlank(message = "Please provide a desciption for your post")
	private String description;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=false)
	@JoinColumn(name="author_id", referencedColumnName="id")
	private User author;
	
//	@Getter(AccessLevel.NONE)
//    @JsonIgnore
//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
//            orphanRemoval = true)
//	private Set<Comment> comments;
	
}
