package com.project.board.common.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenProvider {
	KAKAO("KAKAO"), SERVER("NAVER");
	
	private final String provider;
}
