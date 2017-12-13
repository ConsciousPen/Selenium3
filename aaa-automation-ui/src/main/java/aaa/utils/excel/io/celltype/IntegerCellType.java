package aaa.utils.excel.io.celltype;

import static toolkit.verification.CustomAssertions.assertThat;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;

public class IntegerCellType extends BaseCellType<Integer> {
	@Override
	public Integer getValueFrom(Cell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getName());
		if (isStoredAsText(cell)) {
			return Integer.valueOf(getText(cell));
		}
		Double doubleValue = normalizeCell(cell).getNumericCellValue();
		return doubleValue.intValue();
	}

	@Override
	public boolean isTypeOf(Cell cell) {
		return normalizeCell(cell).getCellType() == Cell.CELL_TYPE_NUMERIC || isStoredAsText(cell);
	}

	@Override
	public boolean isStoredAsText(Cell cell) {
		return super.isStoredAsText(cell) && NumberUtils.isCreatable(getText(cell));
	}
}
