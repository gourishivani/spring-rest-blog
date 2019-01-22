package com.blogosphere.blog.model;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.blogosphere.blog.core.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
	private Long id;

	@JsonIgnore
	@Column(insertable = true, updatable = true, nullable = false)
	private ZonedDateTime created;

	@JsonIgnore
	@Column(insertable = true, updatable = true)
	private ZonedDateTime lastModified;

	@PrePersist
	protected void onCreate() {
		created = Utils.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.lastModified = Utils.now();
	}
}
