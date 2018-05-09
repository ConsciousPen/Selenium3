package aaa.helpers.openl.model.home_ca.dp3;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.FORM_SHEET_NAME, headerRowIndex = OpenLFile.FORM_HEADER_ROW_NUMBER)
public class HomeCaDP3OpenLForm extends HomeCaOpenLForm {
	private Integer percentage;

	public Integer getPercentage() {
		return percentage;
	}

	public void setPercentage(Integer percentage) {
		this.percentage = percentage;
	}

	@Override
	public String toString() {
		return "HomeCaDP3OpenLForm{" +
				"percentage='" + percentage + '\'' +
				", limit=" + limit +
				", applyDiscounts=" + applyDiscounts +
				", hasSupportingForm=" + hasSupportingForm +
				", number=" + number +
				", formCode='" + formCode + '\'' +
				", limit=" + limit +
				'}';
	}
}
