package aaa.utils.excel.io.celltype;

public enum CellTypes {
	BOOLEAN(new BooleanCellType()),
	STRING(new StringCellType()),
	INTEGER(new IntegerCellType()),
	LOCAL_DATE_TIME(new LocalDateTimeCellType());

	private final CellType<?> cellType;

	<T> CellTypes(CellType<T> cellType) {
		this.cellType = cellType;
	}

	@SuppressWarnings("unchecked")
	public <T> CellType<T> get() {
		return (CellType<T>) cellType;
	}
}
