package aaa.modules.regression.sales.home_ca.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInitiateHOQuoteTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)

public class TestInitiateHOQuoteHO29Endorsement extends TestInitiateHOQuoteTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test HO29 Endorsement added by default
	 * @scenario
	 * 1. Create new customer.
	 * 2. Initiate CAH quote creation.
	 * 3. Fill all mandatory fields on all tabs.
	 * 4. Assert that HO-29 Form is added.
	 * 5. Purchase policy.
	 * 6. Initiate HO Quote.
	 * 7. Fill all mandatory fields on all tabs.
	 * 8. Assert that HO-29 Form is added.
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-13261")
	public void pas13261_testInitiateHOQuoteHO29added(@Optional("CA") String state) {

		pas13261_testInitiateHOQuoteHO29added();
	}
}