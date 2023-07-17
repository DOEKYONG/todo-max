package org.codesquad.todo.domain.card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CardRepository {
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public static final String DELETE_CARD_SQL = "DELETE FROM card WHERE id = :id";
	public static final String CARD_ID_COLUMN = "id";

	public CardRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	}

	public Card save(Card card) {
		String sql = "INSERT INTO card(title, content, column_id, member_id, prev_card_id) VALUES(:title, :content, :columnId, :memberId, :prevCardId)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		SqlParameterSource parameters = new BeanPropertySqlParameterSource(card);
		jdbcTemplate.update(sql, parameters, keyHolder);
		return card.createInstanceWithId(keyHolder.getKey().longValue());
	}

	public Boolean exists(Long id) {
		String sql = "SELECT EXISTS(SELECT 1 FROM card WHERE id = :id)";
		return jdbcTemplate.queryForObject(sql, Map.of("id", id), Boolean.class);
	}

	public Optional<Card> findById(Long id) {
		String sql = "SELECT id, title, content, column_id, member_id, prev_card_id FROM card WHERE id = :id";
		return Optional.ofNullable(
			DataAccessUtils.singleResult(jdbcTemplate.query(sql, Map.of("id", id), CARD_ROW_MAPPER)));
	}

	public List<Card> findAllByColumnId(Long columnId) {
		String sql = "SELECT id, title, content, column_id, member_id, prev_card_id FROM card WHERE column_id = :columnId";
		return jdbcTemplate.query(sql, Map.of("columnId", columnId), CARD_ROW_MAPPER);
	}

	public List<Card> findAll() {
		String sql = "SELECT id, title, content, column_id, member_id, prev_card_id FROM card";
		return jdbcTemplate.query(sql, CARD_ROW_MAPPER);
	}

	public List<Card> findWithChildById(Long cardId) {
		String sql = " SELECT id, title, content, column_id, member_id, prev_card_id FROM card WHERE id = :id or prev_card_id = :id";
		return jdbcTemplate.query(sql, Map.of("id", cardId), CARD_ROW_MAPPER);
	}

	public int update(Card card) {
		String sql = "UPDATE card "
			+ "SET title = :title, "
			+ "content = :content, "
			+ "column_id = :columnId, "
			+ "member_id = :memberId, "
			+ "prev_card_id = :prevCardId "
			+ "WHERE id = :id";
		return jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(card));
	}

	public int updateBeforeDelete(Long cardId, Long changedPrevId) {
		String sql = "UPDATE card "
			+ "SET prev_card_id = :changedPrevId "
			+ "WHERE id = :cardId";

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("cardId", cardId);
		paramMap.put("changedPrevId", changedPrevId);

		return jdbcTemplate.update(sql, paramMap);
	}

	public int delete(Long id) {
		return jdbcTemplate.update(DELETE_CARD_SQL, new MapSqlParameterSource(Map.of(CARD_ID_COLUMN, id)));
	}

	private static final RowMapper<Card> CARD_ROW_MAPPER = (rs, rowNum) -> {
		long prevCardId = rs.getLong("prev_card_id");
		return new Card(rs.getLong("id"), rs.getString("title"), rs.getString("content"),
			rs.getLong("column_id"), rs.getLong("member_id"), prevCardId == 0 ? null : prevCardId);
	};

	private static final RowMapper<Long> ONLY_CARD_ID_ROW_MAPPER = (rs, rowNum) -> rs.getLong("id");

	public int moveCardInSameColumn(Long id, Long prevId) {
		String sql = "UPDATE card "
			+ "SET "
			+ "prev_card_id = :prevId "
			+ "WHERE id = :id";
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("prevId", prevId);
		return jdbcTemplate.update(sql, map);
	}

	public Long findIdByPrevId(Long prevId) {
		String sql;
		if (prevId == null) {
			sql = "SELECT id FROM card WHERE prev_card_id is null";
		} else {
			sql = "SELECT id FROM card WHERE prev_card_id = :prevId";

		}
		Map<String, Object> map = new HashMap<>();
		map.put("prevId", prevId);

		// return jdbcTemplate.queryForObject(sql, map, Long.class);
		return DataAccessUtils.singleResult(jdbcTemplate.query(sql, map, ONLY_CARD_ID_ROW_MAPPER));
	}

}

