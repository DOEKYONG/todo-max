package org.codesquad.todo.domain.card;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.codesquad.todo.config.ColumnNotFoundException;
import org.codesquad.todo.config.MemberNotFoundException;
import org.codesquad.todo.config.PrevIdNotFoundException;
import org.codesquad.todo.domain.column.ColumnValidator;
import org.codesquad.todo.domain.member.MemberValidator;
import org.codesquad.todo.domain.prevId.PrevIdValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CardValidatorTest {
	@InjectMocks
	private CardValidator cardValidator;

	@Mock
	private ColumnValidator columnValidator;

	@Mock
	private MemberValidator memberValidator;

	@Mock
	private PrevIdValidator prevIdValidator;

	@DisplayName("카드 정보를 가지고 카드 생성을 하는 검증을 한다.")
	@Test
	void verifyCard() {
		// given
		Card card = new Card(null, "Git 사용해 보기", "add, commit", 1L, 1L, null);
		given(columnValidator.exist(any())).willReturn(true);
		given(memberValidator.exist(any())).willReturn(true);

		// when

		// then
		Assertions.assertDoesNotThrow(() -> cardValidator.verifyCard(card));
	}

	@DisplayName("카드를 검증할 때 해당 멤버가 없다면 에러를 반환한다.")
	@Test
	void verifyCardFail() {
		// given
		Card card = new Card(null, "Git 사용해 보기", "add, commit", 1L, 1L, null);
		given(memberValidator.exist(any())).willReturn(false);

		// when

		// then
		Assertions.assertThrows(MemberNotFoundException.class, () -> cardValidator.verifyCard(card));
	}

	@DisplayName("카드를 검증할 때 해당 칼럼이 없다면 에러를 반환한다.")
	@Test
	void verifyCardFail2() {
		// given
		Card card = new Card(null, "Git 사용해 보기", "add, commit", 1L, 1L, null);
		given(memberValidator.exist(any())).willReturn(true);
		given(columnValidator.exist(any())).willReturn(false);

		// when

		// then
		Assertions.assertThrows(ColumnNotFoundException.class, () -> cardValidator.verifyCard(card));
	}

	@DisplayName("유효한 컬럼에서 prev_Id가 존재 하지않으면 PrevIdNotFoundException 예외 발생 ")
	@Test
	void verifyPrevIdInColumn() {
		// given
		Card card = new Card(null, "Git 사용해 보기", "add, commit", 1L, 1L, null);
		given(columnValidator.exist(any())).willReturn(true);
		given(prevIdValidator.verifyPrevId(any(), any())).willReturn(false);
		// when
		// then
		// Assertions.assertThrows(PrevIdNotFoundException.class,
		// 	() -> cardValidator.verifyPrevIdInColumn(card.getColumnId(),
		// 		card.getPrevCardId())); // todo targetId 추가해서 검증하기
	}
}
