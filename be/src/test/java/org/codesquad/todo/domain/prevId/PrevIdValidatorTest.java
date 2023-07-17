package org.codesquad.todo.domain.prevId;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PrevIdValidatorTest {

	@InjectMocks
	private PrevIdValidator prevIdValidator;

	@Mock
	private PrevIdRepository prevIdRepository;

	@DisplayName(" 참 거짓만 나와서 테스트 코드 작성해야하나??")
	@Test
	void verifyPrevIdFail() {
		// given
		Long columnId = 1L;
		Long prevId = 1L;
		given(prevIdRepository.isPrevIdExistsInColumn(any(), any())).willReturn(true);
		// when

		// then
		Assertions.assertDoesNotThrow(() -> prevIdValidator.verifyPrevId(columnId, prevId));
	}

}