package com.project.board.domain.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.view.RedirectView;

import com.project.board.common.security.TokenProvider;

public interface KakaoService {

	public RedirectView goKakaoOAuth();
	
	public RedirectView goKakaoOAuth(String scope);

	public void kakaoLogout(String accessToken);

	public HashMap<String, String> kakaoLogin(String code);

	public boolean checkAccessTokenExpire(String accessToken);

	public void updateUser(Map<String, Object> userInfoMap, String accessToken);

	public HashMap<String, String> refreshToken(String refreshToken, HttpServletResponse response);
	
	public boolean checkRefreshTokenExpire(HashMap<String, String> refreshedTokens);
}
