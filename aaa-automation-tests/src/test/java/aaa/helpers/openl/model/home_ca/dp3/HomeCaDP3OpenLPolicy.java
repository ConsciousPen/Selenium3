package aaa.helpers.openl.model.home_ca.dp3;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.POLICY_SHEET_NAME, headerRowIndex = OpenLFile.POLICY_HEADER_ROW_NUMBER)
public class HomeCaDP3OpenLPolicy extends HomeCaOpenLPolicy<HomeCaDP3OpenLForm> {

	private List<HomeCaDP3OpenLDwelling> dwelling;
	private List<HomeCaDP3OpenLForm> forms;
	private List<HomeCaDP3OpenLCoverage> coverages;

	private Integer ageOfOldestInsured;
	private Integer covALimit;
	private Integer covELimit;
	private Integer deductible;
	private Double frequencyOfDwellingLoss;
	private Boolean hasAutoPolicy;
	private Boolean hasCeaPolicy;
	private Boolean isNewLoan;
	private String propertyManagerType;
	private Integer yearsOwned;
	private Integer yearsSinceLoan;

	public List<HomeCaDP3OpenLDwelling> getDwellings() {
		return new ArrayList<>(dwelling);
	}

	public void setDwellings(List<HomeCaDP3OpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
	}

	public List<HomeCaDP3OpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<HomeCaDP3OpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public Integer getAgeOfOldestInsured() {
		return ageOfOldestInsured;
	}

	public void setAgeOfOldestInsured(Integer ageOfOldestInsured) {
		this.ageOfOldestInsured = ageOfOldestInsured;
	}

	public Integer getCovALimit() {
		return covALimit;
	}

	public void setCovALimit(Integer covALimit) {
		this.covALimit = covALimit;
	}

	public Integer getCovELimit() {
		return covELimit;
	}

	public void setCovELimit(Integer covELimit) {
		this.covELimit = covELimit;
	}

	public Integer getDeductible() {
		return deductible;
	}

	public void setDeductible(Integer deductible) {
		this.deductible = deductible;
	}

	public Double getFrequencyOfDwellingLoss() {
		return frequencyOfDwellingLoss;
	}

	public void setFrequencyOfDwellingLoss(Double frequencyOfDwellingLoss) {
		this.frequencyOfDwellingLoss = frequencyOfDwellingLoss;
	}

	public Boolean getHasAutoPolicy() {
		return hasAutoPolicy;
	}

	public void setHasAutoPolicy(Boolean hasAutoPolicy) {
		this.hasAutoPolicy = hasAutoPolicy;
	}

	public Boolean getHasCeaPolicy() {
		return hasCeaPolicy;
	}

	public void setHasCeaPolicy(Boolean hasCeaPolicy) {
		this.hasCeaPolicy = hasCeaPolicy;
	}

	public Boolean getNewLoan() {
		return isNewLoan;
	}

	public void setNewLoan(Boolean newLoan) {
		isNewLoan = newLoan;
	}

	public String getPropertyManagerType() {
		return propertyManagerType;
	}

	public void setPropertyManagerType(String propertyManagerType) {
		this.propertyManagerType = propertyManagerType;
	}

	public Integer getYearsOwned() {
		return yearsOwned;
	}

	public void setYearsOwned(Integer yearsOwned) {
		this.yearsOwned = yearsOwned;
	}

	public Integer getYearsSinceLoan() {
		return yearsSinceLoan;
	}

	public void setYearsSinceLoan(Integer yearsSinceLoan) {
		this.yearsSinceLoan = yearsSinceLoan;
	}

	@Override
	public List<HomeCaDP3OpenLForm> getForms() {
		return new ArrayList<>(forms);
	}

	public void setForms(List<HomeCaDP3OpenLForm> forms) {
		this.forms = new ArrayList<>(forms);
	}

	@Override
	public String toString() {
		return "HomeCaDP3OpenLPolicy{" +
				"coverages=" + coverages +
				", dwelling=" + dwelling +
				", forms=" + forms +
				", ageOfOldestInsured=" + ageOfOldestInsured +
				", covALimit=" + covALimit +
				", covELimit=" + covELimit +
				", deductible=" + deductible +
				", frequencyOfDwellingLoss=" + frequencyOfDwellingLoss +
				", hasAutoPolicy=" + hasAutoPolicy +
				", hasCeaPolicy=" + hasCeaPolicy +
				", isNewLoan=" + isNewLoan +
				", propertyManagerType='" + propertyManagerType + '\'' +
				", yearsOwned=" + yearsOwned +
				", yearsSinceLoan=" + yearsSinceLoan +
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
