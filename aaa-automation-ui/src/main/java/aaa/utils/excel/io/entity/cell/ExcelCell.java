package aaa.utils.excel.io.entity.cell;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.BooleanCellType;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.celltype.DoubleCellType;
import aaa.utils.excel.io.celltype.IntegerCellType;
import aaa.utils.excel.io.celltype.LocalDateTimeCellType;
import aaa.utils.excel.io.celltype.NumberCellType;
import aaa.utils.excel.io.celltype.StringCellType;
import aaa.utils.excel.io.entity.Writable;
import aaa.utils.excel.io.entity.queue.ExcelRow;
import toolkit.exceptions.IstfException;

public class ExcelCell implements Writable {
	public static final CellType<Boolean> BOOLEAN_TYPE = new BooleanCellType(Boolean.class);
	public static final CellType<String> STRING_TYPE = new StringCellType(String.class);
	public static final CellType<Integer> INTEGER_TYPE = new IntegerCellType(Integer.class);
	public static final CellType<Double> DOUBLE_TYPE = new DoubleCellType(Double.class);
	public static final CellType<LocalDateTime> LOCAL_DATE_TIME_TYPE = new LocalDateTimeCellType(LocalDateTime.class);

	protected ExcelRow<?> row;
	protected Cell cell;
	protected int columnIndex;
	protected Set<CellType<?>> cellTypes;

	protected ExcelCell(Cell cell, ExcelRow<?> row, int columnIndex) {
		this(cell, row, columnIndex, row.getCellTypes());
	}

	protected ExcelCell(Cell cell, ExcelRow<?> row, int columnIndex, Set<CellType<?>> cellTypes) {
		this.row = row;
		this.cell = normalizeCell(cell);
		this.columnIndex = columnIndex;
	}

	public static Set<CellType<?>> getBaseTypes() {
		return new HashSet<>(Arrays.asList(BOOLEAN_TYPE, STRING_TYPE, INTEGER_TYPE, DOUBLE_TYPE, LOCAL_DATE_TIME_TYPE));
	}

	public Cell getPoiCell() {
		return cell;
	}

	public ExcelRow<?> getRow() {
		return row;
	}

	public Set<CellType<?>> getCellTypes() {
		if (cellTypes == null) {
			cellTypes = filterAndGetValidCellTypes(getRow().getCellTypes());
			assertThat(cellTypes).as("Cell has unknown or unsupported cell type").isNotEmpty();
		}
		return new HashSet<>(this.cellTypes);
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public int getRowIndex() {
		return getRow().getIndex();
	}

	public boolean isEmpty() {
		return getRow().getPoiRow() == null || getPoiCell() == null || StringUtils.isEmpty(getStringValue());
	}

	public Object getValue() {
		Set<NumberCellType<?>> numericTypes = getCellTypes().stream().filter(NumberCellType.class::isInstance).map(t -> (NumberCellType<?>) t).collect(Collectors.toSet());

		// Let's try to obtain float number value first
		for (NumberCellType<?> type : numericTypes) {
			if (type.isTypeOf(this) && type.hasFloatValue(this)) {
				return getValue(type);
			}
		}

		// Then if no float numbers has found let's try to find integer value
		if (hasType(INTEGER_TYPE)) {
			return getIntValue();
		}

		// If no numeric value has been obtained then let's try to get non string value
		Set<CellType<?>> nonNumericAndStringCellTypes = getCellTypes();
		nonNumericAndStringCellTypes.removeAll(numericTypes);
		nonNumericAndStringCellTypes.remove(STRING_TYPE);
		for (CellType<?> type : nonNumericAndStringCellTypes) {
			if (type.isTypeOf(this)) {
				return getValue(type);
			}
		}

		// Finally let's try to get String value
		if (hasType(STRING_TYPE)) {
			return getStringValue();
		}

		throw new IstfException("Cell does not have supported types to retrieve its value");
	}

	public String getStringValue() {
		return getValue(STRING_TYPE);
	}

	public Boolean getBoolValue() {
		return getValue(BOOLEAN_TYPE);
	}

	public Integer getIntValue() {
		return getValue(INTEGER_TYPE);
	}

	public Double getDoubleValue() {
		return getValue(DOUBLE_TYPE);
	}

	public boolean isNumeric() {
		return getCellTypes().stream().anyMatch(t -> t instanceof NumberCellType);
	}

	@Override
	public ExcelManager getExcelManager() {
		return getRow().getExcelManager();
	}

	public String toString() {
		return "ExcelCell{" +
				"Sheet name=" + getRow().getPoiRow().getSheet() +
				", Row number=" + getRowIndex() +
				", Column number=" + getColumnIndex() +
				", Cell Types=" + getCellTypes() +
				", Cell value=" + getStringValue() +
				'}';
	}

	public LocalDateTime getDateValue(DateTimeFormatter... formatters) {
		return ((LocalDateTimeCellType) LOCAL_DATE_TIME_TYPE).getValueFrom(this, formatters);
	}

	public boolean isDate(DateTimeFormatter... formatters) {
		return ((LocalDateTimeCellType) LOCAL_DATE_TIME_TYPE).isTypeOf(this, formatters);
	}

	public <T> T getValue(CellType<T> cellType) {
		assertThat(hasType(cellType)).as("Unable to get value with type %s from cell %s", cellType.getEndType(), this).isTrue();
		return cellType.getValueFrom(this);
	}

	public <T> boolean hasType(CellType<T> cellType) {
		return getCellTypes().contains(cellType);
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

	protected Set<CellType<?>> filterAndGetValidCellTypes(Set<CellType<?>> cellTypes) {
		return cellTypes.stream().filter(t -> t.isTypeOf(this)).collect(Collectors.toSet());
	}

	@SuppressWarnings("resource")
	private Cell normalizeCell(Cell cell) {
		if (cell != null && cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.FORMULA) {
			FormulaEvaluator evaluator = getExcelManager().getWorkbook().getCreationHelper().createFormulaEvaluator();
			return evaluator.evaluateInCell(cell);
		}
		return cell;
	}
}
