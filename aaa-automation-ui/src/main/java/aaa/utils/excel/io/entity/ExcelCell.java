package aaa.utils.excel.io.entity;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.celltype.BooleanCellType;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.celltype.IntegerCellType;
import aaa.utils.excel.io.celltype.LocalDateTimeCellType;
import aaa.utils.excel.io.celltype.StringCellType;

public class ExcelCell {
	public static final CellType<Boolean> BOOLEAN_TYPE = new BooleanCellType();
	public static final CellType<String> STRING_TYPE = new StringCellType();
	public static final CellType<Integer> INTEGER_TYPE = new IntegerCellType();
	public static final CellType<LocalDateTime> LOCAL_DATE_TIME_TYPE = new LocalDateTimeCellType();

	protected static Logger log = LoggerFactory.getLogger(ExcelCell.class);
	protected Cell cell;
	protected ExcelRow row;
	protected Set<CellType<?>> cellTypes;

	public ExcelCell(Cell cell, ExcelRow row) {
		this.cell = normalizeCell(cell);
		this.row = row;
	}

	public static Set<CellType<?>> getBaseTypes() {
		return new HashSet<>(Arrays.asList(BOOLEAN_TYPE, STRING_TYPE, INTEGER_TYPE, LOCAL_DATE_TIME_TYPE));
	}

	public ExcelRow getRow() {
		return row;
	}

	public Set<CellType<?>> getCellTypes() {
		if (cellTypes == null) {
			cellTypes = filterAndGetValidCellTypes(getRow().getCellTypes());
			assertThat(cellTypes).as("Cell has unknown or unsupported cell type").isNotEmpty();
		}
		return new HashSet<>(this.cellTypes);
	}

	public int getColumnNumber() {
		return getPoiCell().getColumnIndex() + 1;
	}

	public int getRowNumber() {
		return getRow().getRowNumber();
	}

	public Object getValue() {
		Set<CellType<?>> typesCopy = getCellTypes();
		if (typesCopy.remove(STRING_TYPE) && typesCopy.isEmpty()) {
			return getStringValue();
		}
		return typesCopy.stream().findFirst().get().getValueFrom(this);
	}

	public <T> ExcelCell setValue(T value) {
		return setValue(value, getType(value));
	}

	public Boolean getBoolValue() {
		return getValue(BOOLEAN_TYPE);
	}

	public String getStringValue() {
		return getValue(STRING_TYPE);
	}

	public int getIntValue() {
		return getValue(INTEGER_TYPE);
	}

	public LocalDateTime getDateValue() {
		return getValue(LOCAL_DATE_TIME_TYPE);
	}

	public Cell getPoiCell() {
		return cell;
	}

	@Override
	public String toString() {
		return "ExcelCell{" +
				"Sheet name=" + getRow().getSheet().getSheetName() +
				", Row number=" + getRowNumber() +
				", Column number=" + getColumnNumber() +
				", Cell Types=" + getCellTypes() +
				", Cell value=" + getStringValue() +
				'}';
	}

	@SuppressWarnings("unchecked")
	public <C extends ExcelCell> C registerCellType(CellType<?>... cellTypes) {
		Set<CellType<?>> typesCopy = getCellTypes();
		typesCopy.addAll(Arrays.asList(cellTypes));
		this.cellTypes = typesCopy;
		return (C) this;
	}

	public <T> T getValue(CellType<T> cellType) {
		assertThat(hasType(cellType)).as("Unable to get value with type %s from cell %s", cellType.getEndType(), this).isTrue();
		return cellType.getValueFrom(this);
	}

	public <T> ExcelCell setValue(T value, CellType<T> valueType) {
		assertThat(valueType).as("%s cell does not have appropriate type to set %s value type", this, value.getClass()).isNotNull();
		if (valueType.isTypeOf(this) && hasValue(value, valueType)) {
			log.warn("{} already has \"{}\" value", this, value);
		}
		valueType.setValueTo(this, value);
		return this;
	}

	public <T> boolean hasType(CellType<T> cellType) {
		return getCellTypes().stream().anyMatch(t -> t.equals(cellType));
	}

	public <T> boolean hasValue(T expectedValue) {
		return hasValue(expectedValue, getType(expectedValue));
	}

	public <T> boolean hasValue(T expectedValue, CellType<T> cellType) {
		return cellType != null ? Objects.equals(getValue(cellType), expectedValue) : Objects.equals(getValue(), expectedValue);
	}

	@SuppressWarnings("unchecked")
	public <T> CellType<T> getType(T value) {
		return (CellType<T>) getCellTypes().stream().filter(t -> t.getEndType().isAssignableFrom(value.getClass())).findFirst().orElse(null);
	}

	@SuppressWarnings("unchecked")
	public <C extends ExcelCell> C save() {
		getRow().save();
		return (C) this;
	}

	@SuppressWarnings("unchecked")
	public <C extends ExcelCell> C save(File destinationFile) {
		getRow().save(destinationFile);
		return (C) this;
	}

	@SuppressWarnings("unchecked")
	public <C extends ExcelCell> C close() {
		getRow().close();
		return (C) this;
	}

	@SuppressWarnings("unchecked")
	public <C extends ExcelCell> C saveAndClose() {
		getRow().saveAndClose();
		return (C) this;
	}

	@SuppressWarnings("unchecked")
	public <C extends ExcelCell> C saveAndClose(File destinationFile) {
		getRow().getSheet().getExcelManager().saveAndClose(destinationFile);
		return (C) this;
	}

	protected Set<CellType<?>> filterAndGetValidCellTypes(Set<CellType<?>> cellTypes) {
		return cellTypes.stream().filter(t -> t.isTypeOf(this)).collect(Collectors.toSet());
	}

	private Cell normalizeCell(Cell cell) {
		if (cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.FORMULA) {
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
			return evaluator.evaluateInCell(cell);
		}
		return cell;
	}
}