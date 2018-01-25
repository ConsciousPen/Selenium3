package aaa.utils.excel.io.celltype;

import static toolkit.verification.CustomAssertions.assertThat;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public class IntegerCellType extends AbstractCellType<Integer> {
	public IntegerCellType(Class<Integer> endType) {
		super(endType);
	}

	@Override
	public Integer getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getEndType()).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		if (hasTextValue(cell)) {
			return Integer.valueOf(getText(cell));
		}
		Double doubleValue = cell.getPoiCell().getNumericCellValue();
		return doubleValue.intValue();
	}

	@Override
	public void setValueTo(EditableCell cell, Integer value) {
		createPoiCellIfNull(cell).getPoiCell().setCellValue(value.doubleValue());
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		Cell c = cell.getPoiCell();
		return c == null
				|| c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && !DateUtil.isCellDateFormatted(c) && !ExcelCell.DOUBLE_TYPE.isTypeOf(cell)
				|| hasTextValue(cell);
	}

	@Override
	public boolean hasTextValue(ExcelCell cell) {
		return super.hasTextValue(cell) && NumberUtils.isCreatable(getText(cell));
	}
}
