package com.project.board.domain.board;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

	@InjectMocks
	private BoardController boardController;
	
	private MockMvc mockMvc;
	
	@BeforeEach
	public void init() {
		mockMvc = MockMvcBuilders
					.standaloneSetup(boardController)
					.build();
	}
	
	@Test
	@DisplayName("mockMvc가 null이 아님")
	public void mockMvcIsNotNull() {
		assertThat(boardController).isNotNull();
		assertThat(mockMvc).isNotNull();
	}
	
}
