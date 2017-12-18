package aaa.utils.excel.io.celltype;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import aaa.utils.excel.io.entity.ExcelCell;

public class StringCellType extends CellType<String> {
	@Override
	public String getValueFrom(ExcelCell cell) {
		String value = "";
		Cell c = cell.getPoiCell();
		switch (c.getCellTypeEnum()) {
			case STRING:
				return getText(cell);
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(c)) {
					value = new DataFormatter().formatCellValue(c);
				} else {
					value = String.valueOf(ExcelCell.INTEGER_TYPE.getValueFrom(cell));
				}
				break;
			case BOOLEAN:
				value = String.valueOf(ExcelCell.BOOLEAN_TYPE.getValueFrom(cell));
				break;
			case ERROR:
				value = "Error: " + String.valueOf(c.getErrorCellValue()).trim();
				break;
			default:
				break;
		}

		return value;
	}

	@Override
	public void setValueTo(ExcelCell cell, String value) {
		cell.getPoiCell().setCellValue(value);
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		return hasTextValue(cell);
	}

	@Override
	protected String getText(ExcelCell cell) {
		return cell.getPoiCell().getStringCellValue().replace("\n", "").trim();
	}

	@Override
	public boolean hasTextValue(ExcelCell cell) {
		return true;
	}
}
