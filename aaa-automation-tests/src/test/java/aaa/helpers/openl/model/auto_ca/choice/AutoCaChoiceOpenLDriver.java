package aaa.helpers.openl.model.auto_ca.choice;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLDriver;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.DRIVER_SHEET_NAME, headerRowIndex = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
public class AutoCaChoiceOpenLDriver extends AutoCaOpenLDriver {
	private Boolean driverTrainingDiscount;
	private Boolean nonSmoker;
	private Boolean occasionalUse;

	public void setDriverTrainingDiscount(Boolean driverTrainingDiscount) {
		this.driverTrainingDiscount = driverTrainingDiscount;
	}

	public void setNonSmoker(Boolean nonSmoker) {
		this.nonSmoker = nonSmoker;
	}

	public void setOccasionalUse(Boolean occasionalUse) {
		this.occasionalUse = occasionalUse;
	}

	public Boolean hasDriverTrainingDiscount() {
		return driverTrainingDiscount;
	}

	public Boolean isNonSmoker() {
		return nonSmoker;
	}

	public Boolean isOccasionalUse() {
		return occasionalUse;
	}
}
