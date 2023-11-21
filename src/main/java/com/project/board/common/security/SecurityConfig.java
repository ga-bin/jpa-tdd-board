package com.project.board.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests((authorizeRequests) -> {
					authorizeRequests.requestMatchers(new MvcRequestMatcher(introspector, "/user/**")).authenticated();
					authorizeRequests.requestMatchers(new MvcRequestMatcher(introspector, "/manager/**"))
						.hasAnyRole("ADMIN", "MANAGER");
					authorizeRequests.requestMatchers(new MvcRequestMatcher(introspector, "/admin/**"))
						.hasRole("ADMIN");
					authorizeRequests.anyRequest().permitAll();
				})
				
				.formLogin((formLogin) -> {
					formLogin.loginPage("/loginView");
				})
				
				
				.build();
	}
}
