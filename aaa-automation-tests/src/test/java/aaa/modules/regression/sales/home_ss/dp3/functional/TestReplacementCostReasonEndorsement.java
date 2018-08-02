package aaa.modules.regression.sales.home_ss.dp3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.modules.regression.sales.template.functional.TestCarryOverValuesTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestReplacementCostReasonEndorsement extends HomeSSDP3BaseTest {

	private TestCarryOverValuesTemplate template = new TestCarryOverValuesTemplate();

	/**
	 * @author Dominykas Razgunas
	 * @name Test Replacement Cost Value Carry over from Renewal
	 * @scenario
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Replacement Cost Value Carry over from Renewal")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-15831")
	public void pas15831_TestReplacementCostReasonEndorsementOnRenewal(@Optional("") String state) {

		template.pas15831_TestReplacementCostReasonEndorsementOnRenwal(getPolicyType());
	}
}