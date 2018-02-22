package aaa.helpers.openl.model.home_ca.ho4;

import static aaa.helpers.openl.model.OpenLFile.DWELLING_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.DWELLING_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.FORM_SHEET_NAME;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLDwelling;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

public class HomeCaHO4OpenLPolicy extends HomeCaOpenLPolicy {
	@ExcelTableElement(sheetName = DWELLING_SHEET_NAME, headerRowIndex = DWELLING_HEADER_ROW_NUMBER)
	private List<HomeCaOpenLDwelling> dwelling;

	@ExcelTableElement(sheetName = FORM_SHEET_NAME, headerRowIndex = HomeCaHO4OpenLFile.FORM_HEADER_ROW_NUMBER)
	private List<HomeCaHO4OpenLForm> forms;

	@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
	private List<HomeCaOpenLCoverage> coverages;

	private String constructionGroup;
	private Integer covELimit;
	private Boolean hasEmployeeDiscount;
	private Boolean hasPolicySupportingForm;
	private Boolean hasSeniorDiscount;
	private String occupancyType;

	public List<HomeCaOpenLDwelling> getDwellings() {
		return new ArrayList<>(dwelling);
	}

	public void setDwellings(List<HomeCaOpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
	}

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

	public Integer getCovELimit() {
		return covELimit;
	}

	public void setCovELimit(Integer covELimit) {
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
