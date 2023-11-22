package com.project.board.domain.user;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	private String loginId;
	private String password;
	private String userName;
	private String nickName;
	@CreationTimestamp
	private LocalDateTime registDt;
	@UpdateTimestamp
	private LocalDateTime updateDt;
	
	private String refreshToken;
	
	private String tokenProvider;

	public UserDTO toDTO() {
		UserDTO userDTO = UserDTO.builder()
							.userId(userId)
							.loginId(loginId)
							.password(password)
							.userName(userName)
							.registDt(registDt)
							.updateDt(updateDt)
							.refreshToken(refreshToken)
							.tokenProvider(tokenProvider)
							.build();
		
		return userDTO;
	}
}
