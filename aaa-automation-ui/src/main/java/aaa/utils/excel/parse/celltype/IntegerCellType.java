package aaa.utils.excel.parse.celltype;

import org.apache.poi.ss.usermodel.Cell;

public class IntegerCellType extends BaseCellType<Integer> {
	@Override
	public Integer getValueFrom(Cell cell) {
		return null;
	}

	@Override
	public boolean isTypeOf(Cell cell) {
		return false;
	}
}
