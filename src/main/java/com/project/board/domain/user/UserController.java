package com.project.board.domain.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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

import com.project.board.common.HttpService;
import com.project.board.common.security.TokenProvider;

@Controller
public class UserController {

	private final UserService userService;
	private final KakaoService kakaoService;
	private final HttpService httpService;
	
	public UserController(UserService userService, KakaoService kakaoService, HttpService httpService) {
		this.userService = userService;
		this.kakaoService = kakaoService;
		this.httpService = httpService;
	}
	
	
	@GetMapping("/")
	public String loginView1() {
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
		String refreshToken = hashMap.get("refreshToken");
		String nickName = hashMap.get("nickName");
		String returnMessage = hashMap.get("returnMessage");
		
		httpService.setInCookie("accessToken", accessToken, response);
		httpService.setInCookie("refreshToken", refreshToken, response);
		httpService.setInCookie("provider", TokenProvider.KAKAO.getProvider(), response);
		
		model.addAttribute("nickName", nickName);
		
		if(returnMessage == "needExtraInfo") {
			return "user/extraInfo";
		}
		
		return "redirect:/mainView";
	}
	
	@ResponseBody
	@PostMapping("/kakaoLoginExtraInfo") 
	public String kakaoSignIn(@CookieValue(name = "accessToken", required = true) String accessToken,@RequestBody Map<String, Object> userInfoMap) {
		kakaoService.updateUser(userInfoMap, accessToken);			
		return "success";
	}
	
	
	@GetMapping("/mainView")
	public String mainView() {
		return "user/main";
	}
	
	
	@PostMapping("/kakaoLogout")
	public String goKakaoLogout(@CookieValue(name = "accessToken", required = true) String accessToken) {
		kakaoService.kakaoLogout(accessToken);
		return "redirect:/"; 
	}
	
	@GetMapping("/signInView")
	public String singInView() {
		return "user/signIn";
	}
	
	@PostMapping("/signIn")
	public String signIn(@RequestBody Map<String, Object> userInfoMap) {
		userService.signIn(userInfoMap);
		return "success";
	}
}
