package com.project.board.domain.board;

import java.time.LocalDateTime;

import com.project.board.domain.user.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BoardDTO {

	private int boardId;
	private String title;
	private String content;
	private UserDTO register;
	private UserDTO updater;
	private LocalDateTime registDt;
	private LocalDateTime updateDt;

	public Board toEntity() {
		return Board.builder()
					.boardId(boardId)
					.title(title)
					.content(content)
					.register(register.toEntity())
					.updater(updater.toEntity())
					.registDt(registDt)
					.updateDt(updateDt)
					.build();
	}
}
