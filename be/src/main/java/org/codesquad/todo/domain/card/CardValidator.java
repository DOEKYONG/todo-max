package org.codesquad.todo.domain.card;

import org.codesquad.todo.config.ColumnNotFoundException;
import org.codesquad.todo.config.InvalidParentCardIdException;
import org.codesquad.todo.config.MemberNotFoundException;
import org.codesquad.todo.config.PrevIdNotFoundException;
import org.codesquad.todo.domain.column.ColumnValidator;
import org.codesquad.todo.domain.member.MemberValidator;
import org.codesquad.todo.domain.prevId.PrevIdValidator;
import org.springframework.stereotype.Component;

@Component
public class CardValidator {
	private final ColumnValidator columnValidator;
	private final MemberValidator memberValidator;
	private final PrevIdValidator prevIdValidator;



	public CardValidator(ColumnValidator columnValidator, MemberValidator memberValidator,
		PrevIdValidator prevIdValidator) {
		this.columnValidator = columnValidator;
		this.memberValidator = memberValidator;
		this.prevIdValidator = prevIdValidator;
	}

	public void verifyCard(Card card) {
		if (!memberValidator.exist(card.getMemberId())) {
			throw new MemberNotFoundException();
		}

		if (!columnValidator.exist(card.getColumnId())) {
			throw new ColumnNotFoundException();
		}
	}

	public void validateCardMove(Long targetId,Long columnId, Long prevId) {

		if (targetId.equals(prevId)) {
			throw new InvalidParentCardIdException();
		}
		if (!columnValidator.exist(columnId)) {
			throw new ColumnNotFoundException();
		}
		if (!prevIdValidator.verifyPrevId(columnId, prevId)) {
			throw new PrevIdNotFoundException();
		}

	}

	// todo 제자리 이동 요청 메서드
	// 제자리 이동 시 예외 발생 후 controllerAdvice 에서 200 응답주기
	public void validateCardMoveInPlace() {

	}
}
