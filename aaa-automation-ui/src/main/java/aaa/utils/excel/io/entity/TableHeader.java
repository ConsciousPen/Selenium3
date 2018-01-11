package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public class TableHeader extends ImmutableCellsQueue implements Iterable<ExcelCell> {
	protected Row headerRow;
	protected Map<Integer, ExcelCell> headerCells;

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
		return new ArrayList<>(getCellsMap().values().stream().map(ExcelCell::getStringValue).collect(Collectors.toSet()));
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, ExcelCell> getCellsMap() {
		if (this.headerCells == null) {
			this.headerCells = new LinkedHashMap<>(getTable().getColumnsIndexes().size());
			for (int i = 0; i < getTable().getColumnsIndexes().size(); i++) {
				int sheetColumnIndex = getTable().getColumnsIndexesOnSheet().get(i);
				int headerColumnIndex = getTable().getColumnsIndexes().get(i);
				ExcelRow headerRow = new SheetRow(getPoiRow(), getIndex(), getArea());
				ExcelCell headerCell = new ExcelCell(getPoiRow().getCell(sheetColumnIndex - 1), headerRow, sheetColumnIndex);
				this.headerCells.put(headerColumnIndex, headerCell);
			}
		}
		return this.headerCells;
	}

	int getColumnIndexOnSheet() {
		return this.index;
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<ExcelCell> iterator() {
		return (Iterator<ExcelCell>) new CellIterator(this);
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnsNames() +
				'}';
	}

	public boolean hasColumn(String headerColumnName) {
		return getColumnsNames().contains(headerColumnName);
	}

	public int getColumnIndex(String headerColumnName) {
		assertThat(hasColumn(headerColumnName)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return getCellsMap().entrySet().stream().filter(cm -> cm.getValue().getStringValue().equals(headerColumnName)).findFirst().get().getKey();
	}

	public int getColumnIndexOnSheet(String headerColumnName) {
		assertThat(hasColumn(headerColumnName)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return getCell(getColumnIndex(headerColumnName)).getColumnIndex();
	}

	public String getColumnName(int columnIndex) {
		assertThat(hasCell(columnIndex)).as("There is no column with %s index in table's header", columnIndex).isTrue();
		return getCellsMap().get(columnIndex).getStringValue();
	}
}
