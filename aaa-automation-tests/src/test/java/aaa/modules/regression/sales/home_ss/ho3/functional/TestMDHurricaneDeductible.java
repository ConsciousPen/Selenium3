package aaa.modules.regression.sales.home_ss.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestHurricaneDeductibleTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test Home SS Hurricane Deductible for specified zip codes
 * @scenario 1. Create new customer.
 * 2. Initiate HSS quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium. Check That Hurricane Mandatory minimum is 1%.
 * 4. Change Hurricane Deductible to 5%. Save NB Value.
 * 5. Issue Policy.
 * 6. Endorse Policy.
 * 7. Check That Hurricane Deductible is 5%.
 * 8. Change Hurricane Deductible to 2%.
 * 9. Issue Endorsement.
 * 10. Renew Policy.
 * 11. Check That Hurricane Deductible is 2%.
 * 12. Change Hurricane Deductible to 1%.
 * 13. Calculate Premium.
 * 14. Check that Premium is calculated. P&C page is opened.
 * @details
 */

@StateList(states = Constants.States.MD)
public class TestMDHurricaneDeductible extends TestHurricaneDeductibleTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MD Hurricane Deductible")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6907")
	public void pas6907_testMDHurricaneDeductible(@Optional("MD") String state) {

		pas6907_testMDHurricaneDeductible();
	}
}