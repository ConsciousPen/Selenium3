package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;

public final class ColumnFieldInfo extends FieldInfo {
	private final boolean isCaseIgnored;
	private final String headerColumnName;

	private Boolean isTableField;
	//private Boolean isPrimaryKeyField;
	private Class<?> fieldType;

	public ColumnFieldInfo(Field columnField, TableClassesCache tableClassesCache) {
		super(columnField, tableClassesCache);
		this.isCaseIgnored = ColumnFieldHelper.isCaseIgnored(columnField);
		this.headerColumnName = ColumnFieldHelper.getHeaderColumnName(getField());
	}

	public boolean isCaseIgnored() {
		return isCaseIgnored;
	}

	public String getHeaderColumnName() {
		return headerColumnName;
	}

	public boolean isTableField() {
		if (this.isTableField == null) {
			this.isTableField = BindHelper.isTableClassField(getField());
		}
		return this.isTableField;
	}

	/*public boolean isPrimaryKeyField() {
		if (this.isPrimaryKeyField == null) {
			this.isPrimaryKeyField = BindHelper.istableField(getField());
		}
		return this.isPrimaryKeyField;
	}*/

	public Class<?> getTableFieldType() {
		if (this.tableFieldType == null) {
			this.tableFieldType = BindHelper.getTableClass(getField());
		}
		return this.tableFieldType;
	}
}
