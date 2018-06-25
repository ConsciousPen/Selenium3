package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import aaa.utils.excel.bind.BindHelper;
import aaa.utils.excel.io.ExcelManager;

public abstract class TableClassesCache<T extends TableClassInfo> {
	protected final Map<Class<?>, T> tableClassesMap;
	private final ExcelManager excelManager;
	
	public TableClassesCache(ExcelManager excelManager) {
		this.excelManager = excelManager;
		this.tableClassesMap = new HashMap<>();
	}
	
	protected ExcelManager getExcelManager() {
		return excelManager;
	}
	
	public T of(Field field) {
		return of(BindHelper.getFieldType(field));
	}
	
	public T of(Class<?> tableClass) {
		if (!this.tableClassesMap.containsKey(tableClass)) {
			T tableClassInfo = getTableClassInfoInstance(tableClass);
			this.tableClassesMap.put(tableClass, tableClassInfo);
			return tableClassInfo;
		}
		return this.tableClassesMap.get(tableClass);
	}
	
	public void flush(Class<?> tableClass) {
		of(tableClass).flushInfo();
		this.tableClassesMap.remove(tableClass);
	}
	
	public void flushAll() {
		tableClassesMap.values().forEach(TableClassInfo::flushInfo);
		this.tableClassesMap.clear();
	}
	
	protected abstract T getTableClassInfoInstance(Class<?> tableClass);
}
