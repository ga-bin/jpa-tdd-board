package com.project.board.domain.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private int userId;
	private String loginId;
	private String password;
	private String userName;
	private String nickName;
	private LocalDateTime registDt;
	private LocalDateTime updateDt;
	private String refreshToken;
	private String tokenProvider;
	
	public User toEntity() {
		User user = User.builder()
						.userId(userId)
						.loginId(loginId)
						.password(password)
						.userName(userName)
						.nickName(nickName)
						.registDt(registDt)
						.updateDt(updateDt)
						.refreshToken(refreshToken)
						.tokenProvider(tokenProvider)
						.build();
		
		return user;
	}
}
