package org.codesquad.todo.domain.card;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
	@InjectMocks
	private CardService cardService;

	@Mock
	private CardAppender cardAppender;

	@DisplayName("카드를 저장한다")
	@Test
	void saveCard() {
		// given
		Card card = new Card(null, "Git 사용해 보기", "add, commit", 1L, 1L, null);
		given(cardAppender.append(any())).willReturn(card.createInstanceWithId(1L));

		// when
		Card savedCard = cardService.saveCard(card);

		// then
		assertThat(savedCard.getId()).isEqualTo(1L);
		assertThat(savedCard.getTitle()).isEqualTo(card.getTitle());
		assertThat(savedCard.getContent()).isEqualTo(card.getContent());
		assertThat(savedCard.getMemberId()).isEqualTo(card.getMemberId());
		assertThat(savedCard.getPrevCardId()).isEqualTo(card.getPrevCardId());
	}
}
