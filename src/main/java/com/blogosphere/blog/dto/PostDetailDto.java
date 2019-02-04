package com.blogosphere.blog.dto;

import java.time.ZonedDateTime;

import org.springframework.hateoas.core.Relation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

//HATEOAS: The embedded object will be put under data. This makes the embedded links accessible in a super consistent way.
@Relation(collectionRelation="data")

@AllArgsConstructor
@NoArgsConstructor
public class PostDetailDto {
	private String title;
	private String description;
	
	private Long id;
	
	private UserDetailDto author;
	
	private ZonedDateTime created;
	private ZonedDateTime lastModified;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostDetailDto other = (PostDetailDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	
}
