package aaa.utils.excel.io.entity.cell;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.queue.ExcelRow;

public class EditableCell extends ExcelCell {

	protected static Logger log = LoggerFactory.getLogger(EditableCell.class);

	public EditableCell(Cell cell, ExcelRow row, int columnIndex) {
		super(cell, row, columnIndex);
	}

	public <T> EditableCell setValue(T value) {
		return setValue(value, getType(value));
	}

	public EditableCell setPoiCell(Cell cell) {
		this.cell = cell;
		return this;
	}

	EditableCell setCellTypes(Set<CellType<?>> cellTypes) {
		this.cellTypes = new HashSet<>(cellTypes);
		return this;
	}

	public EditableCell registerCellType(CellType<?>... cellTypes) {
		Set<CellType<?>> typesCopy = getCellTypes();
		typesCopy.addAll(Arrays.asList(cellTypes));
		this.cellTypes = typesCopy;
		return this;
	}

	public <T> EditableCell setValue(T value, CellType<T> valueType) {
		assertThat(valueType).as("%s cell does not have appropriate type to set %s value type", this, value.getClass()).isNotNull();
		valueType.setValueTo(this, value);
		return this;
	}

	public EditableCell excludeColumn() {
		((EditableCellsArea) getRow().getArea()).excludeColumns(getColumnIndex());
		return this;
	}

	public EditableCell clear() {
		if (!isEmpty()) {
			getRow().getPoiRow().removeCell(getPoiCell());
			setPoiCell(null);
		}
		return this;
	}

	public EditableCell copy(int destinationRowIndex) {
		return copy(destinationRowIndex, getColumnIndex());
	}

	public EditableCell copy(int destinationRowIndex, int destinationCellIndex) {
		return copy(destinationRowIndex, destinationCellIndex, true, true, true);
	}

	public EditableCell copy(int destinationRowIndex, int destinationCellIndex, boolean copyCellStyle, boolean copyComment, boolean copyHyperlink) {
		return copy((EditableCell) getRow().getArea().getCell(destinationRowIndex, destinationCellIndex), copyCellStyle, copyComment, copyHyperlink);
	}

	public EditableCell copy(EditableCell destinationCell, boolean copyCellStyle, boolean copyComment, boolean copyHyperlink) {
		destinationCell.setCellTypes(this.getCellTypes());

		Cell cell = this.getPoiCell();
		if (cell == null) {
			destinationCell.clear();
			return this;
		}
		destinationCell.setValue(this.getValue());
		if (copyCellStyle) {
			destinationCell.getPoiCell().setCellStyle(cell.getCellStyle());
		}
		if (copyComment && cell.getCellComment() != null) {
			destinationCell.getPoiCell().setCellComment(cell.getCellComment());
		}
		if (copyHyperlink && cell.getHyperlink() != null) {
			destinationCell.getPoiCell().setHyperlink(cell.getHyperlink());
		}
		return this;
	}

	public EditableCell delete() {
		//TODO-dchubkov: implement delete ExcelCell and TableCell
		throw new NotImplementedException("Cell deletion is not implemented yet");
	}
}
