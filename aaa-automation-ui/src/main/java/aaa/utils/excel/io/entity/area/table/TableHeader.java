package aaa.utils.excel.io.entity.area.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.cell.ExcelCell;
import aaa.utils.excel.io.entity.iterator.CellIterator;
import aaa.utils.excel.io.entity.queue.CellsQueue;

public class TableHeader extends CellsQueue implements Iterable<HeaderCell> {
	protected Row headerRow;
	protected Map<Integer, HeaderCell> headerCells;

	public TableHeader(Row headerRow, ExcelTable table) {
		super(headerRow.getRowNum() + 1, table);
		this.headerRow = headerRow;
		this.cellTypes.removeIf(t -> !t.equals(ExcelCell.STRING_TYPE));
		assertThat(this.cellTypes).as("Table header row should have type " + ExcelCell.STRING_TYPE).isNotEmpty();
	}

	public Row getPoiRow() {
		return this.headerRow;
	}

	public ExcelTable getTable() {
		return (ExcelTable) getArea();
	}

	public List<String> getColumnsNames() {
		return getStringValues();
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, HeaderCell> getCellsMap() {
		if (this.headerCells == null) {
			this.headerCells = new LinkedHashMap<>(getTable().getColumnsIndexes().size());
			for (int i = 0; i < getTable().getColumnsIndexes().size(); i++) {
				int sheetColumnIndex = getTable().getColumnsIndexesOnSheet().get(i);
				int headerColumnIndex = getTable().getColumnsIndexes().get(i);
				TableRow headerRow = new TableRow(getPoiRow(), headerColumnIndex, sheetColumnIndex, getTable());
				HeaderCell headerCell = new HeaderCell(getPoiRow().getCell(sheetColumnIndex - 1), headerRow, headerColumnIndex, sheetColumnIndex);
				this.headerCells.put(headerColumnIndex, headerCell);
			}
		}
		return this.headerCells;
	}

	int getIndexOnSheet() {
		return this.index;
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<HeaderCell> iterator() {
		return (Iterator<HeaderCell>) new CellIterator(this);
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnsNames() +
				'}';
	}

	@Override
	public HeaderCell getCell(int queueIndex) {
		return (HeaderCell) super.getCell(queueIndex);
	}

	public boolean hasColumn(String headerColumnName) {
		return getColumnsNames().contains(headerColumnName);
	}

	public boolean hasColumn(int columnIndex) {
		return hasCell(columnIndex);
	}

	public HeaderCell getCell(String headerColumnName) {
		assertThat(hasColumn(headerColumnName)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return getCellsMap().entrySet().stream().filter(cm -> cm.getValue().getStringValue().equals(headerColumnName)).findFirst().get().getValue();
	}

	public int getColumnIndex(String headerColumnName) {
		return getCell(headerColumnName).getColumnIndex();
	}

	public int getColumnIndexOnSheet(String headerColumnName) {
		return getCell(headerColumnName).getColumnIndexOnSheet();
	}

	public int getColumnIndexOnSheet(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no column index %s in the table's header", columnIndex).isTrue();
		return getCell(columnIndex).getColumnIndexOnSheet();
	}

	public String getColumnName(int columnIndex) {
		assertThat(hasCell(columnIndex)).as("There is no column with %s index in table's header", columnIndex).isTrue();
		return getCell(columnIndex).getStringValue();
	}
}
