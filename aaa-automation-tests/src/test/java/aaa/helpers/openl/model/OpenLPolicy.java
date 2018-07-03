package aaa.helpers.openl.model;

import java.time.LocalDate;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.datax.TestData;

public abstract class OpenLPolicy {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected String policyNumber;

	@ExcelTransient
	private Dollar expectedPremium;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public Dollar getExpectedPremium() {
		return expectedPremium;
	}

	public void setExpectedPremium(Dollar expectedPremium) {
		this.expectedPremium = expectedPremium;
	}

	public abstract Integer getTerm();

	public Double getPreviousPolicyPremium() {
		return null;
	}

	public abstract String getUnderwriterCode();

	public abstract LocalDate getEffectiveDate();

	public MocksCollection getRequiredMocks() {
		//override this method in child classes if this type of policy require specific mocks.
		return null;
	}
	
	@Override
	public String toString() {
		return "OpenLPolicy{" +
				"number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}

	public abstract TestDataGenerator<? extends OpenLPolicy> getTestDataGenerator(String state, TestData baseTestData);
}
