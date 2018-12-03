package aaa.helpers.openl.model.auto_ss;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.CAPPINGDETAILS_SHEET_NAME, headerRowIndex = OpenLFile.CAPPINGDETAILS_HEADER_ROW_NUMBER)
public class AutoSSOpenLCappingDetails extends OpenLCappingDetails {
	@RequiredField
	private Integer term;
	private String state;
	private String carrierCode;
	private String lineOfBusiness;
	private Double previousCappingFactor;
	private String productCode;
	private String programCode;
	private Double termCappingFactor;
	private String underwriterCode;
	private Double previousPolicyPremium;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getLineOfBusiness() {
		return lineOfBusiness;
	}

	public void setLineOfBusiness(String lineOfBusiness) {
		this.lineOfBusiness = lineOfBusiness;
	}

	public Double getPreviousCappingFactor() {
		return previousCappingFactor;
	}

	public void setPreviousCappingFactor(Double previousCappingFactor) {
		this.previousCappingFactor = previousCappingFactor;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProgramCode() {
		return programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public Double getTermCappingFactor() {
		return termCappingFactor;
	}

	public void setTermCappingFactor(Double termCappingFactor) {
		this.termCappingFactor = termCappingFactor;
	}

	public String getUnderwriterCode() {
		return underwriterCode;
	}

	public void setUnderwriterCode(String underwriterCode) {
		this.underwriterCode = underwriterCode;
	}

	public Double getPreviousPolicyPremium() {
		return previousPolicyPremium;
	}

	public void setPreviousPolicyPremium(Double previousPolicyPremium) {
		this.previousPolicyPremium = previousPolicyPremium;
	}
}
