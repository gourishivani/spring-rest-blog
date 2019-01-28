package com.outside.basic.auth;

import lombok.Data;

@Data
public class AuthenticationBean {
	private String message;

	public AuthenticationBean(String message) {
		super();
		this.message = message;
	}
	
	public AuthenticationBean() {
	}
}
