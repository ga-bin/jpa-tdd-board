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
	private LocalDateTime registDt;
	private LocalDateTime updateDt;
	private String refreshToken;
	
	public User toEntity() {
		User user = User.builder()
						.userId(userId)
						.loginId(loginId)
						.password(password)
						.userName(userName)
						.registDt(registDt)
						.updateDt(updateDt)
						.refreshToken(refreshToken)
						.build();
		
		return user;
	}
}
