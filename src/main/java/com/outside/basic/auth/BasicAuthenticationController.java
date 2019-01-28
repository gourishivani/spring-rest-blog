package com.outside.basic.auth;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping(path = "/")
public class BasicAuthenticationController {

	@GetMapping(path = "basicauth")
	public AuthenticationBean authenticate() {
		return new AuthenticationBean("You are authenticated");
	}
}
