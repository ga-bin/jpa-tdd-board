package com.project.board.domain.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Service
public class KakaoServiceImpl implements KakaoService {

	private final HttpSession httpSession;
	private final UserRepository userRepository;
	
	
	@Autowired
	public KakaoServiceImpl(HttpSession httpSession, UserRepository userRepository){
		this.httpSession = httpSession;
		this.userRepository = userRepository;
	}
	
	
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String REST_API_KEY;
	
	
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String REDIRECT_URI;
	
	
	@Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
	private String AUTHORIZE_URI;
	
	
	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	public String TOKEN_URI;
	
	
	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String CLIENT_SECRET;
	
	
	private String REQ_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
	
	
	private String REQ_ACCESSTOKEN_INFO_URI = "https://kapi.kakao.com/v1/user/access_token_info";
	
	
	public RedirectView goKakaoOAuth() {
		return goKakaoOAuth("");
	}
	
	public RedirectView goKakaoOAuth(String scope) {
		String uri = AUTHORIZE_URI + "?redirect_uri=" + REDIRECT_URI + "&response_type=code&client_id=" + REST_API_KEY;
		if(!scope.isEmpty()) {
			uri += "&scope="+scope;
		}
		
		return new RedirectView(uri);
		
	}

	@Override
	public void kakaoLogout(HttpSession httpSession) {
		
		String result; 
	}

	@Override
	public HashMap<String, String> kakaoLogin(String code) {
		// accessToken과 userInfo 가져오기
		String accessToken = getKakaoAccessToken(code);
		HashMap<String, Object> kakaoUserInfo = getKakaoUserInfo(accessToken);
		String loginId = kakaoUserInfo.get("loginId").toString();
		String nickName = kakaoUserInfo.get("nickName").toString();

		// 전달할 정보 담기
		HashMap<String, String> returnMap = new HashMap<>(); 
		returnMap.put("accessToken", accessToken);
		returnMap.put("nickName", nickName);
		
		// 해당 아이디가 존재하는지 확인
		User findUser = userRepository.findByLoginId(loginId); 
		if(findUser == null) {
			returnMap.put("returnMessage", "needSignIn");
			return returnMap;
		} else {
			updateRefreshToken(code, findUser.toDTO());
			returnMap.put("returnMessage", "loginSuccess");
			return returnMap;
		}		
	}



	private void updateRefreshToken(String code, UserDTO userDTO) {
		JsonElement element = JsonParser.parseString(code);
		String refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
		
		if(!userDTO.getRefreshToken().equals(refreshToken)) {
			userDTO.setRefreshToken(refreshToken);
			userRepository.save(userDTO.toEntity());
		}
	}
	

	public String getKakaoAccessToken(String code) {
		String parsedCode = pasingKakaoCodeToJsonElement(code);
		JsonElement element = JsonParser.parseString(parsedCode);
		String accessToken = element.getAsJsonObject().get("access_token").getAsString();
		
		return accessToken;
	}


	private String pasingKakaoCodeToJsonElement(String code) {
		RestTemplate restTemplate = new RestTemplate();
		
		// 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<Object> request = new HttpEntity<>(headers);
		
		// uri builder로 요청 uri만들기
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(TOKEN_URI)
				.queryParam("grant_type", "authorization_code")
				.queryParam("client_id", REST_API_KEY)
				.queryParam("redirect_uri", REDIRECT_URI)
				.queryParam("code", code)
				.queryParam("client_secret", CLIENT_SECRET);
		
		// 요청 uri와 header 전송
       ResponseEntity<String> responseEntity = restTemplate.exchange(
    		uriComponentsBuilder.toUriString(),
    		HttpMethod.POST,
    		request,
    		String.class
       );
       
       if(responseEntity.getStatusCode() == HttpStatus.OK) {
    	   return responseEntity.getBody();
       } else {
    	   new RuntimeException("카카오 로그인을 위한 정보를 발급받을 수 없습니다.");
    	   return "error";
       }
	       
	}
	
	// 나중에 이 accesstoken으로 요청할 경우 사용
	public boolean checkAccessTokenExpire(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();
		String accessTokenInfoUri = REQ_ACCESSTOKEN_INFO_URI + "?access_token=" + accessToken;
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(accessTokenInfoUri);
		
		
		// header설정
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		
		// 요청 받기
		ResponseEntity<String> responseEntity = restTemplate.exchange(
				uriComponentsBuilder.toUriString(), 
				HttpMethod.GET, 
				request, 
				String.class
				);
		
		HttpStatus responseCode = responseEntity.getStatusCode();
		
		if(responseCode == HttpStatus.BAD_REQUEST) {
			new RuntimeException("카카오 서비스에 일시적 장애가 발생했습니다.");
			return false;
		}
		
		if(responseCode == HttpStatus.UNAUTHORIZED) {
			return false;
		}
		
		return true;
	}

	
	
	private HashMap<String, Object> getKakaoUserInfo(String accessToken) {
		HashMap<String, Object> userInfoMap = new HashMap<>();
		RestTemplate restTemplate = new RestTemplate();
		
		// 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		// uri설정
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(REQ_USER_INFO_URI);
		
		// 요청보내고 정보 받기
		ResponseEntity<String> responseEntity = restTemplate.exchange(
													uriComponentsBuilder.toUriString(), 
													HttpMethod.GET, 
													request, 
													String.class
												);
		
		// 응답 상태 확인
		if(responseEntity.getStatusCode() != HttpStatus.OK) {
			new RuntimeException("카카오 로그인을 위한 정보를 발급받을 수 없습니다.");
			return userInfoMap;
		}

		// 유저 정보 가져오기
		String responseBody = responseEntity.getBody();
		JsonElement element = JsonParser.parseString(responseBody);
		String loginId = element.getAsJsonObject().get("id").getAsString();
		String nickName = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
		
		userInfoMap.put("loginId", loginId);
		userInfoMap.put("nickName", nickName);
	
		return userInfoMap;
	}

	
	// 나중에 이 accesstoken으로 요청할 경우 사용
	private String refreshAccessToken(String code) {
		String parsedCode = pasingKakaoCodeToJsonElement(code);
		JsonElement element = JsonParser.parseString(parsedCode);
		String refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
		
		return null;
	}

	@Override
	public void setAccessTokenInCookie(String accessToken, HttpServletResponse response) {
		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setPath("/"); // /부터 시작하는 모든 경로에 대해서 쿠키 설정
		cookie.setHttpOnly(true); // javascript로 쿠키 접근 막음
		response.addCookie(cookie);
		
	}

	@Override
	public void signIn(Map<String, Object> signInInfoMap) {
		UserDTO userDTO = new UserDTO();
		userDTO.setLoginId(AUTHORIZE_URI);
	}

}
