package aaa.utils.excel.bind.cache;

import java.util.List;
import aaa.utils.excel.bind.ReflectionHelper;
import aaa.utils.excel.io.ExcelManager;
import toolkit.exceptions.IstfException;

public class MarshallingCache extends TableClassesCache<MarshallingClassInfo> {
	public MarshallingCache(ExcelManager excelManager) {
		super(excelManager);
	}
	
	@Override
	protected MarshallingClassInfo getTableClassInfoInstance(Class<?> tableClass) {
		return new MarshallingClassInfo(tableClass, getExcelManager());
	}
	
	public MarshallingClassInfo of(Object tableObject) {
		Class<?> tableClass;
		if (List.class.isAssignableFrom(tableObject.getClass())) {
			if (((List<?>) tableObject).isEmpty()) {
				throw new IstfException("Ubable to get table class cache with empty rows objects list");
			}
			tableClass = ((List<?>) tableObject).get(0).getClass();
		} else {
			tableClass = tableObject.getClass();
		}
		
		MarshallingClassInfo tableClassInfo = of(tableClass);
		if (!tableClassInfo.hasRowsObjects()) {
			tableClassInfo.setRowsObjects(ReflectionHelper.getValueAsList(tableObject));
		}
		return tableClassInfo;
	}
}
