package aaa.utils.excel.io.celltype;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

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
		return hasValueInTextFormat(cell) ? Boolean.valueOf(getText(cell)) : cell.getPoiCell().getBooleanCellValue();
	}

	@Override
	public void setValueTo(EditableCell cell, Boolean value) {
		createPoiCellIfNull(cell).getPoiCell().setCellValue(value);
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		return cell.getPoiCell() == null || cell.getPoiCell().getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.BOOLEAN || hasValueInTextFormat(cell);
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		if (super.hasValueInTextFormat(cell)) {
			String value = getText(cell);
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}
		return false;
	}
}
