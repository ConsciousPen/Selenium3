package aaa.utils.excel.io.entity.area.sheet;

import java.util.Set;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class SheetCell extends ExcelCell {
	public SheetCell(Cell cell, int columnIndexOnSheet, SheetRow row) {
		this(cell, columnIndexOnSheet, row, row.getCellTypes());
	}

	public SheetCell(Cell cell, int columnIndexOnSheet, SheetRow row, Set<CellType<?>> cellTypes) {
		super(cell, columnIndexOnSheet, columnIndexOnSheet, row, cellTypes);
	}

	public ExcelSheet getSheet() {
		return getRow().getSheet();
	}

	@Override
	public SheetRow getRow() {
		return (SheetRow) super.getRow();
	}

	@Override
	public SheetCell delete() {
		//TODO-dchubkov: implement delete ExcelCell and TableCell
		throw new NotImplementedException("Cell deletion is not implemented yet");
	}
}
