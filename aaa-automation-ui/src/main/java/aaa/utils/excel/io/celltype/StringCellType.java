package aaa.utils.excel.io.celltype;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public class StringCellType extends AbstractCellType<String> {
	public StringCellType(Class<String> endType) {
		super(endType);
	}

	@Override
	public String getValueFrom(ExcelCell cell) {
		Cell c = cell.getPoiCell();
		if (c == null) {
			return null;
		}
		return new DataFormatter().formatCellValue(c).replace("\n", "").trim();
	}

	@Override
	public void setValueTo(EditableCell cell, String value) {
		createPoiCellIfNull(cell).getPoiCell().setCellValue(value);
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		return hasValueInTextFormat(cell);
	}

	@Override
	public String getText(ExcelCell cell) {
		return getValueFrom(cell);
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		return true;
	}
}
