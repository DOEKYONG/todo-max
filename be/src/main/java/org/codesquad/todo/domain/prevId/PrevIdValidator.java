package org.codesquad.todo.domain.prevId;

import org.springframework.stereotype.Component;

@Component
public class PrevIdValidator {
	private final PrevIdRepository prevIdRepository;

	public PrevIdValidator(PrevIdRepository prevIdRepository) {
		this.prevIdRepository = prevIdRepository;
	}

	public Boolean verifyPrevId(Long columnId, Long prevId) {
		return prevIdRepository.isPrevIdExistsInColumn(columnId, prevId);
	}

	public Long findColumnIdByPrevId(Long id) {
		return prevIdRepository.findColumnIdByPrevId(id);
	}

	public Boolean verifyUpdateCount(Long id, Long prevId) {
		return prevIdRepository.conditionalUpdate(id, prevId);
	}

}
