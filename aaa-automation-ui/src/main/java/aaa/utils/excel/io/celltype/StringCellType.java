package aaa.utils.excel.io.celltype;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public class StringCellType extends BaseCellType<String> {
	@Override
	public String getValueFrom(Cell cell) {
		Cell c = normalizeCell(cell);
		//assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getName());
		if (isStoredAsText(c)) {
			return getText(c);
		}
		switch (c.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					value = getDateTimeCellValue(cell).toString();
				} else {
					DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
					df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
					value = df.format(cell.getNumericCellValue());
				}
				break;
			case Cell.CELL_TYPE_STRING:
			case Cell.CELL_TYPE_FORMULA:
				value = cell.getStringCellValue().replace("\n", "").trim();
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				value = String.valueOf(cell.getBooleanCellValue()).trim();
				break;
			case Cell.CELL_TYPE_ERROR:
				value = "Error: " + String.valueOf(cell.getErrorCellValue()).trim();
				break;
			case Cell.CELL_TYPE_BLANK:
			default:
				break;
		}

	}

	@Override
	public boolean isTypeOf(Cell cell) {
		return isStoredAsText(cell);
	}

	public boolean isStoredAsDate(Cell cell) {
		return CellType.LOCAL_DATE_TIME.get().isTypeOf(cell);
	}

	public boolean isStoredAsInteger(Cell cell) {
		return CellType.INTEGER.get().isTypeOf(cell);
	}

	public boolean isStoredAsBoolean(Cell cell) {
		return CellType.BOOLEAN.get().isTypeOf(cell);
	}

	public boolean isStoredAsFormula(Cell cell) {
		return cell.getCellType() == Cell.CELL_TYPE_FORMULA;
	}

	@Override
	protected String getText(Cell cell) {
		return normalizeCell(cell).getStringCellValue().replace("\n", "").trim();
	}
}
