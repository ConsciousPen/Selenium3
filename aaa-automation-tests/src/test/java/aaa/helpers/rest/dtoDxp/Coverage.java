package aaa.helpers.rest.dtoDxp;

import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Coverage Information")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Coverage {

	@ApiModelProperty(value = "Coverage Code", example = "BI")
	public String coverageCd;

	@ApiModelProperty(value = "Coverage Description", example = "Bodily Injury Liability")
	public String coverageDescription;

	@ApiModelProperty(value = "Limit", example = "500000/1000000")
	public String coverageLimit;

	@ApiModelProperty(value = "Coverage Limit", example = "$500,000/$1,000,000")
	public String coverageLimitDisplay;

	@ApiModelProperty(value = "Delimiter", example = "Deductible")
	public String coverageType;

	@ApiModelProperty(value = "Customer Displayed?", example = "false")
	public Boolean customerDisplayed;

	@ApiModelProperty(value = "Is Coverage Available for Update", example = "false")
	public Boolean canChangeCoverage;

	public List<CoverageLimit> availableLimits;

	@ApiModelProperty(value = "List of drivers the coverage can be applied to")
	public List<String> availableDrivers;

	@ApiModelProperty(value = "List of drivers that the coverage is applied to")
	public List<String> currentlyAddedDrivers;

	public String getCoverageCd() {
		return coverageCd;
	}

	public Coverage setCoverageCd(String coverageCd) {
		this.coverageCd = coverageCd;
		return this;
	}

	public String getCoverageDescription() {
		return coverageDescription;
	}

	public Coverage setCoverageDescription(String coverageDescription) {
		this.coverageDescription = coverageDescription;
		return this;
	}

	public String getCoverageLimit() {
		return coverageLimit;
	}

	public Coverage setCoverageLimit(String coverageLimit) {
		this.coverageLimit = coverageLimit;
		return this;
	}

	public String getCoverageLimitDisplay() {
		return coverageLimitDisplay;
	}

	public Coverage setCoverageLimitDisplay(String coverageLimitDisplay) {
		this.coverageLimitDisplay = coverageLimitDisplay;
		return this;
	}

	public String getCoverageType() {
		return coverageType;
	}

	public Coverage setCoverageType(String coverageType) {
		this.coverageType = coverageType;
		return this;
	}

	public Boolean getCustomerDisplayed() {
		return customerDisplayed;
	}

	public Coverage setCustomerDisplayed(Boolean customerDisplayed) {
		this.customerDisplayed = customerDisplayed;
		return this;
	}

	public Boolean getCanChangeCoverage() {
		return canChangeCoverage;
	}

	public Coverage setCanChangeCoverage(Boolean canChangeCoverage) {
		this.canChangeCoverage = canChangeCoverage;
		return this;
	}

	public List<CoverageLimit> getAvailableLimits() {
		return availableLimits;
	}

	public Coverage setAvailableLimits(List<CoverageLimit> availableLimits) {
		this.availableLimits = availableLimits;
		return this;
	}

	public List<String> getAvailableDrivers() {
		return availableDrivers;
	}

	public Coverage setAvailableDrivers(List<String> availableDrivers) {
		this.availableDrivers = availableDrivers;
		return this;
	}

	public List<String> getCurrentlyAddedDrivers() {
		return currentlyAddedDrivers;
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
		return Objects.equals(coverageCd, coverage.coverageCd) &&
				Objects.equals(coverageDescription, coverage.coverageDescription) &&
				Objects.equals(coverageLimit, coverage.coverageLimit) &&
				Objects.equals(coverageLimitDisplay, coverage.coverageLimitDisplay) &&
				Objects.equals(coverageType, coverage.coverageType) &&
				Objects.equals(customerDisplayed, coverage.customerDisplayed) &&
				Objects.equals(canChangeCoverage, coverage.canChangeCoverage) &&
				Objects.equals(availableLimits, coverage.availableLimits) &&
				Objects.equals(availableDrivers, coverage.availableDrivers) &&
				Objects.equals(currentlyAddedDrivers, coverage.currentlyAddedDrivers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(coverageCd, coverageDescription, coverageLimit, coverageLimitDisplay, coverageType, customerDisplayed, canChangeCoverage, availableLimits, availableDrivers, currentlyAddedDrivers);
	}

	public Coverage setCurrentlyAddedDrivers(List<String> currentlyAddedDrivers) {
		this.currentlyAddedDrivers = currentlyAddedDrivers;
		return this;
	}
}
