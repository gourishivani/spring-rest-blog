package com.blogosphere.blog.biz;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.blogosphere.blog.model.Post;

public interface PostService {
	
	public Post createPost(@Valid Post post);

	public List<Post> findAllByAuthor(Long authorId, Sort sort);
	
	public Optional<Post> find(Long postId);
}
