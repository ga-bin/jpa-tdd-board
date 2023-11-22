package com.project.board.domain.user;


import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


	@Override
	public void signIn(Map<String, Object> userInfoMap) {
		// 패스워드 인코딩해서 저장하기
		
	}

}
