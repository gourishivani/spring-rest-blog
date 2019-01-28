package com.blogosphere.blog;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBcryptEncodedPassword {
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		for (int i = 0; i < 20; i++) {
			String encoded = encoder.encode("tester");
			System.out.println(encoded);
		}
		
	}
}
