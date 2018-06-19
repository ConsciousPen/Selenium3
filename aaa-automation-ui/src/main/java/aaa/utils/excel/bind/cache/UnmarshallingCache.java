package aaa.utils.excel.bind.cache;

import aaa.utils.excel.io.ExcelManager;

public class UnmarshallingCache extends TableClassesCache<UnmarshallingClassInfo> {
	public UnmarshallingCache(ExcelManager excelManager, boolean strictMatchBinding) {
		super(excelManager, strictMatchBinding);
	}
	
	@Override
	protected UnmarshallingClassInfo getTableClassInfoInstance(Class<?> tableClass) {
		return new UnmarshallingClassInfo(tableClass, getExcelManager(), isStrictMatchBinding());
	}
}
