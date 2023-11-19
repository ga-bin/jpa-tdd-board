package com.project.board.domain.board;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import com.project.board.domain.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int boardId;
	private String title;
	private String content;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "register_id")
	private User register;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "updater_id")
	private User updater;
	@CreatedDate
	private LocalDateTime registDt;
	@UpdateTimestamp
	private LocalDateTime updateDt;
	

	public BoardDTO toDTO() {
		BoardDTO boardDTO = BoardDTO.builder()
								.boardId(boardId)
								.title(title)
								.content(content)
								.register(register.toDTO())
								.updater(updater.toDTO())
								.registDt(registDt)
								.updateDt(updateDt)
								.build();
		
		return boardDTO;
	}
}
