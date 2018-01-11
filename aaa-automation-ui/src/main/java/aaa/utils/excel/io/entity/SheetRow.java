package aaa.utils.excel.io.entity;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public class SheetRow extends ExcelRow implements Iterable<ExcelCell> {
	private Map<Integer, ExcelCell> cells;

	public SheetRow(Row row, int rowIndex, CellsArea sheet) {
		super(row, rowIndex, sheet);
	}

	public ExcelSheet getSheet() {
		return (ExcelSheet) getArea();
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, ExcelCell> getCellsMap() {
		if (this.cells == null) {
			this.cells = new LinkedHashMap<>(getSheet().getColumnsMap().size());
			for (Map.Entry<Integer, SheetColumn> columnEntry : getSheet().getColumnsMap().entrySet()) {
				//this.cells.put(columnEntry.getKey(), columnEntry.getValue().getCell(getColumnIndex()));
				Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnEntry.getKey() - 1) : null;
				ExcelCell cell = new ExcelCell(poiCell, this, columnEntry.getKey());
				this.cells.put(columnEntry.getKey(), cell);
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
