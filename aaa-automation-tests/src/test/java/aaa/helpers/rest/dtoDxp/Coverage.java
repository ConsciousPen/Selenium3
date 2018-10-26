package aaa.helpers.rest.dtoDxp;

import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Coverage Information")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Coverage {
	public Coverage(){
	}

	private Coverage(CoverageBuilder coverageBuilder){
		this.coverageCd = coverageBuilder.coverageCd;
		this.coverageDescription = coverageBuilder.coverageDescription;
		this.coverageLimit = coverageBuilder.coverageLimit;
		this.coverageLimitDisplay = coverageBuilder.coverageLimitDisplay;
		this.coverageType = coverageBuilder.coverageType;
		this.customerDisplayed = coverageBuilder.customerDisplayed;
		this.canChangeCoverage = coverageBuilder.canChangeCoverage;
		this.availableLimits = coverageBuilder.availableLimits;
		this.availableDrivers = coverageBuilder.availableDrivers;
		this.currentlyAddedDrivers = coverageBuilder.currentlyAddedDrivers;

	}

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
				Objects.equals(getCurrentlyAddedDrivers(), coverage.getCurrentlyAddedDrivers());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCoverageCd(), getCoverageDescription(), getCoverageLimit(), getCoverageLimitDisplay(), getCoverageType(), getCustomerDisplayed(), getCanChangeCoverage(), getAvailableLimits(), getAvailableDrivers(), getCurrentlyAddedDrivers());
	}

	public Coverage setCurrentlyAddedDrivers(List<String> currentlyAddedDrivers) {
		this.currentlyAddedDrivers = currentlyAddedDrivers;
		return this;
	}

	public static class CoverageBuilder{
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

		public CoverageBuilder setCoverageCd(String coverageCd) {
			this.coverageCd = coverageCd;
			return this;
		}

		public CoverageBuilder setCoverageDescription(String coverageDescription) {
			this.coverageDescription = coverageDescription;
			return this;
		}

		public CoverageBuilder setCoverageLimit(String coverageLimit) {
			this.coverageLimit = coverageLimit;
			return this;
		}

		public CoverageBuilder setCoverageLimitDisplay(String coverageLimitDisplay) {
			this.coverageLimitDisplay = coverageLimitDisplay;
			return this;
		}

		public CoverageBuilder setCoverageType(String coverageType) {
			this.coverageType = coverageType;
			return this;
		}

		public CoverageBuilder setCustomerDisplayed(Boolean customerDisplayed) {
			this.customerDisplayed = customerDisplayed;
			return this;
		}

		public CoverageBuilder setCanChangeCoverage(Boolean canChangeCoverage) {
			this.canChangeCoverage = canChangeCoverage;
			return this;
		}

		public CoverageBuilder setAvailableLimits(List<CoverageLimit> availableLimits) {
			this.availableLimits = availableLimits;
			return this;
		}

		public CoverageBuilder setAvailableDrivers(List<String> availableDrivers) {
			this.availableDrivers = availableDrivers;
			return this;
		}

		public CoverageBuilder setCurrentlyAddedDrivers(List<String> currentlyAddedDrivers) {
			this.currentlyAddedDrivers = currentlyAddedDrivers;
			return this;
		}

		public Coverage build(){
			return new Coverage(this);
		}
	}

}
