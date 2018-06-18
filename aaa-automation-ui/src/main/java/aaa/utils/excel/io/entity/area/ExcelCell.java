package aaa.utils.excel.io.entity.area;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.*;
import aaa.utils.excel.io.entity.Writable;
import toolkit.exceptions.IstfException;

public abstract class ExcelCell implements Writable {
	public static final NumberCellType<Integer> INTEGER_TYPE = new IntegerCellType(Integer.class);
	public static final NumberCellType<Double> DOUBLE_TYPE = new DoubleCellType(Double.class);
	public static final CellType<Boolean> BOOLEAN_TYPE = new BooleanCellType(Boolean.class);
	public static final DateCellType<LocalDate> LOCAL_DATE_TYPE = new LocalDateCellType(LocalDate.class);
	public static final DateCellType<LocalDateTime> LOCAL_DATE_TIME_TYPE = new LocalDateTimeCellType(LocalDateTime.class);
	public static final CellType<String> STRING_TYPE = new StringCellType(String.class);
	public static final CellType<Dollar> DOLLAR_CELL_TYPE = new DollarCellType(Dollar.class);
	
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
		this.allowableCellTypes = allowableCellTypes.stream().distinct().collect(Collectors.toList());
	}
	
	public static synchronized List<CellType<?>> getBaseTypes() {
		if (baseCellTypes == null) {
			baseCellTypes = Arrays.asList(INTEGER_TYPE, DOUBLE_TYPE, BOOLEAN_TYPE, LOCAL_DATE_TYPE, LOCAL_DATE_TIME_TYPE, STRING_TYPE);
		}
		return Collections.unmodifiableList(baseCellTypes);
	}
	
	public Cell getPoiCell() {
		return this.cell;
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
	
	public List<CellType<?>> getCellTypes() {
		if (this.cellTypes == null) {
			this.cellTypes = filterAndGetValidCellTypes(this.allowableCellTypes);
			assertThat(this.cellTypes).as("There are unknown or unsupported cell types in %s", this).isNotEmpty();
		}
		return Collections.unmodifiableList(this.cellTypes);
	}
	
	ExcelCell setCellTypes(List<CellType<?>> allowableCellTypes) {
		this.allowableCellTypes = allowableCellTypes.stream().distinct().collect(Collectors.toList());
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
	
	public ExcelCell setValue(Object value) {
		return setValue(value, getType(value));
	}
	
	protected List<NumberCellType<?>> getNumberCellTypes() {
		List<NumberCellType<?>> numberCellTypes = new ArrayList<>();
		for (CellType<?> cellType : getCellTypes()) {
			if (cellType instanceof NumberCellType) {
				numberCellTypes.add((NumberCellType<?>) cellType);
			}
		}
		return numberCellTypes;
	}
	
	protected int getColumnIndexOnSheet() {
		return this.columnIndexOnSheet;
	}
	
	@Override
	public ExcelManager getExcelManager() {
		return getRow().getExcelManager();
	}
	
	@Override
	public String toString() {
		return "ExcelCell{" +
				"sheetName=" + getSheetName() +
				", rowIndex=" + getRowIndex() +
				", columnIndex=" + getColumnIndex() +
				", value=\"" + getStringValue() + "\"" +
				", cellTypes=" + getCellTypes() +
				'}';
	}
	
	public boolean hasNumber() {
		return !getNumberCellTypes().isEmpty();
	}
	
	public boolean hasDate(DateTimeFormatter... dateTimeFormatters) {
		return getDateCellType(dateTimeFormatters) != null;
	}
	
	public Object getValue(DateTimeFormatter... dateTimeFormatters) {
		// Let's try to obtain date value
		DateCellType<?> dateCellType = getDateCellType(dateTimeFormatters);
		if (dateCellType != null) {
			return getDateValue(dateCellType, dateTimeFormatters);
		}
		
		List<CellType<?>> typesToCheck = new ArrayList<>(getCellTypes());
		
		// Then let's try to obtain numeric value first
		List<NumberCellType<?>> numberCellTypes = getNumberCellTypes();
		for (CellType<?> cellType : numberCellTypes) {
			if (cellType.isTypeOf(this)) {
				return getValue(cellType);
			}
			typesToCheck.removeAll(numberCellTypes);
		}
		
		// If no numeric value has been obtained then let's try to get any non string value
		boolean hasStringType = typesToCheck.remove(STRING_TYPE);
		for (CellType<?> cellType : typesToCheck) {
			if (cellType.isTypeOf(this)) {
				return getValue(cellType);
			}
		}
		
		// Finally let's try to get String value
		if (hasStringType) {
			return getStringValue();
		}
		
		throw new IstfException("There are no valid cell types to retrieve value from " + this);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Temporal> T getDateValue(DateTimeFormatter... dateTimeFormatters) {
		DateCellType<?> dateCellType = getDateCellType(dateTimeFormatters);
		assertThat(dateCellType).as("There are no valid date cell types to retrieve value from %1$s%2$s", this,
				ArrayUtils.isNotEmpty(dateTimeFormatters) ? " using formatters " + Arrays.asList(dateTimeFormatters) : "").isNotNull();
		return (T) getDateValue(dateCellType, dateTimeFormatters);
	}
	
	public <T extends Temporal> T getDateValue(DateCellType<T> dateCellType, DateTimeFormatter... dateTimeFormatters) {
		return dateCellType.getValueFrom(this, dateTimeFormatters);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(CellType<T> cellType, DateTimeFormatter... dateTimeFormatters) {
		if (cellType instanceof DateCellType<?>) {
			return (T) ((DateCellType<?>) cellType).getValueFrom(this, dateTimeFormatters);
		}
		return cellType.getValueFrom(this);
	}
	
	public <T> boolean hasType(CellType<T> cellType) {
		return getCellTypes().contains(cellType);
	}
	
	public boolean hasValue(Object expectedValue, DateTimeFormatter... dateTimeFormatters) {
		return getCellTypes().stream().anyMatch(cType -> Objects.equals(getValue(cType, dateTimeFormatters), expectedValue));
	}
	
	public <T> boolean hasValue(T expectedValue, CellType<T> cellType, DateTimeFormatter... dateTimeFormatters) {
		return Objects.equals(getValue(cellType, dateTimeFormatters), expectedValue);
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
		if (value == null) {
			return null;
		}
		return (CellType<T>) getCellTypes().stream().filter(t -> ClassUtils.isAssignable(t.getEndType(), value.getClass(), true)).findFirst().orElse(null);
	}
	
	public ExcelCell registerCellType(List<CellType<?>> cellTypes) {
		this.allowableCellTypes.addAll(cellTypes);
		return setCellTypes(this.allowableCellTypes);
	}
	
	public <T> ExcelCell setValue(T value, CellType<T> valueType) {
		if (value == null) {
			return clear();
		}
		assertThat(hasType(valueType)).as("%s cell does not have appropriate type to set %s value", this, value).isTrue();
		if (getPoiCell() == null) {
			//TODO-dchubkov: maybe would be better to move this cell creation to "aaa.utils.excel.io.celltype.AbstractCellType.setValueTo" method
			Row row = getRow().getPoiRow();
			if (row == null) {
				row = getRow().getArea().getPoiSheet().createRow(getRowIndexOnSheet() - 1);
				getRow().setPoiRow(row);
			}
			this.cell = row.createCell(getColumnIndexOnSheet() - 1);
		}
		valueType.setValueTo(this, value);
		registerCellType(Collections.singletonList(valueType));
		return this;
	}
	
	public ExcelCell clear() {
		if (!isEmpty()) {
			getRow().getPoiRow().removeCell(getPoiCell());
			this.cell = null;
			setCellTypes(Collections.singletonList(STRING_TYPE));
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
	
	protected DateCellType<?> getDateCellType(DateTimeFormatter... dateTimeFormatters) {
		if (ArrayUtils.isNotEmpty(dateTimeFormatters)) {
			for (CellType<?> type : this.allowableCellTypes) {
				if (type instanceof DateCellType && ((DateCellType<?>) type).isTypeOf(this, dateTimeFormatters)) {
					return (DateCellType<?>) type;
				}
			}
		}
		
		for (CellType<?> type : getCellTypes()) {
			if (type instanceof DateCellType) {
				return (DateCellType<?>) type;
			}
		}
		
		return null;
	}
	
	protected List<CellType<?>> filterAndGetValidCellTypes(List<CellType<?>> cellTypes) {
		List<CellType<?>> filteredCellTypes = new ArrayList<>();
		for (CellType<?> type : cellTypes) {
			if (type.isTypeOf(this)) {
				filteredCellTypes.add(type);
			}
		}
		return filteredCellTypes;
	}
	
	private Cell normalizeCell(Cell cell) {
		//TODO-dchubkov: maybe would be better to move this logic to only needed CellTypes
		if (cell != null && cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.FORMULA) {
			return getExcelManager().getFormulaEvaluator().evaluateInCell(cell);
		}
		return cell;
	}
}
