package aaa.utils.excel.io.entity.queue;

import java.util.Arrays;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.cell.EditableCell;

public abstract class EditableRow<C extends EditableCell> extends ExcelRow<C> {
	protected EditableRow(Row row, int rowIndex, ExcelManager excelManager) {
		super(row, rowIndex, excelManager);
	}

	protected abstract EditableCellsArea<C, ?, ?> getArea();

	@Override
	public String toString() {
		return "EditableRow{" +
				"row=" + row +
				", index=" + index +
				", excelManager=" + excelManager +
				", cellTypes=" + cellTypes +
				'}';
	}

	public EditableRow<C> registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getCells().forEach(c -> c.registerCellType(cellTypes));
		return this;
	}

	public EditableRow<C> exclude() {
		getArea().excludeRows(this.getIndex());
		return this;
	}

	public EditableRow<C> copy(int destinationRowIndex) {
		for (C cell : this) {
			cell.copy(destinationRowIndex, cell.getColumnIndex());
		}
		return this;
	}

	public EditableCellsArea<C, ?, ?> delete() {
		getArea().deleteRows(getIndex());
		return getArea();
	}

	public EditableRow<C> clear() {
		getCells().forEach(EditableCell::clear);
		return this;
	}
}
