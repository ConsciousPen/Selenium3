package aaa.utils.excel.bind.cache;

import aaa.utils.excel.io.ExcelManager;

public class UnmarshallingCache extends TableClassesCache<UnmarshallingClassInfo> {
	private final boolean strictMatchBinding;
	
	public UnmarshallingCache(ExcelManager excelManager, boolean strictMatchBinding) {
		super(excelManager);
		this.strictMatchBinding = strictMatchBinding;
	}
	
	public boolean isStrictMatchBinding() {
		return this.strictMatchBinding;
	}
	
	@Override
	protected UnmarshallingClassInfo getTableClassInfoInstance(Class<?> tableClass) {
		return new UnmarshallingClassInfo(tableClass, getExcelManager(), isStrictMatchBinding());
	}
}
