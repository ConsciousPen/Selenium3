package aaa.helpers.rest.dtoDxp;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;
import aaa.main.enums.AvailableCoverageLimits;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import aaa.main.enums.CoverageInfo;
import aaa.main.enums.CoverageLimits;

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
	private List<String> availableDrivers;

	@ApiModelProperty(value = "List of drivers that the coverage is applied to")
	private List<String> currentlyAddedDrivers;

	@ApiModelProperty(value = "List of sub coverages associated to the coverage")
	private List<Coverage> subCoverages;

	public static Coverage create(CoverageInfo coverageInfo) {
		Coverage coverage = new Coverage();
		coverage.coverageCd = coverageInfo.getCode();
		coverage.coverageDescription = coverageInfo.getDescription();
		coverage.coverageLimit = coverageInfo.getDefaultLimit().getLimit();
		coverage.coverageLimitDisplay = coverageInfo.getDefaultLimit().getDisplay();
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

	public Coverage removeAvailableLimit(CoverageLimits coverageLimit) {
		this.availableLimits.removeIf(p -> p.coverageLimit.equals(coverageLimit.getLimit()));
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

	public Coverage disableCanChange() {
		this.canChangeCoverage = false;
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

	public List<String> getAvailableDrivers() {
		return availableDrivers;
	}

	public List<String> getCurrentlyAddedDrivers() {
		return currentlyAddedDrivers;
	}

	public List<Coverage> getSubCoverages() {
		return subCoverages;
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
				Objects.equals(getSubCoverages(), coverage.getSubCoverages());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCoverageCd(), getCoverageDescription(), getCoverageLimit(), getCoverageLimitDisplay(), getCoverageType(), getCustomerDisplayed(), getCanChangeCoverage(), getAvailableLimits(), getAvailableDrivers(), getCurrentlyAddedDrivers(), getSubCoverages());
	}

}
