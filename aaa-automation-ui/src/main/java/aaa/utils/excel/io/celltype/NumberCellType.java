package aaa.utils.excel.io.celltype;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public abstract class NumberCellType<T extends Number> extends AbstractCellType<T> {
	public NumberCellType(Class<T> endType) {
		super(endType);
	}

	@Override
	public T getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getEndType()).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		if (hasValueInTextFormat(cell)) {
			return parseText(getText(cell));
		}
		return parseDouble(cell.getPoiCell().getNumericCellValue());
	}

	@Override
	public void setValueTo(EditableCell cell, Number value) {
		createPoiCellIfNull(cell).getPoiCell().setCellValue(value.doubleValue());
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		return cell.getPoiCell() == null || isNumeric(cell) || hasValueInTextFormat(cell);
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		return super.hasValueInTextFormat(cell) && NumberUtils.isCreatable(getText(cell));
	}

	public abstract boolean hasFloatValue(ExcelCell cell);

	protected abstract T parseText(String s);

	protected abstract T parseDouble(double d);

	protected boolean isNumeric(ExcelCell cell) {
		Cell c = cell.getPoiCell();
		return c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && !DateUtil.isCellDateFormatted(c);
	}
}
