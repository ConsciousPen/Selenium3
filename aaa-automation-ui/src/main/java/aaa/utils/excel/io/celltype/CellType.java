package aaa.utils.excel.io.celltype;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import aaa.utils.excel.io.entity.ExcelCell;

public abstract class CellType<T> {
	protected Class<?> endType;

	public CellType() {
		this.endType = (Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Class<?> getEndType() {
		return endType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CellType<?> cellType = (CellType<?>) o;
		return Objects.equals(endType, cellType.endType);
	}

	@Override
	public int hashCode() {

		return Objects.hash(endType);
	}

	@Override
	public String toString() {
		return "CellType{" +
				"endType=" + endType +
				'}';
	}

	public abstract T getValueFrom(ExcelCell cell);

	public abstract void setValueTo(ExcelCell cell, T value);

	public abstract boolean isTypeOf(ExcelCell cell);

	public boolean hasTextValue(ExcelCell cell) {
		org.apache.poi.ss.usermodel.CellType type = cell.getPoiCell().getCellTypeEnum();
		return type == org.apache.poi.ss.usermodel.CellType.STRING  || type == org.apache.poi.ss.usermodel.CellType.BLANK;
	}

	protected String getText(ExcelCell cell) {
		return ExcelCell.STRING_TYPE.getText(cell);
	}
}
