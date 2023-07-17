package org.codesquad.todo.config;

import org.springframework.http.HttpStatus;

public class InvalidParentCardIdException extends ApiException {
	public static final String MESSAGE = "id와 prev_Id가 같아 이동할 수 없습니다. ";

	public InvalidParentCardIdException() {
		super(HttpStatus.NOT_FOUND, MESSAGE);
	}
}
