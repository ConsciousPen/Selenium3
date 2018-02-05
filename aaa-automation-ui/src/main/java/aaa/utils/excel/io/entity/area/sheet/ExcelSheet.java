package aaa.utils.excel.io.entity.area.sheet;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;
import aaa.utils.excel.io.entity.iterator.RowIterator;
import aaa.utils.excel.io.entity.queue.CellsQueue;
import toolkit.exceptions.IstfException;

public class ExcelSheet extends EditableCellsArea implements Iterable<SheetRow> {
	protected static Logger log = LoggerFactory.getLogger(ExcelSheet.class);

	private int sheetIndex;
	private Map<Integer, SheetRow> rows;
	private Map<Integer, SheetColumn> columns;
	private Set<ExcelTable> tables;

	public ExcelSheet(Sheet sheet, int sheetIndex, ExcelManager excelManager) {
		this(sheet, sheetIndex, excelManager, ExcelCell.getBaseTypes());
	}

	public ExcelSheet(Sheet sheet, int sheetIndex, ExcelManager excelManager, Set<CellType<?>> cellTypes) {
		this(sheet, sheetIndex, null, null, excelManager, cellTypes);
	}

	public ExcelSheet(Sheet sheet, int sheetIndex, Set<Integer> columnsIndexes, Set<Integer> rowsIndexes, ExcelManager excelManager, Set<CellType<?>> cellTypes) {
		super(sheet, columnsIndexes, rowsIndexes, excelManager, cellTypes);
		this.sheetIndex = sheetIndex;
		this.tables = new HashSet<>();
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	/**
	 * @return only previously added tables by {@link #addTable(ExcelTable)} or found by {@link #getTable(String...)}, {@link #getTable(boolean, String...)} and {@link #getTable(int, String...)} methods
	 */
	public List<ExcelTable> getTables() {
		return new ArrayList<>(this.tables);
	}

	public String getSheetName() {
		return getPoiSheet().getSheetName();
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, SheetRow> getRowsMap() {
		if (this.rows == null) {
			this.rows = new LinkedHashMap<>(this.rowsIndexes.size());
			for (int rowIndex : this.rowsIndexes) {
				SheetRow row = new SheetRow(getPoiSheet().getRow(rowIndex - 1), rowIndex, this);
				this.rows.put(rowIndex, row);
			}
		}
		return this.rows;
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, SheetColumn> getColumnsMap() {
		if (this.columns == null) {
			this.columns = new LinkedHashMap<>(this.columnsIndexes.size());
			for (Integer columnIndex : this.columnsIndexes) {
				Row row = getPoiSheet().getRow(columnIndex - 1);
				this.columns.put(columnIndex, new SheetColumn(columnIndex, this));
			}
		}
		return this.columns;
	}

	@Override
	public ExcelSheet excludeColumns(Integer... columnsIndexes) {
		for (Integer cIndex : columnsIndexes) {
			for (CellsQueue row : getRows()) {
				((SheetRow) row).getCellsMap().remove(cIndex);
			}
			this.columnsIndexes.remove(cIndex);
		}
		return this;
	}

	@Override
	@Nonnull
	public Iterator<SheetRow> iterator() {
		return new RowIterator<>(getRowsIndexes(), this::getRow);
	}

	/**
	 * Register cell types for next found ExcelTables and ExcelRows and update cell types for found {@link #tables}
	 */
	@Override
	public ExcelSheet registerCellType(CellType<?>... cellTypes) {
		super.registerCellType(cellTypes);
		getTables().forEach(t -> t.registerCellType(cellTypes));
		return this;
	}

	@Override
	public String toString() {
		return "ExcelSheet{" +
				"sheetNumber=" + getSheetIndex() +
				", sheetName=" + getSheetName() +
				", rowsNumber=" + getRowsNumber() +
				", columnsNumber=" + getColumnsNumber() +
				", cellTypes=" + getCellTypes() +
				'}';
	}

	public ExcelSheet addTable(ExcelTable table) {
		this.tables.add(table);
		return this;
	}

	/**
	 * Only columns with unique names from array will be searched. Returns <b>last</b> found ExcelTable
	 */
	public ExcelTable getTable(String... headerColumnNames) {
		return getTable(false, headerColumnNames);
	}

	public ExcelTable getTable(boolean isLowest, String... headerColumnNames) {
		return getTable(getRow(isLowest, headerColumnNames).getIndex(), headerColumnNames);
	}

	/**
	 *  Get ExcelTable object on current sheet with provided {@code headerColumnNames} header columns found in {@code headerRowIndex} row number.
	 *
	 * @param headerRowIndex Table's header row number on current sheet. Index starts from 1
	 * @param headerColumnNames header column names of needed ExcelTable. If array is empty then all columns from {@code headerRowIndex} will be used as column names
	 * @return {@link ExcelTable} object representation of found excel table
	 */
	public ExcelTable getTable(int headerRowIndex, String... headerColumnNames) {
		assertThat(headerRowIndex).as("Header row number should be greater than 0").isPositive();
		SheetRow headerRow = getRow(headerRowIndex);
		assertThat(headerRow.isEmpty()).as("Header row should not be empty").isFalse();
		Set<Integer> columnIndexes = null;

		if (ArrayUtils.isNotEmpty(headerColumnNames)) {
			Set<String> headerColumns = new HashSet<>(Arrays.asList(headerColumnNames));
			Set<String> foundHeaderColumns = new HashSet<>();
			columnIndexes = new HashSet<>();
			for (EditableCell cell : headerRow) {
				String value = cell.getStringValue();
				if (headerColumns.contains(value)) {
					columnIndexes.add(cell.getColumnIndex());
					foundHeaderColumns.add(value);
				}
			}

			foundHeaderColumns.retainAll(headerColumns);
			if (headerColumns.size() != foundHeaderColumns.size()) {
				headerColumns.removeAll(foundHeaderColumns);
				throw new IstfException(String.format("There are missed header columns %1$s in row number %2$s", headerColumns, headerRowIndex));
			}
		}

		ExcelTable t = new ExcelTable(headerRow.getPoiRow(), columnIndexes, this, getCellTypes());
		return addTable(t).getTable(t);
	}

	ExcelTable getTable(ExcelTable table) {
		return this.tables.stream().filter(t -> t.equals(table)).findFirst().orElseThrow(() -> new IstfException("Table element does not exist in internal tables collection"));
	}
}
