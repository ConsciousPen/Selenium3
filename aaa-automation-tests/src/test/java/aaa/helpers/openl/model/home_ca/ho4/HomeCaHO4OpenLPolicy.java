package aaa.helpers.openl.model.home_ca.ho4;

import static aaa.helpers.openl.model.OpenLFile.POLICY_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.POLICY_SHEET_NAME;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLDwelling;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowIndex = POLICY_HEADER_ROW_NUMBER)
public class HomeCaHO4OpenLPolicy extends HomeCaOpenLPolicy<HomeCaHO4OpenLForm> {

	private HomeCaOpenLDwelling dwelling;
	private List<HomeCaHO4OpenLForm> forms;
	private List<HomeCaOpenLCoverage> coverages;

	private String constructionGroup;
	private Double covELimit;
	private Boolean hasEmployeeDiscount;
	private Boolean hasPolicySupportingForm;
	private Boolean hasSeniorDiscount;
	private String occupancyType;

	public HomeCaOpenLDwelling getDwelling() {
		return dwelling;
	}

	public void setDwellings(HomeCaOpenLDwelling dwelling) {
		this.dwelling = dwelling;
	}

	@Override
	public List<HomeCaHO4OpenLForm> getForms() {
		return new ArrayList<>(forms);
	}

	public void setForms(List<HomeCaHO4OpenLForm> forms) {
		this.forms = new ArrayList<>(forms);
	}

	public List<HomeCaOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<HomeCaOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public String getConstructionGroup() {
		return constructionGroup;
	}

	public void setConstructionGroup(String constructionGroup) {
		this.constructionGroup = constructionGroup;
	}

	public Double getCovELimit() {
		return covELimit;
	}

	public void setCovELimit(Double covELimit) {
		this.covELimit = covELimit;
	}

	public Boolean getHasEmployeeDiscount() {
		return hasEmployeeDiscount;
	}

	public void setHasEmployeeDiscount(Boolean hasEmployeeDiscount) {
		this.hasEmployeeDiscount = hasEmployeeDiscount;
	}

	public Boolean getHasPolicySupportingForm() {
		return hasPolicySupportingForm;
	}

	public void setHasPolicySupportingForm(Boolean hasPolicySupportingForm) {
		this.hasPolicySupportingForm = hasPolicySupportingForm;
	}

	public Boolean getHasSeniorDiscount() {
		return hasSeniorDiscount;
	}

	public void setHasSeniorDiscount(Boolean hasSeniorDiscount) {
		this.hasSeniorDiscount = hasSeniorDiscount;
	}

	public String getOccupancyType() {
		return occupancyType;
	}

	public void setOccupancyType(String occupancyType) {
		this.occupancyType = occupancyType;
	}

	@Override
	public String toString() {
		return "HomeCaHO4OpenLPolicy{" +
				"dwelling=" + dwelling +
				", forms=" + forms +
				", constructionGroup='" + constructionGroup + '\'' +
				", covELimit=" + covELimit +
				", hasEmployeeDiscount=" + hasEmployeeDiscount +
				", hasPolicySupportingForm=" + hasPolicySupportingForm +
				", hasSeniorDiscount=" + hasSeniorDiscount +
				", occupancyType='" + occupancyType + '\'' +
				", coverages=" + coverages +
				", claimPoints=" + claimPoints +
				", covCLimit=" + covCLimit +
				", expClaimPoints=" + expClaimPoints +
				", isAaaMember=" + isAaaMember +
				", yearsOfPriorInsurance=" + yearsOfPriorInsurance +
				", yearsWithCsaa=" + yearsWithCsaa +
				", number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}
}
