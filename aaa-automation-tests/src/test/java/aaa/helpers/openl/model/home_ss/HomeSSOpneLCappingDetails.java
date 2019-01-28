package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.CAPPINGDETAILS_SHEET_NAME, headerRowIndex = OpenLFile.CAPPINGDETAILS_HEADER_ROW_NUMBER)
public class HomeSSOpneLCappingDetails extends OpenLCappingDetails {
	private Double agePremium;
	private Integer cappingCalculatedForTerm;
	private String carrierCode;
	private String formCd;
	private String lineOfBusiness;
	private Double overriddenCappingFactor;
	private Double previousCappingFactor;
	private Double previousPolicyPremium;
	private String productCode;
	private String programCode;
	private String state;
	private Integer term;
	private Double termCappingFactor;
	private String underwriterCode;

	public Double getAgePremium() {
		return agePremium;
	}

	public void setAgePremium(Double agePremium) {
		this.agePremium = agePremium;
	}

	public Integer getCappingCalculatedForTerm() {
		return cappingCalculatedForTerm;
	}

	public void setCappingCalculatedForTerm(Integer cappingCalculatedForTerm) {
		this.cappingCalculatedForTerm = cappingCalculatedForTerm;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getFormCd() {
		return formCd;
	}

	public void setFormCd(String formCd) {
		this.formCd = formCd;
	}

	public String getLineOfBusiness() {
		return lineOfBusiness;
	}

	public void setLineOfBusiness(String lineOfBusiness) {
		this.lineOfBusiness = lineOfBusiness;
	}

	public Double getOverriddenCappingFactor() {
		return overriddenCappingFactor;
	}

	public void setOverriddenCappingFactor(Double overriddenCappingFactor) {
		this.overriddenCappingFactor = overriddenCappingFactor;
	}

	public Double getPreviousCappingFactor() {
		return previousCappingFactor;
	}

	public void setPreviousCappingFactor(Double previousCappingFactor) {
		this.previousCappingFactor = previousCappingFactor;
	}

	public Double getPreviousPolicyPremium() {
		return previousPolicyPremium;
	}

	public void setPreviousPolicyPremium(Double previousPolicyPremium) {
		this.previousPolicyPremium = previousPolicyPremium;
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

	public Double getTermCappingFactor() {
		return termCappingFactor == null ? 0.0 : termCappingFactor;
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
}
