package aaa.utils.excel.io.celltype;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import aaa.utils.excel.io.entity.area.ExcelCell;

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

		return DataFormatterHolder.formatter.formatCellValue(c).replace("\n", "").trim();
	}

	@Override
	public void setValueTo(ExcelCell cell, String value) {
		cell.getPoiCell().setCellValue(value);
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

	private static class DataFormatterHolder {
		private static final DataFormatter formatter = new DataFormatter();
	}
}
