package aaa.utils.excel.io.entity.area;

import java.util.List;
import com.google.common.collect.ImmutableSortedMap;
import aaa.utils.excel.io.celltype.CellType;

public abstract class ExcelColumn<CELL extends ExcelCell> extends CellsQueue<CELL> {
	protected ExcelColumn(int columnIndexInArea, int columnIndexOnSheet, List<Integer> rowsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea) {
		this(columnIndexInArea, columnIndexOnSheet, rowsIndexesOnSheet, excelArea, excelArea.getCellTypes());
	}

	protected ExcelColumn(int columnIndexInArea, int columnIndexOnSheet, List<Integer> rowsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea, List<CellType<?>> cellTypes) {
		super(columnIndexInArea, columnIndexOnSheet, rowsIndexesOnSheet, excelArea, cellTypes);
	}

	@Override
	public ExcelArea<CELL, ?, ?> exclude() {
		return getArea().excludeColumns(getIndex());
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
		return getArea().deleteColumns(getIndex());
	}

	@Override
	public String toString() {
		return "ExcelColumn{" +
				"sheetName=" + getSheetName() +
				", columnIndex=" + getIndex() +
				", rowsNumber=" + getCellsNumber() +
				", cellTypes=" + getCellTypes() +
				", values=" + getStringValues() +
				'}';
	}

	@Override
	protected ImmutableSortedMap<Integer, CELL> gatherQueueIndexesAndCellsMap(List<Integer> rowsIndexesOnSheet, List<CellType<?>> cellTypes) {
		ImmutableSortedMap.Builder<Integer, CELL> queueIndexesAndCellsBuilder = ImmutableSortedMap.naturalOrder();
		List<? extends ExcelRow<CELL>> areaRows = getArea().getRows();
		for (ExcelRow<CELL> row : areaRows) {
			queueIndexesAndCellsBuilder.put(row.getIndex(), row.getCell(getIndex()));
		}
		return queueIndexesAndCellsBuilder.build();
	}

	@Override
	protected Integer getCellIndexOnSheet(Integer cellIndexInQueue) {
		return getCell(cellIndexInQueue).getRowIndexOnSheet();
	}
}
