package aaa.utils.excel.io.celltype;

import static toolkit.verification.CustomAssertions.assertThat;
import org.apache.poi.ss.usermodel.Cell;

public class BooleanCellType extends BaseCellType<Boolean> {

	@Override
	public Boolean getValueFrom(Cell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getName());
		return isStoredAsText(cell) ? Boolean.valueOf(getText(cell)) : cell.getBooleanCellValue();
	}

	@Override
	public boolean isTypeOf(Cell cell) {
		return normalizeCell(cell).getCellType() == Cell.CELL_TYPE_BOOLEAN || isStoredAsText(cell);
	}

	@Override
	public boolean isStoredAsText(Cell cell) {
		if (super.isStoredAsText(cell)) {
			String value = getText(cell);
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}
		return false;
	}
}
