package org.codesquad.todo.config;

import org.springframework.http.HttpStatus;

public class CardMoveInPlaceException extends ApiException {
	public static final String MESSAGE = "";

	public CardMoveInPlaceException() {
		super(HttpStatus.NOT_FOUND, MESSAGE);
	}
}