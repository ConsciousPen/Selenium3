package aaa.helpers.openl.model.home_ca.dp3;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.FORM_SHEET_NAME, headerRowIndex = HomeCaDP3OpenLFile.FORM_HEADER_ROW_NUMBER)
public class HomeCaDP3OpenLForm extends HomeCaOpenLForm {
	private Double percentage;

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	
	@Override
	public Double getLimit() {
		return limit;
	}

	@Override
	public void setLimit(Double limit) {
		this.limit = limit;
	}
}
