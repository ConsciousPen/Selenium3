package aaa.helpers.openl.model.auto_ca.select;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLDriver;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.DRIVER_SHEET_NAME, headerRowIndex = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
public class AutoCaSelectOpenLDriver extends AutoCaOpenLDriver {
	private Boolean drivesync;
	private Boolean newDriver;
	private String type;
	private Integer yaf;

	public Boolean isDrivesync() {
		return drivesync;
	}

	public void setDrivesync(Boolean drivesync) {
		this.drivesync = drivesync;
	}

	public Boolean isNewDriver() {
		return newDriver;
	}

	public void setNewDriver(Boolean newDriver) {
		this.newDriver = newDriver;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getYaf() {
		return yaf;
	}

	public void setYaf(Integer yaf) {
		this.yaf = yaf;
	}
}
