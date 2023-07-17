package org.codesquad.todo.domain.column;

import java.util.List;
import java.util.stream.Collectors;

import org.codesquad.todo.domain.card.CardReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ColumnService {
	private final ColumnReader columnReader;
	private final ColumnAppender columnAppender;
	private final CardReader cardReader;
	private final ColumnRemover columnRemover;
	private final ColumnManager columnManager;

	public ColumnService(ColumnReader columnReader, ColumnAppender columnAppender, CardReader cardReader,
		ColumnRemover columnRemover, ColumnManager columnManager) {
		this.columnReader = columnReader;
		this.columnAppender = columnAppender;
		this.cardReader = cardReader;
		this.columnRemover = columnRemover;
		this.columnManager = columnManager;
	}

	public Column save(Column column) {
		return columnAppender.append(column);
	}

	@Transactional(readOnly = true)
	public List<ColumnWithCard> findAll() {
		return columnReader.findAll().stream()
			.map(c -> new ColumnWithCard(c.getId(), c.getName(), cardReader.findAllByColumnId(c.getId())))
			.collect(Collectors.toUnmodifiableList());
	}

	public int update(Column column) {
		return columnManager.updateColumn(column);
	}

	public int delete(Long columnId) {
		return columnRemover.deleteColumn(columnId);
	}
}