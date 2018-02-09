package aaa.utils.excel.io.entity.area.sheet;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class SheetCell extends ExcelCell {
	protected static Logger log = LoggerFactory.getLogger(SheetCell.class);

	public SheetCell(Cell cell, SheetRow row, int columnIndex) {
		super(cell, row, columnIndex);
	}

	public ExcelSheet getSheet() {
		return getRow().getSheet();
	}

	/*@Override
	protected ExcelSheet getArea() {
		return getSheet();
	}*/

	@Override
	public SheetRow getRow() {
		return (SheetRow) super.getRow();
	}

	/*public <T> SheetCell setValue(T value) {
		return setValue(value, getType(value));
	}*/

	/*public SheetCell setPoiCell(Cell cell) {
		this.cell = cell;
		return this;
	}*/

	/*SheetCell setCellTypes(Set<CellType<?>> cellTypes) {
		this.cellTypes = new HashSet<>(cellTypes);
		return this;
	}*/

	/*public SheetCell registerCellType(CellType<?>... cellTypes) {
		Set<CellType<?>> typesCopy = getCellTypes();
		typesCopy.addAll(Arrays.asList(cellTypes));
		this.cellTypes = typesCopy;
		return this;
	}*/

	/*public <T> SheetCell setValue(T value, CellType<T> valueType) {
		assertThat(valueType).as("%s cell does not have appropriate type to set %s value type", this, value.getClass()).isNotNull();
		valueType.setValueTo(this, value);
		return this;
	}*/

	/*public EditableCell excludeColumn() {
		getRow().getArea().excludeColumns(getColumnIndex());
		return this;
	}*/

	/*public SheetCell clear() {
		if (!isEmpty()) {
			getRow().getPoiRow().removeCell(getPoiCell());
			setPoiCell(null);
		}
		return this;
	}*/

	/*@Override
	public SheetCell excludeColumn() {
		getSheet().excludeColumns(getColumnIndex());
		return this;
	}*/

	/*@Override
	public SheetCell copy(int destinationRowIndex) {
		return copy(destinationRowIndex, getColumnIndex());
	}

	@Override
	public SheetCell copy(int destinationRowIndex, int destinationCellIndex) {
		return copy(destinationRowIndex, destinationCellIndex, true, true, true);
	}

	@Override
	public SheetCell copy(int destinationRowIndex, int destinationCellIndex, boolean copyCellStyle, boolean copyComment, boolean copyHyperlink) {
		return (SheetCell) copy(getRow().getSheet().getCell(destinationRowIndex, destinationCellIndex), copyCellStyle, copyComment, copyHyperlink);
	}*/

	@Override
	public SheetCell delete() {
		//TODO-dchubkov: implement delete ExcelCell and TableCell
		throw new NotImplementedException("Cell deletion is not implemented yet");
	}

	/*@Override
	public SheetCell copy(EditableCell<SheetRow> destinationCell, boolean copyCellStyle, boolean copyComment, boolean copyHyperlink) {
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
	}*/
}
