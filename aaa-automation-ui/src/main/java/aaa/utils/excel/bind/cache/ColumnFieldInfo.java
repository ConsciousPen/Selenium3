package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;

public final class ColumnFieldInfo extends FieldInfo {
	private final boolean isCaseIgnored;
	private final String headerColumnName;

	private Boolean isTableField;
	//private Boolean isPrimaryKeyField;
	private Class<?> tableFieldType;

	public ColumnFieldInfo(Field columnField, FieldsInfoCache fieldsInfoCache) {
		super(columnField, fieldsInfoCache);
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
			this.isTableField = BindHelper.isTableRowField(getField());
		}
		return this.isTableField;
	}

	/*public boolean isPrimaryKeyField() {
		if (this.isPrimaryKeyField == null) {
			this.isPrimaryKeyField = BindHelper.isTableRowField(getField());
		}
		return this.isPrimaryKeyField;
	}*/

	public Class<?> getTableFieldType() {
		if (this.tableFieldType == null) {
			this.tableFieldType = BindHelper.getTableRowType(getField());
		}
		return this.tableFieldType;
	}
}
