package aaa.utils.excel.io.entity.queue;

import java.util.Arrays;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.cell.EditableCell;

public abstract class EditableCellsQueue extends CellsQueue {

	protected EditableCellsQueue(int index, EditableCellsArea cellsArea) {
		super(index, cellsArea);
	}

	public EditableCellsQueue registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getCells().forEach(c -> ((EditableCell) c).registerCellType(cellTypes));
		return this;
	}

	public abstract EditableCellsQueue exclude();

	public EditableCellsQueue clear() {
		getCells().forEach(c -> ((EditableCell) c).clear());
		return this;
	}

	public abstract EditableCellsQueue copy(int destinationQueueIndex);

	public abstract EditableCellsArea delete();
}
