package com.blogosphere.blog.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JWTWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtUnAuthorizedResponseAuthenticationEntryPoint jwtUnAuthorizedResponseAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenAuthorizationOncePerRequestFilter jwtAuthenticationTokenFilter;

    @Value("${jwt.get.token.uri}")
    private String authenticationPath;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoderBean());
    }

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf().disable()
            .exceptionHandling().authenticationEntryPoint(jwtUnAuthorizedResponseAuthenticationEntryPoint).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            /*Starting custom code*/
//            .antMatchers(HttpMethod.GET, "/users").permitAll()
            .antMatchers(HttpMethod.GET, "/users/**").permitAll()
            .antMatchers(HttpMethod.GET, "/comments/**").permitAll()
            .antMatchers(HttpMethod.GET, "/users/*/posts").permitAll()
            .antMatchers(HttpMethod.GET, "/posts/**").permitAll()
            .antMatchers(HttpMethod.POST, "/users").permitAll()
            
            // POSTs to be authenticated
            // These are not needed because of the aggressive GET match
//            .antMatchers(HttpMethod.POST, "/posts").authenticated()
//            .antMatchers(HttpMethod.POST, "/users/**/comments").authenticated()
            
            // Other endpoints are enabled for authenticated user. However, this should be Role based.
//            .antMatchers(HttpMethod.GET, "/actuator/**").denyAll() 
            
            /**
             * Doesn't do much at this point. 
             * But we could easily extend this to add health checks for dependent services
             * */
            .antMatchers(HttpMethod.GET, "/actuator/health").permitAll() 
            
	          /**
	           * Auditing endpoints could be enabled based on user role. (Not implemented as there is no notion of ROLE)
	           */
//            .antMatchers(HttpMethod.GET, "/actuator/**").hasRole("ADMIN") 

            /* Enable swagger for all users*/
            .antMatchers("/v2/api-docs/**").permitAll()
            .antMatchers("/swagger-ui/**").permitAll()
            .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**").permitAll()
            
            .anyRequest().authenticated();

       httpSecurity
            .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        httpSecurity
            .headers()
            .frameOptions().sameOrigin()  //H2 Console Needs this setting
            .cacheControl(); //disable caching
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
    	webSecurity.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    	webSecurity
            .ignoring()
            .antMatchers(
                HttpMethod.POST,
                authenticationPath
            )
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .and()
            .ignoring()
            .antMatchers(
                HttpMethod.GET,
                "/" //Other Stuff You want to Ignore
            )
            .and()
            .ignoring()
            .antMatchers("/h2-console/**/**");//Should not be in Production!
    }
    
    // No CORS. May not be ideal for production!
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//      final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//      source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
//      return source;
//    }
    
//	 path-traversal security is new with this version of spring. Without adding this, path needs to be normalized.
	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		//https://stackoverflow.com/questions/48453980/spring-5-0-3-requestrejectedexception-the-request-was-rejected-because-the-url
		return new DefaultHttpFirewall();
	}
}

