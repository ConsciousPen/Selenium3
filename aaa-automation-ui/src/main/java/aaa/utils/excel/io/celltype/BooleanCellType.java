package aaa.utils.excel.io.celltype;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.utils.excel.io.entity.ExcelCell;

public class BooleanCellType extends AbstractCellType<Boolean> {

	public BooleanCellType(Class<Boolean> endType) {
		super(endType);
	}

	@Override
	public Boolean getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getEndType()).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		return hasTextValue(cell) ? Boolean.valueOf(getText(cell)) : cell.getPoiCell().getBooleanCellValue();
	}

	@Override
	public void setValueTo(ExcelCell cell, Boolean value) {
		createPoiCellIfNull(cell).getPoiCell().setCellValue(value);
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		return cell.getPoiCell() == null || cell.getPoiCell().getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.BOOLEAN || hasTextValue(cell);
	}

	@Override
	public boolean hasTextValue(ExcelCell cell) {
		if (super.hasTextValue(cell)) {
			String value = getText(cell);
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}
		return false;
	}
}
