package com.project.board.domain.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {

	private final UserService userService;
	private final KakaoService kakaoService;
	
	public UserController(UserService userService, KakaoService kakaoService) {
		this.userService = userService;
		this.kakaoService = kakaoService;
	}
	
	@GetMapping("/")
	public String mainView() {
		return "redirect:/loginView";
	}
	
	@GetMapping("/loginView")
	public String loginView() {
		return "user/login";
	}
	
	@GetMapping("/kakaoOAuth")
	public RedirectView goKakaoOAUTH() {
		return kakaoService.goKakaoOAuth();
	}
	
	@GetMapping("/kakaologin")
	public String redirectView(@RequestParam("code") String code, HttpServletResponse response, Model model) {
		HashMap<String, String> hashMap = kakaoService.kakaoLogin(code);
		String accessToken = hashMap.get("accessToken");
		String nickName = hashMap.get("nickName");
		String returnMessage = hashMap.get("returnMessage");
		
		kakaoService.setAccessTokenInCookie(accessToken, response);
		model.addAttribute("nickName", nickName);
		
		if(returnMessage == "needSignIn") {
			return "user/signIn";
		}
		
		return "user/redirect";
	}
	
	@ResponseBody
	@PostMapping("/kakaoSignIn") 
	public String kakaoSignIn(@CookieValue(name = "accessToken", required = true) String accessToken,@RequestBody Map<String, Object> signInInfoMap) {
		if(kakaoService.checkAccessTokenExpire(accessToken)) {
			kakaoService.signIn(signInInfoMap);			
			return "success";
		} else {
			return "accessTokenExpired"; 
		}
		
		
	}
	
	
	@PostMapping("/kakaoLogout")
	public String goKakaoLogout(HttpSession httpSession) {
		kakaoService.kakaoLogout(httpSession);
		return "redirect:/catlogin"; 
	}
	
}
