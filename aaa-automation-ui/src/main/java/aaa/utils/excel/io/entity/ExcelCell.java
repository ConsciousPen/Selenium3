package aaa.utils.excel.io.entity;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.celltype.CellTypes;

public class ExcelCell {
	private Cell cell;
	private Set<CellType<?>> allowableCellTypes;
	private Set<CellType<?>> cellTypes;

	public ExcelCell(Cell cell, Set<CellType<?>> allowableCellTypes) {
		this.cell = normalizeCell(cell);
		this.allowableCellTypes = new HashSet<>(allowableCellTypes);
	}

	public Set<CellType<?>> getCellTypes() {
		if (cellTypes == null) {
			cellTypes = allowableCellTypes.stream().filter(t -> t.isTypeOf(this)).collect(Collectors.toSet());
			assertThat(cellTypes).as("Cell has unknown cell type").isNotEmpty();
		}
		return Collections.unmodifiableSet(cellTypes);
	}

	public int getColumnNumber() {
		return getPoiCell().getColumnIndex() + 1;
	}

	public int getRowNumber() {
		return getPoiCell().getRowIndex() + 1;
	}

	public Object getValue() {
		Set<CellType<?>> cellTypes = new HashSet<>(getCellTypes());
		if (cellTypes.remove(CellTypes.STRING.get()) && cellTypes.isEmpty()) {
			return getStringValue();
		}
		return cellTypes.stream().findFirst().get().getValueFrom(this);
	}

	public Boolean getBoolValue() {
		return (Boolean) getValue(CellTypes.BOOLEAN.get());
	}

	public String getStringValue() {
		return (String) getValue(CellTypes.STRING.get());
	}

	public int getIntValue() {
		return (Integer) getValue(CellTypes.INTEGER.get());
	}

	public LocalDateTime getDateValue() {
		return (LocalDateTime) getValue(CellTypes.LOCAL_DATE_TIME.get());
	}

	public Cell getPoiCell() {
		return cell;
	}

	@Override
	public String toString() {
		return "ExcelCell{" +
				"Sheet name=" + getPoiCell().getSheet().getSheetName() +
				", Row number=" + getRowNumber() +
				", Column number=" + getColumnNumber() +
				", Cell Types=" + getCellTypes() +
				", Cell value=" + getStringValue() +
				'}';
	}

	public <T> boolean hasType(CellType<T> cellType) {
		return getCellTypes().contains(cellType);
	}

	public <T> T getValue(CellType<T> cellType) {
		assertThat(hasType(cellType)).as("Unable to get value with type %s from cell %s", cellType.getEndType(), this);
		return cellType.getValueFrom(this);
	}

	public boolean hasValue(Object expectedValue) {
		for (CellType<?> actualCellType : getCellTypes()) {
			if (actualCellType.getEndType().isAssignableFrom(expectedValue.getClass())) {
				return Objects.equals(getValue(actualCellType), expectedValue);
			}
		}
		return Objects.equals(getValue(), expectedValue);
	}

	private Cell normalizeCell(Cell cell) {
		if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
			return evaluator.evaluateInCell(cell);
		}
		return cell;
	}
}
