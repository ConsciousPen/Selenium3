package aaa.utils.excel.io.celltype;

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
	public boolean isNumeric(ExcelCell cell) {
		return super.isNumeric(cell) && getText(cell).contains(".");
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		return super.hasValueInTextFormat(cell) && getText(cell).contains(".");
	}
}
