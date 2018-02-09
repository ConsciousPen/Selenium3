package aaa.utils.excel.io.entity.area.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.ExcelRow;

public class TableHeader extends ExcelRow<TableCell> {
	protected Row headerRow;
	protected Map<Integer, TableCell> headerCells;

	public TableHeader(Row headerRow, ExcelTable table) {
		super(headerRow, headerRow.getRowNum() + 1, table);
		this.headerRow = headerRow;
		this.cellTypes.removeIf(t -> !t.equals(ExcelCell.STRING_TYPE));
		assertThat(this.cellTypes).as("Table header row should have type " + ExcelCell.STRING_TYPE).isNotEmpty();
	}

	public ExcelTable getTable() {
		return (ExcelTable) getArea();
	}

	public List<String> getColumnsNames() {
		return getStringValues();
	}

	@Override
	//@SuppressWarnings({"AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, TableCell> getCellsMap() {
		if (this.headerCells == null) {
			this.headerCells = new LinkedHashMap<>(getTable().getColumnsIndexes().size());
			for (int i = 0; i < getTable().getColumnsIndexes().size(); i++) {
				int sheetColumnIndex = getTable().getColumnsIndexesOnSheet().get(i);
				int headerColumnIndex = getTable().getColumnsIndexes().get(i);
				TableRow headerRow = new TableRow(getPoiRow(), headerColumnIndex, sheetColumnIndex, getTable());
				TableCell headerCell = new TableCell(getPoiRow().getCell(sheetColumnIndex - 1), headerRow, headerColumnIndex, sheetColumnIndex);
				this.headerCells.put(headerColumnIndex, headerCell);
			}
		}
		return this.headerCells;
	}

	@Override
	public int getIndexOnSheet() {
		return super.getIndexOnSheet();
	}

	@Override
	public Row getPoiRow() {
		return this.headerRow;
	}

	public int getIndex() {
		return 0;
	}

	/*@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<HeaderCell> iterator() {
		return (Iterator<HeaderCell>) new CellIterator(this);
	}*/

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnsNames() +
				'}';
	}

	/*@Override
	public TableCell getCell(int queueIndex) {
		return super.getCell(queueIndex);
	}*/

	public boolean hasColumn(String headerColumnName) {
		return getColumnsNames().contains(headerColumnName);
	}

	public boolean hasColumn(int columnIndex) {
		return hasCell(columnIndex);
	}

	public TableCell getCell(String headerColumnName) {
		assertThat(hasColumn(headerColumnName)).as("There is no column name \"%1$s\" in the table %2$s", headerColumnName, getTable()).isTrue();
		return getCellsMap().entrySet().stream().filter(cm -> cm.getValue().getStringValue().equals(headerColumnName)).findFirst().get().getValue();
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

	public String getColumnName(int columnIndex) {
		assertThat(hasCell(columnIndex)).as("There is no column with %s index in the table %2$s", columnIndex, getTable()).isTrue();
		return getCell(columnIndex).getStringValue();
	}
}
