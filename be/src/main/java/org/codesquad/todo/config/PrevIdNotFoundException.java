package org.codesquad.todo.config;

import org.springframework.http.HttpStatus;

public class PrevIdNotFoundException extends ApiException {
	public static final String MESSAGE = "prevId를 찾을 수 없습니다.";

	public PrevIdNotFoundException() {
		super(HttpStatus.NOT_FOUND, MESSAGE);
	}
}



