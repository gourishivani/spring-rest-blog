package com.outside.basic.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfigurationBasicAuth extends WebSecurityConfigurerAdapter{
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable() /*We will use JWT instead*/
			.authorizeRequests()
			.antMatchers(HttpMethod.OPTIONS, "/**").permitAll() /*Allow pre-flight OPTIONS for all URLs*/
				.anyRequest().authenticated()
				.and()
//			.formLogin().and()
			.httpBasic();
	}

//	 path-traversal security is new with this version of spring. Without adding this, path needs to be normalized.
	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		//https://stackoverflow.com/questions/48453980/spring-5-0-3-requestrejectedexception-the-request-was-rejected-because-the-url
		return new DefaultHttpFirewall();
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    //@formatter:off
	    super.configure(web);
	    web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
	}
}
