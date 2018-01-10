package aaa.utils.excel.io.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public class SheetRow extends ExcelRow implements Iterable<ExcelCell> {
	private Map<Integer, ExcelCell> cells;

	public SheetRow(Row row, int rowIndex, ExcelSheet sheet) {
		super(row, rowIndex, sheet);
	}

	public ExcelSheet getSheet() {
		return (ExcelSheet) getArea();
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, ExcelCell> getCellsMap() {
		if (this.cells == null) {
			this.cells = new HashMap<>(getSheet().getColumnsMap().size());
			for (Map.Entry<Integer, SheetColumn> columnEntry : getSheet().getColumnsMap().entrySet()) {
				this.cells.put(columnEntry.getKey(), columnEntry.getValue().getCell(getIndex()));
			}
		}
		return this.cells;
	}


	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<ExcelCell> iterator() {
		return (Iterator<ExcelCell>) new CellIterator(this);
	}
}
