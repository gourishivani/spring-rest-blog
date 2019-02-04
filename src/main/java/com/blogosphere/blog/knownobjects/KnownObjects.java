package com.blogosphere.blog.knownobjects;

import com.blogosphere.blog.model.Comment;
import com.blogosphere.blog.model.Post;
import com.blogosphere.blog.model.User;

public class KnownObjects {
	public static String KNOWN_USER1_PASSWORD = "test123";
	public static String KNOWN_USER2_PASSWORD = "tester";
	
	public static User knownUser1() {
		return  new User("John Doe", "johndoe@example.com", "$2a$10$X1o2YKGHHj9XYnjv7rQEaeqDpaMUr3w/p88F7XRjJSCkDjQWly.cK", "My Test Blog Space");
	}
	
	public static User knownUser2() {
		return  new User("Tester Albert", "tester@example.com", "$2a$10$Rx19UHVc6MPIPhrj0dIQb.M/NTFZkMhAMT6NBPx2Qtyj2jXM6rfxy", "frodo");
	}
	
	public static Post knownPost1(User author) {
		return new Post("Who Is Venezuela's Juan Guaid?", 
				"The 35-year-old politician declared himself acting president this week, and has been recognized as the country's president by the Trump administration.",
				author );
	}
	
	public static Post knownPost2(User author) {
		return new Post("What is Lorem Ipsum?", 
				"Lorem Ipsum is simply dummy text of the printing and typesetting industry. "
				+ "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,"
				+ " when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
				author );
	}
	
	public static Comment knownComment1(Post post, User commentor) {
		Comment comment = new Comment();
		comment.setCommentor(commentor);
		comment.setPost(post);
		comment.setContent("Great theory!");
		return comment;
	}
	
	public static Comment knownComment2(Post post, User commentor) {
		Comment comment = new Comment();
		comment.setCommentor(commentor);
		comment.setPost(post);
		comment.setContent("Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.");
		return comment;
	}
	
	public static Comment knownComment3(Post post, User commentor) {
		Comment comment = new Comment();
		comment.setCommentor(commentor);
		comment.setPost(post);
		comment.setContent("Where does it come from?");
		return comment;
	}
	
}
