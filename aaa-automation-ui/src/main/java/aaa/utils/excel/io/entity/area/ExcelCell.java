package aaa.utils.excel.io.entity.area;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.*;
import aaa.utils.excel.io.entity.Writable;
import toolkit.exceptions.IstfException;

public abstract class ExcelCell implements Writable {
	public static final CellType<Boolean> BOOLEAN_TYPE = new BooleanCellType(Boolean.class);
	public static final CellType<String> STRING_TYPE = new StringCellType(String.class);
	public static final CellType<Integer> INTEGER_TYPE = new IntegerCellType(Integer.class);
	public static final CellType<Double> DOUBLE_TYPE = new DoubleCellType(Double.class);
	public static final CellType<LocalDateTime> LOCAL_DATE_TIME_TYPE = new LocalDateTimeCellType(LocalDateTime.class);

	private static List<CellType<?>> baseCellTypes;

	private final ExcelRow<? extends ExcelCell> row;
	private final int columnIndexInArea;
	private final int columnIndexOnSheet;

	private List<CellType<?>> allowableCellTypes;
	private List<CellType<?>> cellTypes;
	private Cell cell;

	protected ExcelCell(Cell cell, int columnIndexOnSheet, ExcelRow<? extends ExcelCell> row) {
		this(cell, columnIndexOnSheet, columnIndexOnSheet, row, row.getCellTypes());
	}

	protected ExcelCell(Cell cell, int columnIndexInArea, int columnIndexOnSheet, ExcelRow<? extends ExcelCell> row) {
		this(cell, columnIndexInArea, columnIndexOnSheet, row, row.getCellTypes());
	}

	protected ExcelCell(Cell cell, int columnIndexInArea, int columnIndexOnSheet, ExcelRow<? extends ExcelCell> row, List<CellType<?>> allowableCellTypes) {
		this.row = row; // should be initialized first!
		this.cell = normalizeCell(cell);
		this.columnIndexInArea = columnIndexInArea;
		this.columnIndexOnSheet = columnIndexOnSheet;
		this.allowableCellTypes = ImmutableList.copyOf(new HashSet<>(allowableCellTypes));
	}

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public static synchronized List<CellType<?>> getBaseTypes() {
		if (baseCellTypes == null) {
			baseCellTypes = Stream.of(INTEGER_TYPE, DOUBLE_TYPE, BOOLEAN_TYPE, LOCAL_DATE_TIME_TYPE, STRING_TYPE).collect(ImmutableList.toImmutableList());
		}
		return Collections.unmodifiableList(baseCellTypes);
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

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<CellType<?>> getCellTypes() {
		if (this.cellTypes == null) {
			this.cellTypes = filterAndGetValidCellTypes(this.allowableCellTypes);
			assertThat(this.cellTypes).as("Cell has unknown or unsupported cell type").isNotEmpty();
		}
		return Collections.unmodifiableList(this.cellTypes);
	}

	ExcelCell setCellTypes(List<CellType<?>> allowableCellTypes) {
		this.allowableCellTypes = ImmutableList.copyOf(allowableCellTypes);
		this.cellTypes = null;
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
		// Let's try to obtain numeric value first (non-floating types are checked first)
		for (CellType<?> type : getCellTypes()) {
			if (type instanceof NumberCellType && type.isTypeOf(this)) {
				return getValue(type);
			}
		}

		// If no numeric value has been obtained then let's try to get non string value
		for (CellType<?> type : getCellTypes()) {
			if (!type.equals(STRING_TYPE) && !(type instanceof NumberCellType) && type.isTypeOf(this)) {
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

	public LocalDateTime getDateValue() {
		return LOCAL_DATE_TIME_TYPE.getValueFrom(this);
	}

	public LocalDateTime getDateValue(List<DateTimeFormatter> dateTimeFormatters) {
		return ((LocalDateTimeCellType) LOCAL_DATE_TIME_TYPE).getValueFrom(this, dateTimeFormatters);
	}

	public boolean isDate(List<DateTimeFormatter> dateTimeFormatters) {
		return ((LocalDateTimeCellType) LOCAL_DATE_TIME_TYPE).isTypeOf(this, dateTimeFormatters);
	}

	public <T> T getValue(CellType<T> cellType) {
		//assertThat(hasType(cellType)).as("Unable to get value with type %s from cell %s", cellType.getEndType(), this).isTrue();
		return cellType.getValueFrom(this);
	}

	public <T> boolean hasType(CellType<T> cellType) {
		return getCellTypes().contains(cellType);
	}

	public boolean hasValue(Object expectedValue) {
		return hasValue(expectedValue, getType(expectedValue));
	}

	public <T> boolean hasValue(T expectedValue, CellType<T> cellType, DateTimeFormatter... dateTimeFormatters) {
		if (isDate(dateTimeFormatters)) {
			return Objects.equals(getDateValue(dateTimeFormatters), expectedValue);
		}
		//return getCellTypes().stream().anyMatch(cType -> Objects.equals(getValue(cType), expectedValue));
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
		List<CellType<?>> allowableCellTypes = ImmutableSet.<CellType<?>>builder().addAll(this.allowableCellTypes).add(cellTypes).build().asList();
		return setCellTypes(allowableCellTypes);
	}

	public <T> ExcelCell setValue(T value, CellType<T> valueType) {
		assertThat(hasType(valueType)).as("%s cell does not have appropriate type to set %s value type", this, value.getClass()).isTrue();
		if (getPoiCell() == null) {
			Row row = getRow().getPoiRow() == null ? getRow().getArea().getPoiSheet().createRow(getRowIndex() - 1) : getRow().getPoiRow();
			Cell poiCell = row.createCell(getColumnIndex() - 1);
			setPoiCell(poiCell);
		}
		registerCellType(valueType);
		valueType.setValueTo(this, value);
		return this;
	}

	public ExcelCell clear() {
		if (!isEmpty()) {
			getRow().getPoiRow().removeCell(getPoiCell());
			setPoiCell(null);
			setCellTypes(ImmutableList.of(STRING_TYPE));
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

	protected ImmutableList<CellType<?>> filterAndGetValidCellTypes(List<CellType<?>> cellTypes) {
		return cellTypes.stream().filter(t -> t.isTypeOf(this)).collect(collectingAndThen(toSet(), ImmutableList::copyOf));
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
