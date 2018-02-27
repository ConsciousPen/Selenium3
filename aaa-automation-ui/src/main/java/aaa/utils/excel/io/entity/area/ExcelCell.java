package aaa.utils.excel.io.entity.area;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.BooleanCellType;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.celltype.DoubleCellType;
import aaa.utils.excel.io.celltype.IntegerCellType;
import aaa.utils.excel.io.celltype.LocalDateTimeCellType;
import aaa.utils.excel.io.celltype.NumberCellType;
import aaa.utils.excel.io.celltype.StringCellType;
import aaa.utils.excel.io.entity.Writable;
import toolkit.exceptions.IstfException;

public abstract class ExcelCell implements Writable {
	public static final CellType<Boolean> BOOLEAN_TYPE = new BooleanCellType(Boolean.class);
	public static final CellType<String> STRING_TYPE = new StringCellType(String.class);
	public static final CellType<Integer> INTEGER_TYPE = new IntegerCellType(Integer.class);
	public static final CellType<Double> DOUBLE_TYPE = new DoubleCellType(Double.class);
	public static final CellType<LocalDateTime> LOCAL_DATE_TIME_TYPE = new LocalDateTimeCellType(LocalDateTime.class);

	private ExcelRow<? extends ExcelCell> row;
	private Cell cell;
	private int columnIndexInArea;
	private int columnIndexOnSheet;
	private Set<CellType<?>> cellTypes;

	protected ExcelCell(Cell cell, int columnIndexOnSheet, ExcelRow<? extends ExcelCell> row) {
		this(cell, columnIndexOnSheet, columnIndexOnSheet, row, row.getCellTypes());
	}

	protected ExcelCell(Cell cell, int columnIndexInArea, int columnIndexOnSheet, ExcelRow<? extends ExcelCell> row) {
		this(cell, columnIndexInArea, columnIndexOnSheet, row, row.getCellTypes());
	}

	protected ExcelCell(Cell cell, int columnIndexInArea, int columnIndexOnSheet, ExcelRow<? extends ExcelCell> row, Set<CellType<?>> cellTypes) {
		this.row = row; // should be initialized first!
		this.cell = normalizeCell(cell);
		this.columnIndexInArea = columnIndexInArea;
		this.columnIndexOnSheet = columnIndexOnSheet;
	}

	public static Set<CellType<?>> getBaseTypes() {
		return new HashSet<>(Arrays.asList(BOOLEAN_TYPE, STRING_TYPE, INTEGER_TYPE, DOUBLE_TYPE, LOCAL_DATE_TIME_TYPE));
	}

	public Cell getPoiCell() {
		return this.cell;
	}

	private ExcelCell setPoiCell(Cell cell) {
		this.cell = cell;
		return this;
	}

	public String getSheetName() {
		return getRow().getSheetName();
	}

	public ExcelRow<? extends ExcelCell> getRow() {
		return this.row;
	}

	public ExcelColumn<? extends ExcelCell> getColumn() {
		return getRow().getArea().getColumn(getColumnIndex());
	}

	public int getColumnIndex() {
		return this.columnIndexInArea;
	}

	public Set<CellType<?>> getCellTypes() {
		if (this.cellTypes == null) {
			this.cellTypes = filterAndGetValidCellTypes(getRow().getCellTypes());
			assertThat(cellTypes).as("Cell has unknown or unsupported cell type").isNotEmpty();
		}
		return new HashSet<>(this.cellTypes);
	}

	ExcelCell setCellTypes(Set<CellType<?>> cellTypes) {
		this.cellTypes = new HashSet<>(cellTypes);
		return this;
	}

	public int getRowIndex() {
		return getRow().getIndex();
	}

	public int getRowIndexOnSheet() {
		return getRow().getIndexOnSheet();
	}

	public boolean isEmpty() {
		return getRow().getPoiRow() == null || getPoiCell() == null || StringUtils.isEmpty(getStringValue());
	}

	public Object getValue() {
		// Let's try to obtain numeric value first
		Set<NumberCellType<?>> numericTypes = getCellTypes().stream().filter(NumberCellType.class::isInstance).map(t -> (NumberCellType<?>) t).collect(Collectors.toSet());
		for (NumberCellType<?> type : numericTypes) {
			if (type.isTypeOf(this)) {
				return getValue(type);
			}
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

	public ExcelCell setValue(Object value) {
		return setValue(value, getType(value));
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

	protected int getColumnIndexOnSheet() {
		return this.columnIndexOnSheet;
	}

	@Override
	public ExcelManager getExcelManager() {
		return getRow().getExcelManager();
	}

	public String toString() {
		return "ExcelCell{" +
				"sheetName=" + getSheetName() +
				", rowIndex=" + getRowIndex() +
				", columnIndex=" + getColumnIndex() +
				", cellTypes=" + getCellTypes() +
				", value=" + getStringValue() +
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

	public boolean hasValue(Object expectedValue) {
		return hasValue(expectedValue, getType(expectedValue));
	}

	public <T> boolean hasValue(T expectedValue, CellType<T> cellType) {
		return cellType != null ? Objects.equals(getValue(cellType), expectedValue) : Objects.equals(getValue(), expectedValue);
	}

	public boolean hasStringValue(String expectedValue) {
		return hasStringValue(expectedValue, false);
	}

	public boolean hasStringValue(String expectedValue, boolean ignoreCase) {
		String actualValue = getStringValue();
		if (actualValue == null) {
			return Objects.equals(actualValue, expectedValue);
		}
		return ignoreCase ? actualValue.equalsIgnoreCase(expectedValue) : actualValue.equals(expectedValue);
	}

	@SuppressWarnings("unchecked")
	public <T> CellType<T> getType(T value) {
		return (CellType<T>) getCellTypes().stream().filter(t -> t.getEndType().isAssignableFrom(value.getClass())).findFirst().orElse(null);
	}

	public ExcelCell registerCellType(CellType<?>... cellTypes) {
		Set<CellType<?>> typesCopy = getCellTypes();
		typesCopy.addAll(Arrays.asList(cellTypes));
		this.cellTypes = typesCopy;
		return this;
	}

	public <T> ExcelCell setValue(T value, CellType<T> valueType) {
		assertThat(hasType(valueType)).as("%s cell does not have appropriate type to set %s value type", this, value.getClass()).isTrue();
		if (getPoiCell() == null) {
			Row row = getRow().getPoiRow() == null ? getRow().getArea().getPoiSheet().createRow(getRowIndex() - 1) : getRow().getPoiRow();
			Cell poiCell = row.createCell(getColumnIndex() - 1);
			setPoiCell(poiCell);
		}
		valueType.setValueTo(this, value);
		this.cellTypes = filterAndGetValidCellTypes(getCellTypes());
		return this;
	}

	public ExcelCell clear() {
		if (!isEmpty()) {
			getRow().getPoiRow().removeCell(getPoiCell());
			setPoiCell(null);
			this.cellTypes = Collections.singleton(STRING_TYPE);
		}
		return this;
	}

	public ExcelCell copy(int destinationRowIndex) {
		return copy(destinationRowIndex, getColumnIndex());
	}

	public ExcelCell copy(int destinationRowIndex, int destinationColumnIndex) {
		return copy(destinationRowIndex, destinationColumnIndex, true, true, true);
	}

	public ExcelCell copy(int destinationRowIndex, int destinationColumnIndex, boolean copyCellStyle, boolean copyComment, boolean copyHyperlink) {
		return copy(getRow().getArea().getCell(destinationRowIndex, destinationColumnIndex), copyCellStyle, copyComment, copyHyperlink);
	}

	public ExcelCell copy(ExcelCell destinationCell, boolean copyCellStyle, boolean copyComment, boolean copyHyperlink) {
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

	public abstract ExcelCell delete();

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
