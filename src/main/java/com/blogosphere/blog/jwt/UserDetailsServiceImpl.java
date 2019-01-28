package com.blogosphere.blog.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.blogosphere.blog.biz.UserService;
import com.blogosphere.blog.model.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userService.findByEmail(username);
		if (user == null)
			throw new UsernameNotFoundException(username);
		JwtUserDetails jwtUser = new JwtUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(), "ROLE_USER_2", user.getName(), user.getSpaceName());
		return jwtUser;
	}
}
