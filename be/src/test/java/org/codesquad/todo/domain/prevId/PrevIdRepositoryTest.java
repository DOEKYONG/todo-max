package org.codesquad.todo.domain.prevId;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.sql.DataSource;

import org.codesquad.todo.domain.card.Card;
import org.codesquad.todo.domain.card.CardRepository;
import org.codesquad.todo.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

@JdbcTest
class PrevIdRepositoryTest {
	private CardRepository cardRepository;
	private final PrevIdRepository prevIdRepository;
	private DatabaseCleaner databaseCleaner;
	private Card SECOND_CARD;
	private Card FIRST_CARD;
	private Card THIRD_CARD;
	private Card LAST_CARD;

	@Autowired
	PrevIdRepositoryTest(JdbcTemplate jdbcTemplate) {
		this.cardRepository = new CardRepository(jdbcTemplate);
		this.prevIdRepository = new PrevIdRepository(jdbcTemplate);
		this.databaseCleaner = new DatabaseCleaner(jdbcTemplate);
	}

	@BeforeEach
	void setUp() {
		databaseCleaner.execute();
		FIRST_CARD = cardRepository.save(new Card(null, "카드 등록 구현", "카드 등록", 1L, 1L, 2L));
		SECOND_CARD = cardRepository.save(new Card(null, "Git 사용해 보기", "add, commit", 1L, 1L, 3L));
		THIRD_CARD = cardRepository.save(new Card(null, "카드 등록 구현", "카드 등록", 1L, 1L, 4L));
		LAST_CARD = cardRepository.save(new Card(null, "Git 사용해 보기", "add, commit", 1L, 1L, null));
	}

	@Test
	@DisplayName("해당 컬럼에 존재하는 prevId 가 유효한지 확인 ")
	void exist() {
		// given

		// when
		Boolean exist = prevIdRepository.isPrevIdExistsInColumn(FIRST_CARD.getColumnId(), FIRST_CARD.getPrevCardId());

		// then
		assertThat(exist).isTrue();
	}

	@Test
	@DisplayName("해당 컬럼에 존재하는 prevId 가 null 일 때"
		+ " SELECT EXISTS(SELECT 1 FROM card WHERE column_id = :columnId and prev_card_id is null)"
		+ " 가 실행되는지 확인 ")
	void prevIdIsNull() {
		// given

		// when
		Boolean exist = prevIdRepository.isPrevIdExistsInColumn(SECOND_CARD.getColumnId(), SECOND_CARD.getPrevCardId());

		// then
		assertThat(exist).isTrue();
	}

	@Test
	@DisplayName("해당 컬럼에 prevId가 존재하지 않으면 false 를 반환  ")
	void isNotExist() {
		// given

		// when
		Boolean exist = prevIdRepository.isPrevIdExistsInColumn(1L, 5L);

		// then
		assertThat(exist).isFalse();
	}

	@Test
	@DisplayName(" prevId 가 null 일 때 SELECT column_id FROM card where prev_card_id is null 쿼리 실행 되고 올바른 columId를 찾는다")
	void findColumnIdByPrevId() {
		// given

		// when
		Long columId = prevIdRepository.findColumnIdByPrevId(null);

		// then
		assertThat(columId).isEqualTo(1L);
	}

	@Test
	@DisplayName("targetId 로 prev_card_id 를 조회 하고 , requestPrevId 로 prev_card_id 로 조회했을 때 결과가 모두 존재하면 true")
	void conditionalUpdate() {
		Long targetId = 2L;
		Long requestPrevId = 4L;
		Boolean result = prevIdRepository.conditionalUpdate(targetId, requestPrevId);
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("targetId 로 prev_card_id 를 조회 하고 , requestPrevId 로 prev_card_id 를 조회했을 때 결과가 하나라도 존재하지 않으면 false"
		+ "targetId의 조회 결과가 없는 경우")
	void conditionalUpdate2() {
		Long targetId = 1L;
		Long requestPrevId = 4L;
		Boolean result = prevIdRepository.conditionalUpdate(targetId, requestPrevId);
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("targetId 로 prev_card_id 를 조회 하고 , requestPrevId 로 prev_card_id 로 조회했을 때 결과가 하나라도 존재하지 않으면 false"
		+ "requestPrevId의 조회결과가 없는 경우")
	void conditionalUpdate3() {
		Long targetId = 4L;
		Long requestPrevId = 1L;
		Boolean result = prevIdRepository.conditionalUpdate(targetId, requestPrevId);
		assertThat(result).isFalse();
	}

	@DisplayName("두번 업데이트 ")
	@Test
	void executeDoubleUpdate() {
		// given
		Long id = 3L;
		Long prevId = 1L;
		Long idInSecond = cardRepository.findIdByPrevId(prevId);
		Long prevIdInSecond = id;
		if (idInSecond == null) {
			idInSecond = cardRepository.findIdByPrevId(id);
			prevIdInSecond = cardRepository.findById(id).get().getPrevCardId();
		}

		// when
		int first = cardRepository.moveCardInSameColumn(id, prevId);
		int second = cardRepository.moveCardInSameColumn(idInSecond, prevIdInSecond);

		List<Card> cards = cardRepository.findAll();

		// then
		assertThat(first + second).isEqualTo(2);
		assertThat(cards).hasSize(4)
			.extracting("id", "prevCardId")
			.containsExactlyInAnyOrder(
				tuple(1L, 2L),
				tuple(2L, 4L),
				tuple(3L, 1L),
				tuple(4L, null)
			);

	}

}