package aaa.utils.excel.io.celltype;

import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public abstract class AbstractCellType<T> implements CellType<T> {
	protected Class<T> endType;

	public AbstractCellType(Class<T> endType) {
		this.endType = endType;
	}

	@Override
	public Class<T> getEndType() {
		return endType;
	}

	public EditableCell createPoiCellIfNull(EditableCell cell) {
		Cell poiCell = cell.getPoiCell();
		if (poiCell == null) {
			poiCell = cell.getRow().getPoiRow().createCell(cell.getColumnIndex() - 1);
			cell.setPoiCell(poiCell);
		}
		return cell;
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

	public boolean hasValueInTextFormat(ExcelCell cell) {
		if (cell.getPoiCell() == null) {
			return false;
		}
		org.apache.poi.ss.usermodel.CellType type = cell.getPoiCell().getCellTypeEnum();
		return type == org.apache.poi.ss.usermodel.CellType.STRING || type == org.apache.poi.ss.usermodel.CellType.BLANK;
	}

	@Override
	public String getText(ExcelCell cell) {
		return ExcelCell.STRING_TYPE.getText(cell);
	}
}
