package aaa.helpers.rest.dtoDxp;

import aaa.main.enums.CoverageInfo;
import aaa.main.enums.CoverageLimits;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApiModel(description = "Coverage Information")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coverage {

	@ApiModelProperty(value = "Coverage Code", example = "BI")
	private String coverageCd;

	@ApiModelProperty(value = "Coverage Description", example = "Bodily Injury Liability")
	private String coverageDescription;

	@ApiModelProperty(value = "Limit", example = "500000/1000000")
	private String coverageLimit;

	@ApiModelProperty(value = "Coverage Limit", example = "$500,000/$1,000,000")
	private String coverageLimitDisplay;

	@ApiModelProperty(value = "Delimiter", example = "Deductible")
	private String coverageType;

	@ApiModelProperty(value = "Customer Displayed?", example = "false")
	private Boolean customerDisplayed;

	@ApiModelProperty(value = "Is Coverage Available for Update", example = "false")
	private Boolean canChangeCoverage;

	private List<CoverageLimit> availableLimits;

	@ApiModelProperty(value = "List of drivers the coverage can be applied to")
	private LinkedHashSet<String> availableDrivers;

	@ApiModelProperty(value = "List of drivers that the coverage is applied to")
	private LinkedHashSet<String> currentlyAddedDrivers;

	@ApiModelProperty(value = "List of sub coverages associated to the coverage")
	private List<Coverage> subCoverages;

	@ApiModelProperty(value = "Insurer Name", example = "John Smith")
	private String insurerName; //for PIPPRIMINS coverage

	private String certNum; //for PIPPRIMINS coverage

	public List<String> relativesCovered;

	public static Coverage create(CoverageInfo coverageInfo) {
		Coverage coverage = new Coverage();
		coverage.coverageCd = coverageInfo.getCode();
		coverage.coverageDescription = coverageInfo.getDescription();
		if(coverageInfo.getDefaultLimit() != null) {
			coverage.coverageLimit = coverageInfo.getDefaultLimit().getLimit();
			coverage.coverageLimitDisplay = coverageInfo.getDefaultLimit().getDisplay();
		}
		coverage.availableLimits = coverageInfo.getAvailableLimits().stream()
				.map(al -> new CoverageLimit().setCoverageLimit(al.getLimit()).setCoverageLimitDisplay(al.getDisplay()))
				.collect(Collectors.toList());
		coverage.coverageType = coverageInfo.getCoverageType();
		coverage.canChangeCoverage = true;
		coverage.customerDisplayed = true;
		return coverage;
	}

	public static Coverage createWithCdAndDescriptionOnly(CoverageInfo coverageInfo) {
		Coverage coverage = new Coverage();
		coverage.coverageCd = coverageInfo.getCode();
		coverage.coverageDescription = coverageInfo.getDescription();
		coverage.availableLimits = ImmutableList.of();//empty list
		return coverage;
	}

	public Coverage changeLimit(CoverageLimits coverageLimit) {
		this.coverageLimit = coverageLimit.getLimit();
		this.coverageLimitDisplay = coverageLimit.getDisplay();
		return this;
	}

	public Coverage changeAvailableLimits(CoverageLimits... limitToAdd) {
		availableLimits.clear();
		for (CoverageLimits coverageLimit : limitToAdd) {
			CoverageLimit limit = new CoverageLimit().setCoverageLimit(coverageLimit.getLimit()).setCoverageLimitDisplay(coverageLimit.getDisplay());
			availableLimits.add(limit);
		}
		return this;
	}

	public Coverage removeAvailableLimit(CoverageLimits... coverageLimits) {
		Arrays.stream(coverageLimits).forEach(cl -> this.availableLimits.removeIf(p -> p.coverageLimit.equals(cl.getLimit())));
		return this;
	}

	public Coverage removeAvailableLimitsAbove(CoverageLimits removeAboveLimit) {
		int removeFromLimitIndex = availableLimits.stream().map(CoverageLimit::getCoverageLimit).collect(Collectors.toList()).indexOf(removeAboveLimit.getLimit()) + 1;
		int lastElementIndex = availableLimits.size();
		List<CoverageLimit> limitsToRemove = availableLimits.subList(removeFromLimitIndex, lastElementIndex);
		availableLimits.removeAll(limitsToRemove);
		return this;
	}

	public Coverage removeAvailableLimitsAll() {
		availableLimits.clear();
		return this;
	}

	public Coverage enableCanChange() {
		this.canChangeCoverage = true;
		return this;
	}

	public Coverage disableCustomerDisplay() {
		this.customerDisplayed = false;
		this.canChangeCoverage = false;
		return this;
	}

	public Coverage enableCustomerDisplay() {
		this.customerDisplayed = true;
		return this;
	}

	public Coverage disableCanChange() {
		this.canChangeCoverage = false;
		return this;
	}

	public Coverage changeDescription(String newDescription) {
		this.coverageDescription = newDescription;
		return this;
	}

	public Coverage addAvailableDrivers(String... driverOids) {
		if(this.availableDrivers == null) {
			this.availableDrivers = new LinkedHashSet<>();
		}
		this.availableDrivers.addAll(Arrays.asList(driverOids));
		return this;
	}

	public Coverage addCurrentlyAddedDrivers(String... driverOids) {
		if(this.currentlyAddedDrivers == null) {
			this.currentlyAddedDrivers = new LinkedHashSet<>();
		}
		this.currentlyAddedDrivers.addAll(Arrays.asList(driverOids));
		return this;
	}

	public String getCoverageCd() {
		return coverageCd;
	}

	public String getCoverageDescription() {
		return coverageDescription;
	}

	public String getCoverageLimit() {
		return coverageLimit;
	}

	public String getCoverageLimitDisplay() {
		return coverageLimitDisplay;
	}

	public String getCoverageType() {
		return coverageType;
	}

	public Boolean getCustomerDisplayed() {
		return customerDisplayed;
	}

	public Boolean getCanChangeCoverage() {
		return canChangeCoverage;
	}

	public List<CoverageLimit> getAvailableLimits() {
		return availableLimits;
	}

	public LinkedHashSet<String> getAvailableDrivers() {
		return availableDrivers;
	}

	public LinkedHashSet<String> getCurrentlyAddedDrivers() {
		return currentlyAddedDrivers;
	}

	public List<Coverage> getSubCoverages() {
		return subCoverages;
	}

	public String getInsurerName() {
		return insurerName;
	}
	public String getCertNum(){
		return certNum;
	}

	public List <String> getRelativesCovered(){
		return relativesCovered;
	}

	public Coverage addInsurerName(String insurerName) {
		this.insurerName = insurerName;
		return this;
	}

	public Coverage addCertNum(String certNum) {
		this.certNum = certNum;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Coverage coverage = (Coverage) o;
		return Objects.equals(getCoverageCd(), coverage.getCoverageCd()) &&
				Objects.equals(getCoverageDescription(), coverage.getCoverageDescription()) &&
				Objects.equals(getCoverageLimit(), coverage.getCoverageLimit()) &&
				Objects.equals(getCoverageLimitDisplay(), coverage.getCoverageLimitDisplay()) &&
				Objects.equals(getCoverageType(), coverage.getCoverageType()) &&
				Objects.equals(getCustomerDisplayed(), coverage.getCustomerDisplayed()) &&
				Objects.equals(getCanChangeCoverage(), coverage.getCanChangeCoverage()) &&
				Objects.equals(getAvailableLimits(), coverage.getAvailableLimits()) &&
				Objects.equals(getAvailableDrivers(), coverage.getAvailableDrivers()) &&
				Objects.equals(getCurrentlyAddedDrivers(), coverage.getCurrentlyAddedDrivers()) &&
				Objects.equals(getSubCoverages(), coverage.getSubCoverages()) &&
				Objects.equals(getInsurerName(), coverage.getInsurerName()) &&
				Objects.equals(getCertNum(), coverage.getCertNum());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCoverageCd(), getCoverageDescription(), getCoverageLimit(), getCoverageLimitDisplay(),
				getCoverageType(), getCustomerDisplayed(), getCanChangeCoverage(), getAvailableLimits(),
				getAvailableLimits(), getCurrentlyAddedDrivers(), getSubCoverages(), getInsurerName(), getCertNum(), getRelativesCovered());
	}

	@Override
	public String toString() {
		return "Coverage{" +
				"coverageCd='" + coverageCd + '\'' +
				", coverageDescription='" + coverageDescription + '\'' +
				", coverageLimit='" + coverageLimit + '\'' +
				", coverageLimitDisplay='" + coverageLimitDisplay + '\'' +
				", coverageType='" + coverageType + '\'' +
				", customerDisplayed=" + customerDisplayed +
				", canChangeCoverage=" + canChangeCoverage +
				", availableLimits=" + availableLimits +
				", availableDrivers=" + availableDrivers +
				", currentlyAddedDrivers=" + currentlyAddedDrivers +
				", subCoverages=" + subCoverages +
				", insurerName=" + insurerName +
				", certNum=" + certNum +
				", relativesCovered=" + relativesCovered +
				'}';
	}
}
