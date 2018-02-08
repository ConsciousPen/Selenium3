package aaa.utils.excel.io.entity.area.table;

import java.util.LinkedHashMap;
import java.util.Map;
import aaa.utils.excel.io.entity.queue.EditableColumn;

public class TableColumn extends EditableColumn<TableCell> {
	private int tableColumnIndex;
	private Map<Integer, TableCell> tableCells;
	private ExcelTable table;

	public TableColumn(int tableColumnIndex, int columnIndex, ExcelTable table) {
		super(columnIndex, table.getExcelManager());
		this.tableColumnIndex = tableColumnIndex;
		this.table = table;
	}

	public ExcelTable getTable() {
		return this.table;
	}

	public String getHeaderName() {
		return getTable().getHeader().getColumnName(getIndex());
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, TableCell> getCellsMap() {
		if (this.tableCells == null) {
			this.tableCells = new LinkedHashMap<>(getTable().getRowsMap().size());
			for (Map.Entry<Integer, TableRow> rowEntry : getTable().getRowsMap().entrySet()) {
				this.tableCells.put(rowEntry.getKey(), rowEntry.getValue().getCell(getIndex()));
			}
		}
		return this.tableCells;
	}

	@Override
	protected ExcelTable getArea() {
		return getTable();
	}

	int getIndexOnSheet() {
		return this.index;
	}

/*	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<TableCell> iterator() {
		return (Iterator<TableCell>) new CellIterator(this);
	}*/

	@Override
	public int getIndex() {
		return tableColumnIndex;
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
