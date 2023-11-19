package com.project.board.domain.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/")
	public String mainView() {
		return "redirect:/catlogin";
	}
	
	@GetMapping("/catlogin")
	public String loginView() {
		return "user/login";
	}
}
