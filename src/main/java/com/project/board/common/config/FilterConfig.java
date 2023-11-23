package com.project.board.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.board.common.security.AccessTokenValidFilter;

@Configuration(proxyBeanMethods = false)
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<AccessTokenValidFilter> registration(AccessTokenValidFilter filter) {
		FilterRegistrationBean<AccessTokenValidFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}
}
