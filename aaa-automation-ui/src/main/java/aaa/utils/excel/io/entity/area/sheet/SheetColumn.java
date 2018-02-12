package aaa.utils.excel.io.entity.area.sheet;

import java.util.Set;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelColumn;

public class SheetColumn extends ExcelColumn<SheetCell> {
	public SheetColumn(int columnIndexOnSheet, Set<Integer> rowsIndexesOnSheet, ExcelSheet sheet) {
		this(columnIndexOnSheet, rowsIndexesOnSheet, sheet, sheet.getCellTypes());
	}

	public SheetColumn(int columnIndexOnSheet, Set<Integer> rowsIndexesOnSheet, ExcelSheet sheet, Set<CellType<?>> cellTypes) {
		super(columnIndexOnSheet, columnIndexOnSheet, rowsIndexesOnSheet, sheet, cellTypes);
	}

	public ExcelSheet getSheet() {
		return (ExcelSheet) this.getArea();
	}
}
