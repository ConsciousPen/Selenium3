package aaa.utils.excel.io.celltype;

import static toolkit.verification.CustomAssertions.assertThat;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import aaa.utils.excel.io.entity.area.ExcelCell;

public abstract class NumberCellType<T extends Number> extends AbstractCellType<T> {
	public NumberCellType(Class<T> endType) {
		super(endType);
	}

	@Override
	public T getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Unable to get value with \"%1$s\" type from %2$s", getEndType(), cell).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		if (hasValueInTextFormat(cell)) {
			return parseText(getText(cell));
		}
		return parseDouble(cell.getPoiCell().getNumericCellValue());
	}

	@Override
	public void setValueTo(ExcelCell cell, Number value) {
		cell.getPoiCell().setCellValue(value.doubleValue());
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		return cell.getPoiCell() == null || isNumeric(cell) || hasValueInTextFormat(cell);
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		return super.hasValueInTextFormat(cell) && NumberUtils.isCreatable(getText(cell));
	}

	protected abstract T parseText(String s);

	protected abstract T parseDouble(double d);

	protected boolean isNumeric(ExcelCell cell) {
		Cell c = cell.getPoiCell();
		return c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && !DateUtil.isCellDateFormatted(c);
	}
}
