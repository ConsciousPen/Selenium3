package aaa.utils.excel.io.entity.area.sheet;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelRow;

public class SheetRow extends ExcelRow<SheetCell> {
	public SheetRow(Row row, int rowIndexOnSheet, Set<Integer> columnsIndexesOnSheet, ExcelSheet sheet) {
		this(row, rowIndexOnSheet, columnsIndexesOnSheet, sheet, sheet.getCellTypes());
	}

	public SheetRow(Row row, int rowIndexOnSheet, Set<Integer> columnsIndexesOnSheet, ExcelSheet sheet, Set<CellType<?>> cellTypes) {
		super(row, rowIndexOnSheet, rowIndexOnSheet, columnsIndexesOnSheet, sheet, cellTypes);
	}

	public ExcelSheet getSheet() {
		return (ExcelSheet) getArea();
	}

	@Override
	protected Map<Integer, SheetCell> gatherQueueIndexesAndCellsMap(Set<Integer> columnsIndexes, Set<CellType<?>> cellTypes) {
		Map<Integer, SheetCell> columnsIndexesAndCellsMap = new LinkedHashMap<>(columnsIndexes.size());
		for (Integer columnIndex : columnsIndexes) {
			Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnIndex - 1) : null;
			SheetCell cell = new SheetCell(poiCell, columnIndex, this, cellTypes);
			columnsIndexesAndCellsMap.put(columnIndex, cell);
		}
		return columnsIndexesAndCellsMap;
	}
}
