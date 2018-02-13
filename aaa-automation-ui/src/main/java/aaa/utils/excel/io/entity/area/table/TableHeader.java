package aaa.utils.excel.io.entity.area.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.NotSupportedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class TableHeader extends TableRow {
	public TableHeader(Row headerRow, Set<Integer> columnsIndexesOnSheet, ExcelTable table) {
		super(headerRow, 0, headerRow.getRowNum() + 1, columnsIndexesOnSheet, table, Collections.singleton(ExcelCell.STRING_TYPE));
	}

	public List<String> getColumnsNames() {
		return getStringValues();
	}

	@Override
	protected Map<Integer, TableCell> gatherQueueIndexesAndCellsMap(Set<Integer> columnsIndexesOnSheet, Set<CellType<?>> cellTypes) {
		Map<Integer, TableCell> columnsIndexesAndCellsMap = new LinkedHashMap<>(columnsIndexesOnSheet.size());
		int columnIndexInTable = 1;
		for (Integer columnIndexOnSheet : columnsIndexesOnSheet) {
			Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnIndexOnSheet - 1) : null;
			HeaderCell headerCell = new HeaderCell(poiCell, columnIndexInTable, columnIndexOnSheet, this);
			columnsIndexesAndCellsMap.put(columnIndexInTable, headerCell);
			columnIndexInTable++;
		}
		return columnsIndexesAndCellsMap;
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"sheetName=" + getSheetName() +
				", headerColumns=" + getColumnsNames() +
				", columnsNumber=" + getCellsNumber() +
				'}';
	}

	@Override
	public boolean hasColumn(String headerColumnName) {
		return getColumnsNames().contains(headerColumnName);
	}

	@Override
	public TableCell getCell(String headerColumnName) {
		assertThat(hasColumn(headerColumnName)).as("There is no column name \"%1$s\" in the table %2$s", headerColumnName, getTable()).isTrue();
		return getCells().stream().filter(c -> c.hasValue(headerColumnName, ExcelCell.STRING_TYPE)).findFirst().get();
	}

	@Override
	public String getColumnName(int columnIndex) {
		assertThat(hasCell(columnIndex)).as("There is no column with %s index in the table %2$s", columnIndex, getTable()).isTrue();
		return getCell(columnIndex).getStringValue();
	}

	@Override
	public ExcelTable exclude() {
		throw new NotSupportedException("Excluding of table header's row is not supported");
	}

	@Override
	public TableHeader clear() {
		throw new NotSupportedException("Clearing of table header's row is not supported");
	}

	@Override
	public ExcelTable delete() {
		throw new NotSupportedException("Deletion of table header's row is not supported");
	}

	@Override
	public TableHeader registerCellType(CellType<?>... cellTypes) {
		if (Arrays.stream(cellTypes).anyMatch(t -> !ExcelCell.STRING_TYPE.equals(t))) {
			throw new NotSupportedException("Table header row does not support non string cell types");
		}
		return (TableHeader) super.registerCellType(cellTypes);
	}

	@Override
	protected void removeCellsIndexes(Integer... cellsIndexesInQueue) {
		super.removeCellsIndexes(cellsIndexesInQueue);
	}

	public boolean hasColumn(int columnIndex) {
		return hasCell(columnIndex);
	}

	public int getColumnIndex(String headerColumnName) {
		return getCell(headerColumnName).getColumnIndex();
	}

	public int getColumnIndexOnSheet(String headerColumnName) {
		return getCell(headerColumnName).getColumnIndexOnSheet();
	}

	public int getColumnIndexOnSheet(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no column index %s in the table %2$s", columnIndex, getTable()).isTrue();
		return getCell(columnIndex).getColumnIndexOnSheet();
	}
}
