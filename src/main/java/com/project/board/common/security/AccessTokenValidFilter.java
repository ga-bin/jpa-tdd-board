package com.project.board.common.security;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.board.common.HttpService;
import com.project.board.domain.user.KakaoService;

@Component
public class AccessTokenValidFilter extends OncePerRequestFilter {

	@Autowired
	HttpService httpService;
	
	@Autowired
	KakaoService kakaoService;

	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// prodiver가 kakao냐 일반이냐에 따라서 다른 로직 타도록
		String provider = httpService.getCookieValueFromRequest(request, "provider"); 
		
		if(provider.equals(TokenProvider.KAKAO.getProvider())) {
			checkKakaoAccesssTokenExpire(request, response);
		} else {
			checkAccessTokenExpire(request.getCookies());
		}
		
		filterChain.doFilter(request, response);
	}



	private void checkKakaoAccesssTokenExpire(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String accessToken = httpService.getCookieValueFromRequest(request, "accessToken");
		String refreshToken = httpService.getCookieValueFromRequest(request, "refreshToken");

		// accesstoken만료 시
		if(!kakaoService.checkAccessTokenExpire(accessToken)) {			
			HashMap<String, String> refreshedTokens = kakaoService.refreshToken(refreshToken, response);
			String refreshedAccessToken = refreshedTokens.get("refreshedAccessToken");
			
			if(!kakaoService.checkRefreshTokenExpire(refreshedTokens)) {
				kakaoService.kakaoLogout(refreshedAccessToken);
				response.sendRedirect("/");
			} else {
				httpService.setInCookie("accessToken", refreshedAccessToken, response);
			}			
		}		
	}

	
	private void checkAccessTokenExpire(Cookie[] cookies) {
		// TODO Auto-generated method stub
		
	}
}
