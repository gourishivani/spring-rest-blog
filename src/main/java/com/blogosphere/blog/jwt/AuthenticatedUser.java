package com.blogosphere.blog.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.blogosphere.blog.jwt.resource.AuthenticationException;

public class AuthenticatedUser {
	
	public static JwtUserDetails getAuthenticatedUser() {
		Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();

		//TODO: This might be un-necessary. It should be safe to assume that the user is authenticated at this point
		if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserDetails))
			throw new AuthenticationException("Not Authorized", new UsernameNotFoundException("User not allowed"));
		
		JwtUserDetails user = (JwtUserDetails) authentication.getPrincipal();
		return user;
	}
	
}
