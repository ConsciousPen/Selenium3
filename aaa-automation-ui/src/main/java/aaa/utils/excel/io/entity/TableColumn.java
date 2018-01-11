package aaa.utils.excel.io.entity;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public class TableColumn extends ExcelColumn implements Iterable<TableCell> {
	private int tableColumnIndex;
	private Map<Integer, TableCell> tableCells;

	public TableColumn(int tableColumnIndex, int columnIndex, ExcelTable table) {
		super(columnIndex, table);
		this.tableColumnIndex = tableColumnIndex;
	}

	public ExcelTable getTable() {
		return (ExcelTable) getArea();
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, TableCell> getCellsMap() {
		if (this.tableCells == null) {
			this.tableCells = new LinkedHashMap<>(getTable().getRowsMap().size());
			for (Map.Entry<Integer, TableRow> rowEntry : getTable().getRowsMap().entrySet()) {
				this.tableCells.put(rowEntry.getKey(), (TableCell) rowEntry.getValue().getCell(getIndex()));
			}
		}
		return this.tableCells;
	}

	int getIndexOnSheet() {
		return this.index;
	}

	@Override
	public int getIndex() {
		return tableColumnIndex;
	}

	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<TableCell> iterator() {
		return (Iterator<TableCell>) new CellIterator(this);
	}

	@Override
	public String toString() {
		return "TableColumn{" +
				"columnIndex=" + getIndex() +
				", values=" + getValues() +
				'}';
	}

	public ExcelColumn copy(String destinationHeaderColumnName) {
		for (ExcelCell cell : getCells()) {
			cell.copy(cell.getRowIndex(), getTable().getColumnIndex(destinationHeaderColumnName));
		}
		return this;
	}
}
