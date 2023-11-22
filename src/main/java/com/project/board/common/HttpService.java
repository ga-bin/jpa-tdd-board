package com.project.board.common;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

public interface HttpService {

	public void setInCookie(String key, String value, HttpServletResponse response);

	public String getCookieValueFromRequest(HttpServletRequest request, String cookieName);
	
	public HttpEntity<Object> setHttpHeaderInHttpEntity(HashMap<String, String> headerValues, MediaType mediatype);
}
