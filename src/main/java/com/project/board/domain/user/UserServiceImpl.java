package com.project.board.domain.user;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	
	@Override
	public void signIn(Map<String, Object> userInfoMap) {
		String loginId = userInfoMap.get("loginId").toString();
		String encodedPassword = passwordEncoder.encode(userInfoMap.get("password").toString());
		String nickName = userInfoMap.get("nickName").toString();
		String userName = userInfoMap.get("userName").toString();
		
		UserDTO userDTO = new UserDTO();
		userDTO.setLoginId(loginId);
		userDTO.setPassword(encodedPassword);
		userDTO.setNickName(nickName);
		userDTO.setUserName(userName);
		
		userRepository.save(userDTO.toEntity());
	}

}
