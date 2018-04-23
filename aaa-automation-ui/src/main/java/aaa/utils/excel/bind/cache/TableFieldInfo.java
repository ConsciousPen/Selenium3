package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.io.entity.area.table.TableHeader;

public final class TableFieldInfo {
	private final Field tableField;

	private Boolean isCaseIgnored;
	private Boolean isTableField;
	private Boolean isPrimaryKeyField;
	private String headerColumnName;
	private Integer headerColumnIndex;
	private Class<?> tableClass;

	public TableFieldInfo(Field tableField) {
		this.tableField = tableField;
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
			this.headerColumnName = ColumnFieldHelper.getHeaderColumnName(tableField);
		}
		return this.headerColumnName;
	}

	public int getHeaderColumnIndex(TableHeader header, boolean isCaseIgnoredForAllColumns) {
		if (this.headerColumnIndex == null) {
			this.headerColumnIndex = header.getColumnIndex(getHeaderColumnName(), isCaseIgnoredForAllColumns || isCaseIgnored());
		}
		return this.headerColumnIndex;
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
			this.isPrimaryKeyField = getTableField().isAnnotationPresent(ExcelTableColumnElement.class) && getTableField().getAnnotation(ExcelTableColumnElement.class).isPrimaryKey();
		}
		return this.isPrimaryKeyField;
	}
}
