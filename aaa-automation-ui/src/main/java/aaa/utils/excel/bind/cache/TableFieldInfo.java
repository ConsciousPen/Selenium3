package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.io.entity.area.table.TableCell;
import aaa.utils.excel.io.entity.area.table.TableHeader;

public final class TableFieldInfo {
	private final Field tableField;
	private final List<Integer> headerColumnsIndexes;

	private Boolean isCaseIgnored;
	private Boolean isMultyColumnsField;
	private Boolean isTableField;
	private Boolean isPrimaryKeyField;
	private Boolean hasHeaderColumnNamePattern;
	private String headerColumnName;
	private String headerColumnNamePattern;
	private Class<?> tableClass;

	public TableFieldInfo(Field tableField) {
		this.tableField = tableField;
		this.headerColumnsIndexes = new ArrayList<>();
	}

	public Field getTableField() {
		return tableField;
	}

	public boolean isCaseIgnored() {
		if (this.isCaseIgnored == null) {
			this.isCaseIgnored = ColumnFieldHelper.isCaseIgnored(tableField);
		}
		return this.isCaseIgnored;
	}

	public String getHeaderColumnName() {
		if (this.headerColumnName == null) {
			this.headerColumnName = hasHeaderColumnNamePattern() ? ColumnFieldHelper.getHeaderColumnNamePattern(tableField) : ColumnFieldHelper.getHeaderColumnName(tableField);
		}
		return this.headerColumnName;
	}

	public boolean isMultyColumnsField() {
		if (this.isMultyColumnsField == null) {
			this.isMultyColumnsField = List.class.equals(tableField.getType()) && !isTableField();
		}
		return this.isMultyColumnsField;
	}

	public boolean isTableField() {
		if (this.isTableField == null) {
			this.isTableField = BindHelper.isTableClassField(getTableField());
		}
		return this.isTableField;
	}

	public Class<?> getTableClass() {
		if (this.tableClass == null) {
			this.tableClass = BindHelper.getTableClass(getTableField());
		}
		return this.tableClass;
	}

	public boolean isPrimaryKeyField() {
		if (this.isPrimaryKeyField == null) {
			this.isPrimaryKeyField = getTableField().isAnnotationPresent(ExcelColumnElement.class) && getTableField().getAnnotation(ExcelColumnElement.class).isPrimaryKey();
		}
		return this.isPrimaryKeyField;
	}

	/*public boolean hasHeaderColumnIndexesListByContainsPattern() {
		return hasHeaderColumnNamePattern() && List.class.equals(tableField.getType());
	}*/

	public boolean hasHeaderColumnNamePattern() {
		if (this.hasHeaderColumnNamePattern == null) {
			this.hasHeaderColumnNamePattern = ColumnFieldHelper.hasHeaderColumnNamePattern(tableField);
		}
		return this.hasHeaderColumnNamePattern;
	}

	public int getHeaderColumnIndex(TableHeader header, boolean isCaseIgnoredForAllColumns) {
		return getHeaderColumnsIndexes(header, isCaseIgnoredForAllColumns).get(0);
	}

	public List<Integer> getHeaderColumnsIndexes(TableHeader header, boolean isCaseIgnoredForAllColumns) {
		if (this.headerColumnsIndexes.isEmpty()) {
			boolean ignoreCase = isCaseIgnoredForAllColumns || isCaseIgnored();
			boolean isColumnFound = false;

			for (TableCell cell : header.getCells()) {
				String tableColumnName = cell.getStringValue();
				if (hasHeaderColumnNamePattern()) {
					if (ignoreCase ? tableColumnName.toLowerCase().contains(getHeaderColumnName().toLowerCase()) : tableColumnName.contains(getHeaderColumnName())) {
						isColumnFound = true;
					}
				} else {
					if (ignoreCase ? tableColumnName.toLowerCase().equals(getHeaderColumnName().toLowerCase()) : tableColumnName.equals(getHeaderColumnName())) {
						isColumnFound = true;
					}
				}

				if (isColumnFound) {
					headerColumnsIndexes.add(cell.getColumnIndex());
					isColumnFound = false;
					if (!isMultyColumnsField()) {
						break;
					}
				}
			}
			/*assertThat(this.headerColumnsIndexes).as("There are no header column names which %1$s: \"%2$s\"%3$s in %4$s",
					hasHeaderColumnNamePattern() ? "contains pattern" : "equals to", getHeaderColumnName(), ignoreCase ? " with ignored case" : "", header).isNotEmpty();*/
		}

		return Collections.unmodifiableList(this.headerColumnsIndexes);
	}
}
