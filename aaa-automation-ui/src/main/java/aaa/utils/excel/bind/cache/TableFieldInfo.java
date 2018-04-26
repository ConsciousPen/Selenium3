package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import aaa.utils.excel.bind.BindHelper;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.io.entity.area.table.TableCell;
import aaa.utils.excel.io.entity.area.table.TableHeader;

public final class TableFieldInfo {
	private final Field tableField;
	private final List<Integer> headerColumnsIndexes;

	private Boolean isCaseIgnored;
	private Boolean isPrimaryKeyField;
	private Boolean hasHeaderColumnNamePattern;
	private String primaryKeysSeparator;
	private String headerColumnName;
	private Class<?> tableClass;
	private BindType bindType;

	public TableFieldInfo(Field tableField) {
		this.tableField = tableField;
		this.headerColumnsIndexes = new ArrayList<>();
	}

	public Field getTableField() {
		return tableField;
	}

	public boolean isCaseIgnored() {
		if (this.isCaseIgnored == null) {
			if (tableField.isAnnotationPresent(ExcelColumnElement.class)) {
				this.isCaseIgnored = tableField.getAnnotation(ExcelColumnElement.class).ignoreCase();
			}
			return ExcelColumnElement.DEFAULT_CASE_IGNORED;
		}
		return this.isCaseIgnored;
	}

	public boolean isPrimaryKeyField() {
		if (this.isPrimaryKeyField == null) {
			this.isPrimaryKeyField = getTableField().isAnnotationPresent(ExcelColumnElement.class) && getTableField().getAnnotation(ExcelColumnElement.class).isPrimaryKey();
		}
		return this.isPrimaryKeyField;
	}

	public String getPrimaryKeySeparator() {
		if (this.primaryKeysSeparator == null) {
			if (tableField.isAnnotationPresent(ExcelColumnElement.class)) {
				this.primaryKeysSeparator = tableField.getAnnotation(ExcelColumnElement.class).primaryKeysSeparator();
			} else {
				this.primaryKeysSeparator = ExcelColumnElement.DEFAULT_PRIMARY_KEY_SEPARATOR;
			}
		}
		return this.primaryKeysSeparator;
	}

	public String getHeaderColumnName() {
		if (this.headerColumnName == null) {
			this.headerColumnName = hasHeaderColumnNamePattern() ? getHeaderColumnNamePattern(tableField) : getHeaderColumnName(tableField);
		}
		return this.headerColumnName;
	}

	public Class<?> getTableClass() {
		if (this.tableClass == null) {
			this.tableClass = BindHelper.getTableClass(getTableField());
		}
		return this.tableClass;
	}

	public BindType getBindType() {
		if (this.bindType == null) {
			if (BindHelper.isTableClassField(getTableField())) {
				this.bindType = BindType.TABLE;
			} else if (List.class.equals(tableField.getType())) {
				this.bindType = BindType.MULTY_COLUMNS;
			} else {
				this.bindType = BindType.REGULAR;
			}
		}
		return this.bindType;
	}

	public boolean hasHeaderColumnNamePattern() {
		if (this.hasHeaderColumnNamePattern == null) {
			this.hasHeaderColumnNamePattern = tableField.isAnnotationPresent(ExcelColumnElement.class)
					&& !Objects.equals(tableField.getAnnotation(ExcelColumnElement.class).containsName(), ExcelColumnElement.DEFAULT_COLUMN_NAME);
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
					if (!getBindType().equals(BindType.MULTY_COLUMNS)) {
						break;
					}
				}
			}
		}

		return Collections.unmodifiableList(this.headerColumnsIndexes);
	}

	private String getHeaderColumnNamePattern(Field field) {
		return field.getAnnotation(ExcelColumnElement.class).containsName();
	}

	private String getHeaderColumnName(Field field) {
		if (field.isAnnotationPresent(ExcelColumnElement.class) && !field.getAnnotation(ExcelColumnElement.class).name().equals(ExcelColumnElement.DEFAULT_COLUMN_NAME)) {
			return field.getAnnotation(ExcelColumnElement.class).name();
		}
		return field.getName();
	}

	public enum BindType {
		REGULAR,
		MULTY_COLUMNS,
		TABLE
	}
}
