package org.codesquad.todo.domain.prevId;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PrevIdRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public PrevIdRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	}

	public Boolean isPrevIdExistsInColumn(Long columnId, Long prevId) {
		String sql;
		if (prevId == null) {
			sql = "SELECT EXISTS(SELECT 1 FROM card WHERE column_id = :columnId and prev_card_id is null)";
		} else {
			sql = "SELECT EXISTS(SELECT 1 FROM card WHERE column_id = :columnId and prev_card_id = :prevId)";
		}
		Map<String, Object> map = new HashMap<>();
		map.put("columnId", columnId);
		map.put("prevId", prevId);
		return jdbcTemplate.queryForObject(sql, map, Boolean.class);
	}

	public Long findColumnIdByPrevId(Long prevId) {
		String sql;
		if (prevId == null) {
			sql = "SELECT column_id FROM card where prev_card_id is null ";
		} else {
			sql = "SELECT column_id FROM card where prev_card_id = :prevId ";
		}
		Map<String, Object> map = new HashMap<>();
		map.put("prevId", prevId);
		return jdbcTemplate.queryForObject(sql, map, Long.class);
	}

	public Boolean conditionalUpdate(Long id, Long prevId) {
		String sql;
		if (prevId == null) {
			sql = "SELECT CASE WHEN SUM(result) = 2 THEN TRUE ELSE FALSE END "
				+ "FROM ( "
				+ "    SELECT EXISTS(SELECT 1 FROM card WHERE prev_card_id is null) AS result "
				+ "    UNION ALL "
				+ "    SELECT EXISTS(SELECT 1 FROM card WHERE prev_card_id = :id) AS result "
				+ ") AS subquery";
		} else {
			sql = "SELECT CASE WHEN SUM(result) = 2 THEN TRUE ELSE FALSE END "
				+ "FROM ( "
				+ "    SELECT EXISTS(SELECT 1 FROM card WHERE prev_card_id = :prevId) AS result "
				+ "    UNION ALL "
				+ "    SELECT EXISTS(SELECT 1 FROM card WHERE prev_card_id = :id) AS result "
				+ ") AS subquery";
		}
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("prevId", prevId);
		return jdbcTemplate.queryForObject(sql, map, Boolean.class);
	}
}
