package aaa.utils.excel.io.entity.area;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.ss.usermodel.Sheet;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.queue.EditableColumn;
import aaa.utils.excel.io.entity.queue.EditableRow;

public abstract class EditableCellsArea<E extends EditableCell, R extends EditableRow<E>, C extends EditableColumn<E>> extends CellsArea<E, R, C> {
	protected EditableCellsArea(Sheet sheet, Set<Integer> columnsIndexes, Set<Integer> rowsIndexes, ExcelManager excelManager, Set<CellType<?>> cellTypes) {
		super(sheet, columnsIndexes, rowsIndexes, excelManager, cellTypes);
	}

	public EditableCellsArea<E, R, C> registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getRows().forEach(r -> r.registerCellType(cellTypes));
		return this;
	}

	public abstract EditableCellsArea<E, R, C> excludeColumns(Integer... columnsIndexes);

	public EditableCellsArea<E, R, C> excludeRows(Integer... rowsIndexes) {
		for (Integer rIndex : rowsIndexes) {
			assertThat(hasRow(rIndex)).as("There is no row number %s", rIndex).isTrue();
			getRowsMap().remove(rIndex);
		}
		return this;
	}

	public EditableCellsArea<E, R, C> clearColumns(Integer... columnsIndexes) {
		for (R row : this) {
			for (Integer index : columnsIndexes) {
				row.getCell(index).clear();
			}
		}
		return this;
	}

	public EditableCellsArea<E, R, C> clearRows(Integer... rowsIndexes) {
		for (Integer index : rowsIndexes) {
			getRow(index).clear();
		}
		return this;
	}

	public EditableCellsArea<E, R, C> copyColumn(int columnIndex, int destinationColumnIndex) {
		for (R row : this) {
			row.getCell(columnIndex).copy(row.getIndex(), row.getCell(destinationColumnIndex).getColumnIndex());
		}
		return this;
	}

	public EditableCellsArea<E, R, C> copyRow(int rowIndex, int destinationRowIndex) {
		getRow(rowIndex).copy(destinationRowIndex);
		return this;
	}

	public EditableCellsArea<E, R, C> deleteColumns(Integer... columnsIndexes) {
		//TODO-dchubkov: implement delete columns
		throw new NotImplementedException("Columns deletion is not implemented yet");
	}

	public EditableCellsArea<E, R, C> deleteRows(Integer... rowsIndexes) {
		int rowsShifts = 0;
		Set<Integer> uniqueSortedRowIndexes = Arrays.stream(rowsIndexes).sorted().collect(Collectors.toSet());
		Sheet sheet = getPoiSheet();
		for (int index : uniqueSortedRowIndexes) {
			assertThat(hasRow(index - rowsShifts)).as("There is no row number %1$s on sheet %2$s", index, sheet.getSheetName()).isTrue();
			sheet.shiftRows(index - rowsShifts, sheet.getLastRowNum(), -1);
			rowsShifts++;
		}
		return this;
	}
}
