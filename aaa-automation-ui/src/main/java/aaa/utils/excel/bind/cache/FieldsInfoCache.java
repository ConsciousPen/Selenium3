package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import aaa.utils.excel.io.ExcelManager;

public class FieldsInfoCache {
	private final ExcelManager excelManager;
	private final boolean strictMatch;
	private final Map<Field, TableFieldInfo> tableFieldsInfoMap;
	private final Map<Field, ColumnFieldInfo> columnFieldsInfoMap;

	public FieldsInfoCache(ExcelManager excelManager, boolean strictMatch) {
		this.excelManager = excelManager;
		this.strictMatch = strictMatch;
		this.tableFieldsInfoMap = new HashMap<>();
		this.columnFieldsInfoMap = new HashMap<>();
	}

	public ExcelManager getExcelManager() {
		return this.excelManager;
	}

	public boolean isStrictMatch() {
		return this.strictMatch;
	}

	public TableFieldInfo ofTableField(Field tableField) {
		if (!this.tableFieldsInfoMap.containsKey(tableField)) {
			TableFieldInfo fieldInfo = new TableFieldInfo(tableField, this);
			this.tableFieldsInfoMap.put(tableField, fieldInfo);
			return fieldInfo;
		}
		return this.tableFieldsInfoMap.get(tableField);
	}

	public ColumnFieldInfo ofColumnField(Field tableColumnField) {
		if (!this.columnFieldsInfoMap.containsKey(tableColumnField)) {
			ColumnFieldInfo fieldInfo = new ColumnFieldInfo(tableColumnField, this);
			this.columnFieldsInfoMap.put(tableColumnField, fieldInfo);
			return fieldInfo;
		}
		return this.columnFieldsInfoMap.get(tableColumnField);
	}

	public void flushAll() {
		this.tableFieldsInfoMap.clear();
		this.columnFieldsInfoMap.clear();
	}

	public void flushTableField(Field tableField) {
		this.tableFieldsInfoMap.remove(tableField);
	}

	public void flushColumnField(Field tableColumnField) {
		this.columnFieldsInfoMap.remove(tableColumnField);
	}
}
