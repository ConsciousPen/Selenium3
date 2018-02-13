package aaa.utils.excel.io.entity.area.table;

import java.util.List;
import java.util.Set;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelColumn;

public class TableColumn extends ExcelColumn<TableCell> {
	public TableColumn(int columnIndexInTable, int columnIndexOnSheet, Set<Integer> rowsIndexesOnSheet, ExcelTable table) {
		this(columnIndexInTable, columnIndexOnSheet, rowsIndexesOnSheet, table, table.getCellTypes());
	}

	public TableColumn(int columnIndexInTable, int columnIndexOnSheet, Set<Integer> rowsIndexesOnSheet, ExcelTable table, Set<CellType<?>> cellTypes) {
		super(columnIndexInTable, columnIndexOnSheet, rowsIndexesOnSheet, table, cellTypes);
	}

	public ExcelTable getTable() {
		return (ExcelTable) getArea();
	}

	public String getHeaderName() {
		return getTable().getHeader().getColumnName(getIndex());
	}

	@Override
	public int getIndexOnSheet() {
		return super.getIndexOnSheet();
	}

	@Override
	public List<Integer> getCellsIndexesOnSheet() {
		return super.getCellsIndexesOnSheet();
	}

	@Override
	public String toString() {
		return "TableColumn{" +
				"sheetName=" + getSheetName() +
				", columnIndex=" + getIndex() +
				", headerColumnName=" + getHeaderName() +
				", rowsNumber=" + getCellsNumber() +
				", cellTypes=" + getCellTypes() +
				", values=" + getStringValues() +
				'}';
	}

	public TableColumn copy(String destinationHeaderColumnName) {
		return (TableColumn) copy(getTable().getColumnIndex(destinationHeaderColumnName));
	}
}
