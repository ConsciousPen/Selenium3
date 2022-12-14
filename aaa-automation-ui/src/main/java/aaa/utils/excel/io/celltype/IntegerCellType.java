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
		return super.isNumeric(cell) && isInteger(cell);
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		return super.hasValueInTextFormat(cell) && !getText(cell).contains(".");
	}

	@SuppressWarnings({"FloatingPointEquality", "NumericCastThatLosesPrecision"})
	private boolean isInteger(ExcelCell cell) {
		double d = cell.getPoiCell().getNumericCellValue();
		return d == (int) d && !getText(cell).contains(".");
	}
}
