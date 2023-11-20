package com.project.board.domain.user;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
	private String id;
	private String password;
	private String userName;
	@CreatedDate
	private LocalDateTime registDt;
	@UpdateTimestamp
	private LocalDateTime updateDt;

	public UserDTO toDTO() {
		UserDTO userDTO = UserDTO.builder()
							.userId(userId)
							.id(id)
							.password(password)
							.userName(userName)
							.registDt(registDt)
							.updateDt(updateDt)
							.build();
		
		return userDTO;
	}
}
