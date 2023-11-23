package com.project.board.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	AccessTokenValidFilter accessTokenValidFilter;
	
	String[] permitAllPatterns = {"/", "/loginView", "/signInView", "/signIn", "/kakaoOAuth", "/kakaologin", "/mainView"};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests((authorizeRequests) -> {
					authorizeRequests
						.antMatchers(permitAllPatterns).permitAll() 
						.antMatchers("wer").hasAnyRole(permitAllPatterns);
				})
				
				.formLogin((formLogin) -> {
					formLogin.loginPage("/loginView");
				})
				
				.addFilterAfter(accessTokenValidFilter, FilterSecurityInterceptor.class)
				
				.build();
	}
	

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
							.antMatchers(permitAllPatterns)
							.antMatchers("/image/**");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}