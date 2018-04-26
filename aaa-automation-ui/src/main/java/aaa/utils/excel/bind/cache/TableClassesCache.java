package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import aaa.utils.excel.bind.BindHelper;
import aaa.utils.excel.io.ExcelManager;

public class TableClassesCache {
	private final ExcelManager excelManager;
	private final boolean strictMatchBinding;
	private final Map<Class<?>, TableClassInfo> tableClassesMap;

	public TableClassesCache(ExcelManager excelManager, boolean strictMatchBinding) {
		this.excelManager = excelManager;
		this.strictMatchBinding = strictMatchBinding;
		this.tableClassesMap = new HashMap<>();
	}

	public boolean isStrictMatchBinding() {
		return this.strictMatchBinding;
	}

	public TableClassInfo of(Field field) {
		return of(BindHelper.getTableClass(field));
	}

	public TableClassInfo of(Class<?> tableClass) {
		if (!this.tableClassesMap.containsKey(tableClass)) {
			TableClassInfo tableClassInfo = new TableClassInfo(tableClass, this.excelManager, this.strictMatchBinding);
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
