package aaa.helpers.openl.model.home_ca;

import java.time.LocalDate;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;
import aaa.helpers.openl.mock_generator.HomeCaMockGenerator;
import aaa.helpers.openl.mock_generator.MockGenerator;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public abstract class HomeCaOpenLPolicy<F extends HomeCaOpenLForm> extends OpenLPolicy {
	protected Integer claimPoints;
	protected Double covCLimit;
	protected Integer expClaimPoints;
	protected Boolean isAaaMember;
	protected Integer yearsOfPriorInsurance;
	protected Integer yearsWithCsaa;

	@ExcelTransient
	private LocalDate effectiveDate;

	public Integer getClaimPoints() {
		return claimPoints;
	}

	public void setClaimPoints(Integer claimPoints) {
		this.claimPoints = claimPoints;
	}

	public Double getCovCLimit() {
		return covCLimit;
	}

	public void setCovCLimit(Double covCLimit) {
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
	public MocksCollection getRequiredMocks() {
		MocksCollection requiredMocks = new MocksCollection();
		MockGenerator mockGenerator = new HomeCaMockGenerator();
		if (!mockGenerator.isPropertyClassificationMockPresent()) {
			RetrievePropertyClassificationMock propertyClassificationMock = mockGenerator.getRetrievePropertyClassificationMock();
			requiredMocks.add(propertyClassificationMock);
		}
		return requiredMocks;
	}

	@Override
	public LocalDate getEffectiveDate() {
		if (effectiveDate == null) {
			return TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
		}
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public Integer getTerm() {
		//TODO-dchubkov: to be verified
		return 12;
	}

	@Override
	public String getUnderwriterCode() {
		return null;
	}

	public abstract List<F> getForms();

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
