package com.project.board.domain.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
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
import com.project.board.common.HttpService;
import com.project.board.common.security.TokenProvider;

@Service
public class KakaoServiceImpl implements KakaoService {

	private final UserRepository userRepository;
	private final HttpService httpService;
	
	
	@Autowired
	public KakaoServiceImpl(UserRepository userRepository, HttpService httpService){
		this.userRepository = userRepository;
		this.httpService = httpService;
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
	
	
	private String LOGOUT_URI = "https://kapi.kakao.com/v1/user/logout";
	
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
	public void kakaoLogout(String accessToken) {
		HashMap<String, Object> kakaoUserInfo = getKakaoUserInfo(accessToken);
		String loginId = kakaoUserInfo.get("loginId").toString();
		
		RestTemplate restTemplate = new RestTemplate();
		
		// 헤더 설정
		HashMap<String, String> headerValues = new HashMap<>();
		headerValues.put("Authorization", "Bearer " + accessToken);
		HttpEntity<Object> request = httpService.setHttpHeaderInHttpEntity(headerValues, null);
		
		
		// uri builder로 요청 uri만들기
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(LOGOUT_URI)
						.queryParam("target_id_type", "user_id")
						.queryParam("target_id", loginId);
				
		// 요청 받기
		restTemplate.exchange(
			uriComponentsBuilder.toUriString(), 
			HttpMethod.POST, 
			request, 
			String.class
		);
		
		
	}

	@Override
	public HashMap<String, String> kakaoLogin(String code) {
		// accessToken과 userInfo 가져오기
		String parsedCode = pasingKakaoCodeToJson(code);
		String accessToken = getKakaoToken(parsedCode, "access_token");
		String refreshToken = getKakaoToken(parsedCode, "refresh_token");
		HashMap<String, Object> kakaoUserInfo = getKakaoUserInfo(accessToken);
		String loginId = kakaoUserInfo.get("loginId").toString();
		String nickName = kakaoUserInfo.get("nickName").toString();

		// 전달할 정보 담기
		HashMap<String, String> userInfoMap = new HashMap<>(); 
		userInfoMap.put("accessToken", accessToken);
		userInfoMap.put("refreshToken", refreshToken);
		userInfoMap.put("nickName", nickName);
		userInfoMap.put("loginId", loginId);
		
		// 해당 아이디가 존재하는지 확인
		User findUser = userRepository.findByLoginId(loginId); 
		if(findUser == null) {
			saveUser(parsedCode, userInfoMap);
			userInfoMap.put("returnMessage", "needExtraInfo");
			return userInfoMap;
		} else {
			updateRefreshToken(userInfoMap, findUser.toDTO());
			userInfoMap.put("returnMessage", "loginSuccess");
			return userInfoMap;
		}		
	}


	private String pasingKakaoCodeToJson(String code) {
		RestTemplate restTemplate = new RestTemplate();
		
		// 헤더 설정
		HttpEntity<Object> request = httpService.setHttpHeaderInHttpEntity(null, MediaType.APPLICATION_FORM_URLENCODED);
		
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
	
	private void saveUser(String parsedCode, HashMap<String, String> userInfoMap) {
		UserDTO userDTO = new UserDTO();
		userDTO.setLoginId(userInfoMap.get("loginId").toString());
		userDTO.setNickName(userInfoMap.get("nickName").toString());
		userDTO.setRefreshToken(userInfoMap.get("refreshToken").toString());
		userDTO.setTokenProvider(TokenProvider.KAKAO.getProvider());
		userRepository.save(userDTO.toEntity());
	}

	
	private void updateRefreshToken(HashMap<String, String> userInfoMap, UserDTO findUser) {
		String refreshToken = userInfoMap.get("refreshToken").toString();
		
		if(!findUser.getRefreshToken().equals(refreshToken)) {
			findUser.setRefreshToken(refreshToken);
			userRepository.save(findUser.toEntity()); 
		}
	}
	
	
	private String getKakaoToken(String parsedCode, String tokenName) {
		JsonElement element = JsonParser.parseString(parsedCode);
		String token = element.getAsJsonObject().get(tokenName).getAsString();
		
		return token;
	}

	private HashMap<String, Object> getKakaoUserInfo(String accessToken) {
		HashMap<String, Object> userInfoMap = new HashMap<>();
		RestTemplate restTemplate = new RestTemplate();
		
		// 헤더 설정
		HashMap<String, String> headerValues = new HashMap<>();
		headerValues.put("Authorization", "Bearer " + accessToken);
		HttpEntity<Object> request = httpService.setHttpHeaderInHttpEntity(headerValues, MediaType.APPLICATION_FORM_URLENCODED);
		
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
	public boolean checkAccessTokenExpire(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(REQ_ACCESSTOKEN_INFO_URI);
		
		
		// 헤더 설정
		HashMap<String, String> headerValues = new HashMap<>();
		headerValues.put("Authorization", "Bearer " + accessToken);
		HttpEntity<Object> request = httpService.setHttpHeaderInHttpEntity(headerValues, null);
		
		
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


	
	// 나중에 이 accesstoken으로 요청할 경우 사용
	public HashMap<String, String> refreshToken(String refreshToken, HttpServletResponse response) {
		HashMap<String, String> refreshedTokens = getRefreshedTokens(refreshToken);
		return refreshedTokens;
	}
	


	private HashMap<String, String> getRefreshedTokens(String refreshToken) {
		RestTemplate restTemplate = new RestTemplate();
		HashMap<String, String> refreshedTokens = new HashMap<>();
		
		// 헤더 설정
		HttpEntity<Object> request = httpService.setHttpHeaderInHttpEntity(null, MediaType.APPLICATION_FORM_URLENCODED);
		
		
		// uri builder로 요청 uri만들기
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(TOKEN_URI)
				.queryParam("grant_type", "refresh_token")
				.queryParam("client_id", REST_API_KEY)
				.queryParam("refresh_token", refreshToken)
				.queryParam("client_secret", CLIENT_SECRET);
		
		// 요청 uri와 header 전송
       ResponseEntity<String> responseEntity = restTemplate.exchange(
    		uriComponentsBuilder.toUriString(),
    		HttpMethod.POST,
    		request,
    		String.class
       );
       
       // refresh된 토큰 정보들 받아오기
       String responseBody = responseEntity.getBody();
       JsonElement element = JsonParser.parseString(responseBody);
	   String refreshedAccessToken = element.getAsJsonObject().get("access_token").getAsString();
	   String refreshedRefreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
	   
	   refreshedTokens.put("refreshedAccessToken", refreshedAccessToken);
	   refreshedTokens.put("refreshedRefreshToken", refreshedRefreshToken);
	   
	   return refreshedTokens;
	}

	
	public boolean checkRefreshTokenExpire(HashMap<String, String> refreshedTokens) {
		String refreshedAccessToken = refreshedTokens.get("refreshedAccessToken");
		String refreshedRefreshToken = refreshedTokens.get("refreshedRefreshToken");
		
		HashMap<String, Object> kakaoUserInfo = getKakaoUserInfo(refreshedAccessToken);
		String loginId = kakaoUserInfo.get("loginId").toString();
		
		User user = userRepository.findByLoginId(loginId);
		
		if(!user.getRefreshToken().equals(refreshedRefreshToken)) {
			return false;
		}
		
		return true;
	}
	
	
	@Override
	public void updateUser(Map<String, Object> userInfoMap, String accessToken) {
		HashMap<String, Object> kakaoUserInfoMap = getKakaoUserInfo(accessToken);
		String loginId = kakaoUserInfoMap.get("loginId").toString();
		String userName = userInfoMap.get("userName").toString();
		String nickName = userInfoMap.get("nickName").toString();
		
		User user = userRepository.findByLoginId(loginId);
		
		if(user != null) {
			UserDTO userDTO = user.toDTO();
			userDTO.setUserName(userName);
			userDTO.setNickName(nickName);
			userRepository.save(userDTO.toEntity());
		}
	}


}
