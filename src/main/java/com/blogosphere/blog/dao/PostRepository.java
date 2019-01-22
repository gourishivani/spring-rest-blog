package com.blogosphere.blog.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogosphere.blog.model.Post;

// Spring will provide implementation for all the methods defined in the JpaRepository 
public interface PostRepository extends JpaRepository<Post, Long> {

//	@Query("from Post post inner join fetch r.comments where r.reviewId = :id")
//	@Query("from Review r inner join fetch r.comments where r.reviewId = :id")
//	User findByReviewId(@Param("id") int id);
	List<Post> findByAuthor_Id(Long authorId);

}
