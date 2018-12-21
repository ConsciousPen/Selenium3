package aaa.modules.regression.sales.home_ca.ho6.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestCarryOverValuesTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class TestRenewalStatusChange extends TestCarryOverValuesTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO6;
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Renewal Status change
	 * @scenario
	 * 1. Create Individual Customer
	 * 2. Create Property Policy
	 * 3. Initiate Renewal
	 * 4. Propose
	 * 5. Customer Decline Renewal Offer
	 * 6. Endorse current term
	 * 7. Navigate to Billing
	 * 8. Assert that Renewal Status is Customer Declined
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Renewal Status change")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-17736")
	public void pas17736_TestRenewalStatusChangeCA_HO6(@Optional("") String state) {

		pas17736_TestRenewalStatusChangeCA();
	}
}