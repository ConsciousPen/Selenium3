package aaa.utils.excel.bind;

import java.io.File;
import aaa.utils.excel.io.ExcelManager;

public class ExcelMarshaller {

	public void marshal(Object excelFileObject, File outputExcelFile) {
		try (ExcelManager excelManager = new ExcelManager(outputExcelFile)) {
			//to be implemented...
			excelManager.save();
		}
	}
}
