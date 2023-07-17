package org.codesquad.todo.domain.card;

import java.util.List;

import org.codesquad.todo.domain.prevId.PrevIdValidator;
import org.springframework.stereotype.Component;

@Component
public class CardManager {
	private final CardRepository cardRepository;
	private final CardReader cardReader;

	private final CardValidator cardValidator;

	private final PrevIdValidator prevIdValidator;

	public CardManager(CardRepository cardRepository, CardReader cardReader, CardValidator cardValidator,
		PrevIdValidator prevIdValidator) {
		this.cardRepository = cardRepository;
		this.cardReader = cardReader;
		this.cardValidator = cardValidator;
		this.prevIdValidator = prevIdValidator;
	}

	public int updatePrevCardId(Long updateTargetId, Long prevCardId) {
		Card findNextCard = cardReader.findById(updateTargetId);
		return cardRepository.update(findNextCard.createInstanceWithPrevId(prevCardId));
	}

	public int updateCard(Long id, String title, String content) {
		Card card = cardReader.findById(id);
		return cardRepository.update(card.createInstanceWithTitleAndContent(title, content));
	}

	public int updateCardBeforeDelete(Long id) {
		List<Card> cards = cardReader.findWithChildById(id);

		if (cards.size() == 2) {
			int cardToDeleteIndex = 0;
			int childCardIndex = 0;
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getId().equals(id)) {
					cardToDeleteIndex = i;
				} else {
					childCardIndex = i;
				}
			}
			return cardRepository.updateBeforeDelete(cards.get(childCardIndex).getId(),
				cards.get(cardToDeleteIndex).getPrevCardId());
		}

		return 0;
	}

	public void move(Long id, Long columnId, Long prevId) {
		Long targetId = cardReader.findById(id).getId();
		cardValidator.validateCardMove(targetId,columnId,prevId);
		// todo 제자리 이동 예외 추가
		Long idColumn = prevIdValidator.findColumnIdByPrevId(id);
		if (idColumn == columnId) {
			// 같은 컬럼일 때 메서드 구현
			updateInSameColumn(targetId, prevId);
		} else {
			// 다른 컬럼일 때 메서드 구현
		}
	}

	private Boolean checkUpdateCount(Long id, Long prevId) {
		return prevIdValidator.verifyUpdateCount(id, prevId);
	}

	private void updateInSameColumn(Long id, Long prevId) {
		if (!checkUpdateCount(id, prevId)) { // 2번만 업데이트 할 경우 메서드 실행
			executeDoubleUpdate(id, prevId);
		} else { // todo 3번 업데이트 할 경우 메서드

		}
	}

	private void executeDoubleUpdate(Long id, Long prevId) {
		Long idInSecond = cardReader.findIdByPrevId(prevId);
		Long prevIdInSecond = id;
		if (idInSecond == null) {
			idInSecond = cardReader.findIdByPrevId(id);
			prevIdInSecond = cardReader.findById(id).getPrevCardId();
		}
		cardRepository.moveCardInSameColumn(id, prevId);
		cardRepository.moveCardInSameColumn(idInSecond, prevIdInSecond);
	}
}
