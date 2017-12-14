package aaa.utils.excel.io.celltype;

import static toolkit.verification.CustomAssertions.assertThat;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import aaa.utils.excel.io.entity.ExcelCell;

public class IntegerCellType extends CellType<Integer> {
	@Override
	public Integer getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getEndType()).isTrue();
		if (hasTextValue(cell)) {
			return Integer.valueOf(getText(cell));
		}
		Double doubleValue = cell.getPoiCell().getNumericCellValue();
		return doubleValue.intValue();
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		Cell c = cell.getPoiCell();
		return c.getCellType() == Cell.CELL_TYPE_NUMERIC && !DateUtil.isCellDateFormatted(c) || hasTextValue(cell);
	}

	@Override
	public boolean hasTextValue(ExcelCell cell) {
		return super.hasTextValue(cell) && NumberUtils.isCreatable(getText(cell));
	}
}
