package aaa.modules.regression.sales.home_ca.dp3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestPPCReportReOrderRuleTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test Check PPC Report ReOrder Rule
 * @scenario
 * 1. Open App.
 * 2. Create Customer.
 * 3. Create Policy.
 * 4. Endorse Policy.
 * 5. Change Dwelling Address. DO NOT reorder Reports.
 * 6. If HO6,HO3,DP3 then Bind Endorsement.
 * 6a. If PolicyType is HO4 check that error is present.
 * 7. Renew Policy (works only going with time points and batch jobs).
 * 8. Endorse Policy.
 * 9. Calculate Premium.
 * 10. Bind Endorsement.
 * @details
 */
public class TestPPCReportReOrderRule extends TestPPCReportReOrderRuleTemplate {

	@Override
	protected PolicyType getPolicyType() { return PolicyType.HOME_CA_DP3; }

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-19737")
	public void pas19737_testPPCReportReOrderRule_DP3(@Optional("CA") String state) {
		testPPCReportReOrderRuleCA(getPolicyType());
	}
}