package aaa.utils.excel.io.entity.area.table;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import aaa.utils.excel.io.entity.area.ExcelColumn;

public class TableColumn extends ExcelColumn<TableCell> {
	private int tableColumnIndex;
	private Map<Integer, TableCell> tableCells;

	public TableColumn(int tableColumnIndex, int columnIndex, ExcelTable table) {
		super(columnIndex, table);
		this.tableColumnIndex = tableColumnIndex;
	}

	public ExcelTable getTable() {
		return getArea();
	}

	public String getHeaderName() {
		return getTable().getHeader().getColumnName(getIndex());
	}

	public int getIndex() {
		return tableColumnIndex;
	}

	@Override
	//@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, TableCell> getCellsMap() {
		if (this.tableCells == null) {
			List<TableRow> tableRows = getTable().getRows();
			this.tableCells = new LinkedHashMap<>(tableRows.size());
			/*for (Map.Entry<Integer, TableRow> rowEntry : getTable().getRowsMap().entrySet()) {
				this.tableCells.put(rowEntry.getKey(), rowEntry.getValue().getCell(getIndex()));
			}*/

			for (TableRow row : tableRows) {
				this.tableCells.put(row.getIndex(), row.getCell(getIndex()));
			}
		}
		return this.tableCells;
	}

	@Override
	protected ExcelTable getArea() {
		return getTable();
	}

/*	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<TableCell> iterator() {
		return (Iterator<TableCell>) new CellIterator(this);
	}*/

	@Override
	public int getIndexOnSheet() {
		return super.getIndexOnSheet();
	}

	@Override
	public String toString() {
		return "TableColumn{" +
				"columnIndex=" + getIndex() +
				", headerColumnName=" + getHeaderName() +
				", values=" + getValues() +
				'}';
	}

/*	@Override
	public EditableCellsArea<TableCell, ?, ?> exclude() {
		//TODO-dchubkov: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		return null;
	}

	@Override
	public EditableCellsArea<TableCell, ?, ?> delete() {
		//TODO-dchubkov: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		return null;
	}*/

	public TableColumn copy(String destinationHeaderColumnName) {
		for (TableCell cell : this) {
			cell.copy(cell.getRowIndex(), getTable().getColumnIndex(destinationHeaderColumnName));
		}
		return this;
	}
}
