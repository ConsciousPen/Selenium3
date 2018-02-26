package aaa.utils.excel.io.celltype;

import aaa.utils.excel.io.entity.area.ExcelCell;

public class IntegerCellType extends NumberCellType<Integer> {
	public IntegerCellType(Class<Integer> endType) {
		super(endType);
	}

	@Override
	protected Integer parseText(String t) {
		return Integer.valueOf(t);
	}

	@Override
	protected Integer parseDouble(double d) {
		return Double.valueOf(d).intValue();
	}

	@Override
	public boolean isNumeric(ExcelCell cell) {
		return super.isNumeric(cell) && isInteger(cell.getPoiCell().getNumericCellValue());
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		boolean isIntegerInTextFormat = false;
		if (super.hasValueInTextFormat(cell)) {
			try {
				isIntegerInTextFormat = isInteger(Double.valueOf(getText(cell)));
			} catch (NumberFormatException ignore) {
			}
		}
		return isIntegerInTextFormat;
	}

	@SuppressWarnings({"FloatingPointEquality", "NumericCastThatLosesPrecision"})
	private boolean isInteger(double d) {
		return d == (int) d;
	}
}
