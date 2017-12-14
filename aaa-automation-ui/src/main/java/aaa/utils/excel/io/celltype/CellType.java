package aaa.utils.excel.io.celltype;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
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

	public abstract T getValueFrom(ExcelCell cell);

	public abstract boolean isTypeOf(ExcelCell cell);

	public boolean hasTextValue(ExcelCell cell) {
		return cell.getPoiCell().getCellType() == Cell.CELL_TYPE_STRING || cell.getPoiCell().getCellType() == Cell.CELL_TYPE_BLANK;
	}

	protected String getText(ExcelCell cell) {
		return CellTypes.STRING.get().getText(cell);
	}

	@Override
	public String toString() {
		return "CellType{" +
				"endType=" + endType +
				'}';
	}
}
