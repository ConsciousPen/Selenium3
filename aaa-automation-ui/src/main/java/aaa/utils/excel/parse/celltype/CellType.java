package aaa.utils.excel.parse.celltype;

import org.joda.time.LocalDateTime;

public enum CellType {
	BOOLEAN(new <Boolean>BooleanCellType()),
	STRING(new <String>StringCellType()),
	INTEGER(new <Integer>IntegerCellType()),
	LOCAL_DATE_TIME(new <LocalDateTime>LocalDateTimeCellType());

	private final BaseCellType<?> cellType;

	<C extends BaseCellType<T>, T> CellType(C cellType) {
		this.cellType = cellType;
	}

	@SuppressWarnings("unchecked")
	public <C extends BaseCellType<T>, T> C get() {
		return (C) cellType;
	}
}