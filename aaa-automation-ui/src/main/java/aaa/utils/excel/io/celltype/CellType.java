package aaa.utils.excel.io.celltype;

import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public interface CellType<T> {
	Class<T> getEndType();

	T getValueFrom(ExcelCell cell);

	void setValueTo(EditableCell cell, T value);

	boolean isTypeOf(ExcelCell cell);

	String getText(ExcelCell cell);
}
