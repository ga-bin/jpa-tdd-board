package com.project.board.domain.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.view.RedirectView;

public interface KakaoService {

	public RedirectView goKakaoOAuth();
	
	public RedirectView goKakaoOAuth(String scope);

	public void kakaoLogout(HttpSession httpSession);

	public HashMap<String, String> kakaoLogin(String code);

	public String getKakaoAccessToken(String code);

	public void setAccessTokenInCookie(String accessToken, HttpServletResponse response);

	public void signIn(Map<String, Object> signInInfoMap);

	public boolean checkAccessTokenExpire(String accessToken);
}
