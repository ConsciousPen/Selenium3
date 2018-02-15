package aaa.helpers.openl.model.home_ca;

import aaa.helpers.openl.model.OpenLForm;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

public class HomeCaOpenLForm extends OpenLForm {
	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	@ExcelTableColumnElement(name = "formLimit")
	protected Integer limit;

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
