package aaa.utils.excel.io.entity.area;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import aaa.utils.excel.io.celltype.CellType;

public abstract class ExcelColumn<CELL extends ExcelCell> extends CellsQueue<CELL> {
	protected ExcelColumn(int columnIndexInArea, int columnIndexOnSheet, Set<Integer> rowsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea) {
		this(columnIndexInArea, columnIndexOnSheet, rowsIndexesOnSheet, excelArea, excelArea.getCellTypes());
	}

	protected ExcelColumn(int columnIndexInArea, int columnIndexOnSheet, Set<Integer> rowsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea, Set<CellType<?>> cellTypes) {
		super(columnIndexInArea, columnIndexOnSheet, rowsIndexesOnSheet, excelArea, cellTypes);
	}

	@Override
	public ExcelArea<CELL, ?, ?> exclude() {
		getArea().excludeColumns(getIndex());
		return getArea();
	}

	@Override
	public ExcelColumn<CELL> copy(int destinationColumnIndex) {
		for (CELL cell : this) {
			cell.copy(cell.getRowIndex(), destinationColumnIndex);
		}
		return this;
	}

	@Override
	public ExcelArea<CELL, ?, ?> delete() {
		getArea().deleteColumns(getIndex());
		return getArea();
	}

	@Override
	public String toString() {
		return "ExcelColumn{" +
				"columnIndex=" + getIndex() +
				", values=" + getValues() +
				'}';
	}

	@Override
	protected Map<Integer, CELL> gatherQueueIndexesAndCellsMap(Set<Integer> rowsIndexesOnSheet, Set<CellType<?>> cellTypes) {
		Map<Integer, CELL> rowsIndexesAndCellsMap = new LinkedHashMap<>(rowsIndexesOnSheet.size());
		List<? extends ExcelRow<CELL>> areaRows = getArea().getRows();
		for (ExcelRow<CELL> row : areaRows) {
			rowsIndexesAndCellsMap.put(row.getIndex(), row.getCell(getIndex()));
		}
		return rowsIndexesAndCellsMap;
	}

	@Override
	protected Integer getCellIndexOnSheet(Integer cellIndexInQueue) {
		return getCell(cellIndexInQueue).getRowIndexOnSheet();
	}
}
