package aaa.utils.excel.io.celltype;

import aaa.utils.excel.io.entity.cell.ExcelCell;

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
	public boolean hasFloatValue(ExcelCell cell) {
		return false;
	}
}
