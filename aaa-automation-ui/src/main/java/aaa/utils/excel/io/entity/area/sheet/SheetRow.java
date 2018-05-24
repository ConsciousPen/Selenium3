package aaa.utils.excel.io.entity.area.sheet;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelRow;

public class SheetRow extends ExcelRow<SheetCell> {
	public SheetRow(Row row, int rowIndexOnSheet, List<Integer> columnsIndexesOnSheet, ExcelSheet sheet) {
		this(row, rowIndexOnSheet, columnsIndexesOnSheet, sheet, sheet.getCellTypes());
	}

	public SheetRow(Row row, int rowIndexOnSheet, List<Integer> columnsIndexesOnSheet, ExcelSheet sheet, List<CellType<?>> cellTypes) {
		super(row, rowIndexOnSheet, rowIndexOnSheet, columnsIndexesOnSheet, sheet, cellTypes);
	}

	public ExcelSheet getSheet() {
		return (ExcelSheet) getArea();
	}

	@Override
	protected List<SheetCell> gatherCells(List<Integer> columnsIndexes, List<CellType<?>> cellTypes) {
		List<SheetCell> rowCells = new ArrayList<>(columnsIndexes.size());
		for (Integer columnIndex : columnsIndexes) {
			Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnIndex - 1) : null;
			SheetCell cell = new SheetCell(poiCell, columnIndex, this, cellTypes);
			rowCells.add(cell);
		}
		return rowCells;
	}
}
