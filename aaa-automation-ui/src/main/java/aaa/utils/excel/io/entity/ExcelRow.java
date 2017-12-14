package aaa.utils.excel.io.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import com.atlassian.util.concurrent.NotNull;
import aaa.utils.excel.io.celltype.BaseCellType;
import toolkit.exceptions.IstfException;

public class ExcelRow implements Iterable<ExcelCell<?>> {
	protected Row row;
	protected Set<BaseCellType<?>> cellTypes;
	protected List<ExcelCell<?>> cells;

	public ExcelRow(Row row, Set<BaseCellType<?>> cellTypes) {
		this.row = row;
		this.cellTypes = new HashSet<>(cellTypes);
		this.cells = new ArrayList<>(row.getLastCellNum() + 1);
		for (Cell cell : row) {
			BaseCellType<?> cellType = getCellType(cell, cellTypes);
			cells.add(new ExcelCell(cell, cellType));
		}
	}

	public int getRowNumber() {
		return row.getRowNum() + 1;
	}

	public Set<BaseCellType<?>> getCellTypes() {
		return Collections.unmodifiableSet(cellTypes);
	}

	public List<ExcelCell<?>> getCells() {
		return Collections.unmodifiableList(this.cells);
	}

	public List<Integer> getCellIndexes() {
		return getCells().stream().map(ExcelCell::getCellNumber).sorted().collect(Collectors.toList());
	}

	public int getLastCellNum() {
		List<Integer> cellIndexes = getCellIndexes();
		return cellIndexes.get(cellIndexes.size());
	}

	public int getSize() {
		return cells.size();
	}

	public List<Object> getValues() {
		return getCells().stream().map(ExcelCell::getValue).collect(Collectors.toList());
	}

	public List<String> getStringValues() {
		return getCells().stream().map(ExcelCell::getStringValue).collect(Collectors.toList());
	}

	Row getRow() {
		return row;
	}

	protected static BaseCellType<?> getCellType(Cell cell, Set<BaseCellType<?>> cellTypes) {
		for (BaseCellType<?> cellType : cellTypes) {
			if (cellType.isTypeOf(cell)) {
				return cellType;
			}
		}
		throw new IstfException(String.format("Unable to get value for cell located in \"%s\". Unknown cell type", getLocation(cell)));
	}

	//TODO-dchubkov: find better place for this method or get rid of it
	static String getLocation(Cell cell) {
		return String.format("sheet name: \"%1$s\", row number: %2$s, column number: %3$s", cell.getSheet().getSheetName(), cell.getRowIndex() + 1, cell.getColumnIndex() + 1);
	}

	@NotNull
	@Override
	public
	Iterator<ExcelCell<?>> iterator() {
		return new CellIterator(getCellIndexes(), this);
	}

	public ExcelCell<?> getCell(int cellNumber) {
		return getCells().stream().filter(c -> c.getCellNumber() == cellNumber).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no cell with %s column index", cellNumber)));
	}

	public String getStringValue(int cellNumber) {
		return getCell(cellNumber).getStringValue();
	}

	public boolean getBoolValue(int cellNumber) {
		return getCell(cellNumber).getBoolValue();
	}

	public int getIntValue(int cellNumber) {
		return getCell(cellNumber).getIntValue();
	}

	public LocalDateTime getDateValue(int cellNumber) {
		return getCell(cellNumber).getDateValue();
	}

	public boolean hasValue(int cellNumber, Object expectedValue) {
		return Objects.equals(getCell(cellNumber).getValue(), expectedValue);
	}
}
