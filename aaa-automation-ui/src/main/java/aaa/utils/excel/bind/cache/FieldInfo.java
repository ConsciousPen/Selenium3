package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldInfo {
	protected static Logger log = LoggerFactory.getLogger(FieldInfo.class);

	private final Field field;
	private final TableClassesCache tableClassesCache;
	/*private final ExcelManager excelManager;
	private final boolean strictMatch; //TODO-dchubkov: remove this field?*/

	public FieldInfo(Field field, TableClassesCache tableClassesCache) {
		this.field = field;
		/*this.excelManager = excelManager;
		this.strictMatch = strictMatch;*/
		this.tableClassesCache = tableClassesCache;
	}

	public Field getField() {
		return field;
	}

	public TableClassesCache getTableClassesCache() {
		return tableClassesCache;
	}

	/*public ExcelManager getExcelManager() {
		return excelManager;
	}

	public boolean isStrictMatch() {
		return strictMatch;
	}*/

	//protected abstract FieldInfo initialize();


	/*public FieldInfo of(Field field) {
		if (!this.fieldsInfoMap.containsKey(field)) {
			FieldInfo fieldInfo = initialize();
			fieldsInfoMap.put(field, fieldInfo);
			return fieldInfo;
		}
		return this.fieldsInfoMap.get(field);
	}*/

	//public abstract ExcelTable getExcelTable();
}
