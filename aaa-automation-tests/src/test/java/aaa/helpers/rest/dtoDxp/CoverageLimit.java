package aaa.helpers.rest.dtoDxp;

import java.util.Objects;
import io.swagger.annotations.ApiModelProperty;

public class CoverageLimit {

	@ApiModelProperty(value = "Limit", example = "500000/1000000")
	public String coverageLimit;

	@ApiModelProperty(value = "Coverage Limit", example = "$500,000/$1,000,000")
	public String coverageLimitDisplay;

	public String getCoverageLimit() {
		return coverageLimit;
	}

	public CoverageLimit setCoverageLimit(String coverageLimit) {
		this.coverageLimit = coverageLimit;
		return this;
	}

	public String getCoverageLimitDisplay() {
		return coverageLimitDisplay;
	}

	public CoverageLimit setCoverageLimitDisplay(String coverageLimitDisplay) {
		this.coverageLimitDisplay = coverageLimitDisplay;
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
		CoverageLimit that = (CoverageLimit) o;
		return Objects.equals(coverageLimit, that.coverageLimit) &&
				Objects.equals(coverageLimitDisplay, that.coverageLimitDisplay);
	}

	@Override
	public int hashCode() {
		return Objects.hash(coverageLimit, coverageLimitDisplay);
	}
}
