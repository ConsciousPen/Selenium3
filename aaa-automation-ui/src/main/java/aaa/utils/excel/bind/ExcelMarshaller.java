package aaa.utils.excel.bind;

import java.io.File;
import aaa.utils.excel.io.ExcelManager;

public class ExcelMarshaller {
	
	public void marshal(Object excelFileObject, File outputExcelFile) {
		try (ExcelManager excelManager = new ExcelManager(outputExcelFile)) {
			//excelManager.createSheet("aaa").createTable(4, "_PK_", "aaaAsdInsurancePersistency", "aaaCondoPolicy").addRow(ImmutableMap.of("_PK_", 1, "aaaAsdInsurancePersistency", 0)).saveAndClose();
		}
		//TODO-dchubkov: to be implemented...
	}
}
