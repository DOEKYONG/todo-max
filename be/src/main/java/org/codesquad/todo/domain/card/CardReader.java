package org.codesquad.todo.domain.card;

import java.util.List;

import org.codesquad.todo.config.CardNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CardReader {

	private final CardRepository cardRepository;

	public CardReader(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}

	public Card findById(Long id) {
		return cardRepository.findById(id)
			.orElseThrow(CardNotFoundException::new);
	}

	public List<Card> findAllByColumnId(Long columnId) {
		return cardRepository.findAllByColumnId(columnId);
	}

	public List<Card> findWithChildById(Long cardId) {
		return cardRepository.findWithChildById(cardId);
	}

	public Long findIdByPrevId(Long prevId) {
		return cardRepository.findIdByPrevId(prevId);
	}

}
