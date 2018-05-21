package aaa.utils.excel.io.celltype;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Objects;
import aaa.utils.excel.io.entity.area.ExcelCell;
import toolkit.exceptions.IstfException;

public abstract class AbstractCellType<T> implements CellType<T> {
	protected Class<T> endType;

	public AbstractCellType(Class<T> endType) {
		this.endType = endType;
	}

	@Override
	public Class<T> getEndType() {
		return endType;
	}

	@Override
	public T getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Unable to get value with \"%1$s\" type from %2$s", getEndType(), cell).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}

		T value;
		try {
			value = getRawValueFrom(cell);
		} catch (RuntimeException e) {
			throw new IstfException("Cannot get value from cell " + cell, e);
		}
		return value;
	}

	protected abstract T getRawValueFrom(ExcelCell cell);

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CellType<?> cellType = (CellType<?>) o;
		return Objects.equals(endType, cellType.getEndType());
	}

	@Override
	public int hashCode() {
		return Objects.hash(endType);
	}

	@Override
	public String toString() {
		return "CellType{" +
				"endType=" + getEndType() +
				'}';
	}

	@Override
	public String getText(ExcelCell cell) {
		return ExcelCell.STRING_TYPE.getText(cell);
	}

	public boolean hasValueInTextFormat(ExcelCell cell) {
		if (cell.getPoiCell() == null) {
			return false;
		}
		org.apache.poi.ss.usermodel.CellType type = cell.getPoiCell().getCellTypeEnum();
		return type == org.apache.poi.ss.usermodel.CellType.STRING || type == org.apache.poi.ss.usermodel.CellType.BLANK;
	}
}
