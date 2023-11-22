package com.project.board.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private AccessTokenValidFilter accessTokenValidFilter;
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests((authorizeRequests) -> {
					authorizeRequests.antMatchers("/").permitAll(); 
					authorizeRequests.antMatchers("/loginView").permitAll(); 
					authorizeRequests.antMatchers("/signInView").permitAll(); 
					authorizeRequests.antMatchers("/signIn").permitAll(); 
				})
				
				.formLogin((formLogin) -> {
					formLogin.loginPage("/loginView");
				})
				
				.addFilterAfter(accessTokenValidFilter, FilterSecurityInterceptor.class)
				
				.build();
	}
}