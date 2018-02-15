package aaa.utils.excel.io.celltype;

import java.util.regex.Pattern;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class DoubleCellType extends NumberCellType<Double> {
	public DoubleCellType(Class<Double> endType) {
		super(endType);
	}

	@Override
	protected Double parseText(String t) {
		return Double.valueOf(t);
	}

	@Override
	protected Double parseDouble(double d) {
		return d;
	}

	@Override
	public boolean hasFloatValue(ExcelCell cell) {
		String textValue = getText(cell);
		if (textValue == null) {
			return false;
		}
		// Excluding values with only zeros after dot, e.g. "1.00' will return false
		Pattern floatNumberPattern = Pattern.compile("^[+-]?\\p{Digit}+\\.[0]*[1-9]+\\p{Digit}*$");
		return floatNumberPattern.matcher(textValue).matches();
	}
}
