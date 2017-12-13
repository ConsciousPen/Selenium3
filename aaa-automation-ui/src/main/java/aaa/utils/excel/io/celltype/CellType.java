package aaa.utils.excel.io.celltype;

public enum CellType {
	BOOLEAN(new BooleanCellType()),
	STRING(new StringCellType()),
	INTEGER(new IntegerCellType()),
	LOCAL_DATE_TIME(new LocalDateTimeCellType());

	private final BaseCellType<?> cellType;

	<T> CellType(BaseCellType<T> cellType) {
		this.cellType = cellType;
	}

	@SuppressWarnings("unchecked")
	public <T> BaseCellType<T> get() {
		return (BaseCellType<T>) cellType;
	}
}
