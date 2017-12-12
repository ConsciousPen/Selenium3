package aaa.utils.excel;

import org.apache.poi.ss.usermodel.Cell;

public class BooleanCellType extends CellType<Boolean> {
	public BooleanCellType(ExcelParser excelParser) {
		super(excelParser);
	}

	@Override
	public Boolean getValueFor(Cell cell) {
		if (isStoredAsText(cell)) {
			return Boolean.valueOf(getParser(cell).getStringValue(cell).trim());
		}
		return cell.getBooleanCellValue();
	}

	@Override
	public boolean isTypeOf(Cell cell) {
		return cell.getCellType() == Cell.CELL_TYPE_BOOLEAN || isStoredAsText(cell);
	}

	public boolean isStoredAsText(Cell cell) {
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			String value = getParser(cell).getStringValue(cell).trim();
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}
		return false;
	}
}
