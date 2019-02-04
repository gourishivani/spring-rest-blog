package com.blogosphere.blog.jwt.resource;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public class  JwtTokenRequest implements Serializable {
  
  private static final long serialVersionUID = -5616176897013108345L;

  @NotBlank(message="Please enter a username")
  private String username;
  
  @NotBlank(message="Please enter a password")
  private String password;

    public JwtTokenRequest() {
        super();
    }

    public JwtTokenRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

