package aaa.modules.regression.sales.home_ca.ho3;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.home_ca.HOCATestDataProvider;
import aaa.modules.regression.sales.home_ca.TestAbstractAHDRXXDiscountRemovalDocument;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestAHDRXXDiscountRemovalDocument extends TestAbstractAHDRXXDiscountRemovalDocument {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = {"PAS-3717", "PAS-3712"})
	public void pas550_membershipEligibilityConfigurationTrueForCancelledMembership(@Optional("CA") String state) {
		super.pas550_membershipEligibilityConfigurationTrueForCancelledMembership(HOCATestDataProvider.getPas550TestData(getPolicyTD()));
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
}