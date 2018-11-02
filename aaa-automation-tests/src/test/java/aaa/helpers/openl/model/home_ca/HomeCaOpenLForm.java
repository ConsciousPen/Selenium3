package aaa.helpers.openl.model.home_ca;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLForm;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.FORM_SHEET_NAME, headerRowIndex = HomeCaHO6OpenLFile.FORM_HEADER_ROW_NUMBER)
public class HomeCaOpenLForm extends OpenLForm {
	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	@ExcelColumnElement(name = "formLimit")
	protected Double limit;

	@RequiredField
	protected Boolean applyDiscounts;

	protected Boolean hasSupportingForm;

	public Boolean getApplyDiscounts() {
		return applyDiscounts;
	}

	public void setApplyDiscounts(Boolean applyDiscounts) {
		this.applyDiscounts = applyDiscounts;
	}

	public Boolean getHasSupportingForm() {
		return hasSupportingForm;
	}

	public void setHasSupportingForm(Boolean hasSupportingForm) {
		this.hasSupportingForm = hasSupportingForm;
	}

}
