package aaa.utils.excel.io.entity;

import java.io.File;
import aaa.utils.excel.io.ExcelManager;

public interface Writable {

	ExcelManager getExcelManager();

	default ExcelManager save() {
		return getExcelManager().save();
	}

	default ExcelManager save(File destinationFile) {
		return getExcelManager().save(destinationFile);
	}

	default ExcelManager close() {
		return getExcelManager().close();
	}

	default ExcelManager saveAndClose() {
		return getExcelManager().saveAndClose();
	}

	default ExcelManager saveAndClose(File destinationFile) {
		return getExcelManager().saveAndClose(destinationFile);
	}
}
