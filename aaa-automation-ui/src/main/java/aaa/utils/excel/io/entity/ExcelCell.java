package aaa.utils.excel.io.entity;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
	protected CellType<?>[] allowableCellTypes;
	protected CellType<?>[] cellTypes;

	public ExcelCell(Cell cell) {
		this(cell, getBaseTypes());
	}

	public ExcelCell(Cell cell, CellType<?>... allowableCellTypes) {
		this.cell = normalizeCell(cell);
		this.allowableCellTypes = allowableCellTypes.clone();
	}

	public static CellType<?>[] getBaseTypes() {
		return new CellType<?>[] {BOOLEAN_TYPE, STRING_TYPE, INTEGER_TYPE, LOCAL_DATE_TIME_TYPE};
	}

	public CellType<?>[] getCellTypes() {
		if (cellTypes == null) {
			cellTypes = Arrays.stream(allowableCellTypes).filter(t -> t.isTypeOf(this)).toArray(CellType<?>[]::new);
			assertThat(cellTypes).as("Cell has unknown or unsupported cell type").isNotEmpty();
		}
		return cellTypes.clone();
	}

	public int getColumnNumber() {
		return getPoiCell().getColumnIndex() + 1;
	}

	public int getRowNumber() {
		return getPoiCell().getRowIndex() + 1;
	}

	public Object getValue() {
		Set<CellType<?>> cellTypes = new HashSet<>(Arrays.asList(getCellTypes()));
		if (cellTypes.remove(STRING_TYPE) && cellTypes.isEmpty()) {
			return getStringValue();
		}
		return cellTypes.stream().findFirst().get().getValueFrom(this);
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
				"Sheet name=" + getPoiCell().getSheet().getSheetName() +
				", Row number=" + getRowNumber() +
				", Column number=" + getColumnNumber() +
				", Cell Types=" + Arrays.toString(getCellTypes()) +
				", Cell value=" + getStringValue() +
				'}';
	}

	public <T> T getValue(CellType<T> cellType) {
		assertThat(hasType(cellType)).as("Unable to get value with type %s from cell %s", cellType.getEndType(), this).isTrue();
		return cellType.getValueFrom(this);
	}

	public <T> boolean setValue(T value) {
		return setValue(value, getType(value));
	}

	public <T> boolean setValue(T value, CellType<T> valueType) {
		assertThat(valueType).as("%s cell does not have appropriate type to set %s value type", this, value.getClass()).isNotNull();
		if (hasValue(value, valueType)) {
			log.warn("{} already has \"{}\" value", this, value);
			return false;
		}
		valueType.setValueTo(this, value);
		return true;
	}

	public <T> boolean hasType(CellType<T> cellType) {
		return Arrays.stream(getCellTypes()).anyMatch(t -> t.equals(cellType));
	}

	public <T> boolean hasValue(T expectedValue) {
		return hasValue(expectedValue, getType(expectedValue));
	}

	public <T> boolean hasValue(T expectedValue, CellType<T> cellType) {
		return cellType != null ? Objects.equals(getValue(cellType), expectedValue) : Objects.equals(getValue(), expectedValue);
	}

	@SuppressWarnings("unchecked")
	public <T> CellType<T> getType(T value) {
		return (CellType<T>) Arrays.stream(getCellTypes()).filter(type -> type.getEndType().isAssignableFrom(value.getClass())).findFirst().orElse(null);
	}

	private Cell normalizeCell(Cell cell) {
		if (cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.FORMULA) {
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
			return evaluator.evaluateInCell(cell);
		}
		return cell;
	}
}
