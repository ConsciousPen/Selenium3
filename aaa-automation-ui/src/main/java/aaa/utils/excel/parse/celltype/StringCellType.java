package aaa.utils.excel.parse.celltype;

import org.apache.poi.ss.usermodel.Cell;

public class StringCellType extends BaseCellType<String> {
	@Override
	public String getValueFrom(Cell cell) {
		return null;
	}

	@Override
	public boolean isTypeOf(Cell cell) {
		return false;
	}
}
