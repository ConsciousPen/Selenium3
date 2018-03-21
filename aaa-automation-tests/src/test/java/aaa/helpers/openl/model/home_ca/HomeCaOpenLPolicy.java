package aaa.helpers.openl.model.home_ca;

import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.OpenLPolicy;

public class HomeCaOpenLPolicy extends OpenLPolicy {
	protected Integer claimPoints;
	protected Integer covCLimit;
	protected Integer expClaimPoints;
	protected Boolean isAaaMember;
	protected Integer yearsOfPriorInsurance;
	protected Integer yearsWithCsaa;

	public Integer getClaimPoints() {
		return claimPoints;
	}

	public void setClaimPoints(Integer claimPoints) {
		this.claimPoints = claimPoints;
	}

	public Integer getCovCLimit() {
		return covCLimit;
	}

	public void setCovCLimit(Integer covCLimit) {
		this.covCLimit = covCLimit;
	}

	public Integer getExpClaimPoints() {
		return expClaimPoints;
	}

	public void setExpClaimPoints(Integer expClaimPoints) {
		this.expClaimPoints = expClaimPoints;
	}

	public Boolean getAaaMember() {
		return isAaaMember;
	}

	public void setAaaMember(Boolean aaaMember) {
		isAaaMember = aaaMember;
	}

	public Integer getYearsOfPriorInsurance() {
		return yearsOfPriorInsurance;
	}

	public void setYearsOfPriorInsurance(Integer yearsOfPriorInsurance) {
		this.yearsOfPriorInsurance = yearsOfPriorInsurance;
	}

	public Integer getYearsWithCsaa() {
		return yearsWithCsaa;
	}

	public void setYearsWithCsaa(Integer yearsWithCsaa) {
		this.yearsWithCsaa = yearsWithCsaa;
	}

	@Override
	public Integer getTerm() {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException(String.format("Getting term for %s is not implemented", this.getClass().getSimpleName()));
	}

	@Override
	public String toString() {
		return "HomeCaOpenLPolicy{" +
				"claimPoints=" + claimPoints +
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
