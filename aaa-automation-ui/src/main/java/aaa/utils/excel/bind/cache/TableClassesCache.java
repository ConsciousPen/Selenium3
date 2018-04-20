package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.io.ExcelManager;

public class TableClassesCache {
	private final ExcelManager excelManager;
	private final boolean strictMatch;
	private final Map<Class<?>, TableClassInfo> tableClassesMap;

	public TableClassesCache(ExcelManager excelManager, boolean strictMatch) {
		this.excelManager = excelManager;
		this.strictMatch = strictMatch;
		this.tableClassesMap = new HashMap<>();
	}

	/*public ExcelManager getExcelManager() {
		return this.excelManager;
	}

	public boolean isStrictMatch() {
		return this.strictMatch;
	}*/

	public TableClassInfo of(Field field) {
		Class<?> tableClass = BindHelper.getTableClass(field);
		if (!this.tableClassesMap.containsKey(tableClass)) {
			TableClassInfo tableClassInfo = new TableClassInfo(tableClass, field, this.excelManager, this.strictMatch);
			this.tableClassesMap.put(tableClass, tableClassInfo);
			return tableClassInfo;
		}
		return this.tableClassesMap.get(tableClass);
	}

	public void flush(Class<?> tableClass) {
		this.tableClassesMap.remove(tableClass);
	}

	public void flushAll() {
		this.tableClassesMap.clear();
	}
}
