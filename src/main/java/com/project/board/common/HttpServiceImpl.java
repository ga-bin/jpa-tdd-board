package com.project.board.common;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class HttpServiceImpl implements HttpService {

	@Override
	public void setInCookie(String key, String value, HttpServletResponse response) {		
		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/"); // /부터 시작하는 모든 경로에 대해서 쿠키 설정
		cookie.setHttpOnly(true); // javascript로 쿠키 접근 막음
		response.addCookie(cookie);
	}
	
	public String getCookieValueFromRequest(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		
		String cookieValue = null;
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals(cookieName)) {
					cookieValue = cookie.getValue();
				}
			}
		}
		return cookieValue;
	}
	
	public HttpEntity<Object> setHttpHeaderInHttpEntity(HashMap<String, String> headerValues, MediaType mediaType) {
		HttpHeaders headers = new HttpHeaders();
		
		if(headerValues != null && headerValues.size() > 0) {
			headerValues.forEach((key, value) -> {
				headers.set(key, value);
			});
		}
		
		if(mediaType != null) {
			headers.setContentType(mediaType);
		}
		
		HttpEntity<Object> request = new HttpEntity<>(headers);
		
		return request;
	}
}
