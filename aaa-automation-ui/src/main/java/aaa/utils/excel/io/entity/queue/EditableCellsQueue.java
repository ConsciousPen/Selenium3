/*
package aaa.utils.excel.io.entity.queue;

import java.util.Arrays;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.cell.EditableCell;

public abstract class EditableCellsQueue<C extends EditableCell> extends CellsQueue<C> {
	protected EditableCellsQueue(int index, ExcelManager excelManager) {
		super(index, excelManager);
	}

	*/
/*@Override
	//@SuppressWarnings("unchecked")
	public EditableCellsArea<C, EditableCellsQueue<C>, EditableCellsQueue<C>> getArea() {
		return (EditableCellsArea<C, EditableCellsQueue<C>, EditableCellsQueue<C>>) cellsArea;
	}*//*


	public EditableCellsQueue<C> registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getCells().forEach(c -> c.registerCellType(cellTypes));
		return this;
	}

	public abstract EditableCellsArea<C, ?, ?> exclude();

	public abstract EditableCellsArea<C, ?, ?> delete();

	public EditableCellsQueue<C> clear() {
		getCells().forEach(EditableCell::clear);
		return this;
	}

	public abstract EditableCellsQueue<C> copy(int destinationQueueIndex);

}
*/
