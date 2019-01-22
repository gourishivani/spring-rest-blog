package com.blogosphere.blog.biz;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.blogosphere.blog.dao.PostRepository;
import com.blogosphere.blog.model.Post;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepository postRepository;

	@Override
	public Post createPost(@Valid Post post) {
		return this.postRepository.save(post);
	}

	@Override
	public List<Post> findAllByAuthor(Long authorId, Pageable pageable) {
		return this.postRepository.findByAuthor_Id(authorId);
	}

	@Override
	public Optional<Post> find(Long postId) {
		return this.postRepository.findById(postId);
	}
}
