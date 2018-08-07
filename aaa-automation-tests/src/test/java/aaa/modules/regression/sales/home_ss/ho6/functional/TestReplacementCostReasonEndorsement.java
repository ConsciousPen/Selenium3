package aaa.modules.regression.sales.home_ss.ho6.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.modules.regression.sales.template.functional.TestCarryOverValuesTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(statesExcept = {Constants.States.CA})
public class TestReplacementCostReasonEndorsement extends HomeSSHO6BaseTest {

	private TestCarryOverValuesTemplate template = new TestCarryOverValuesTemplate();

	/**
	 * @author Dominykas Razgunas
	 * @name Test Replacement Cost Value Carry over from Renewal
	 * @scenario
	 * 1. Create Individual Customer
	 * 2. Create Property Policy
	 * 3. Initiate Renewal
	 * 4. Navigate To Property Info Tab
	 * 5. Select Replacement Reason Cost = Renewal
	 * 6. Issue Renewal
	 * 7. Purchase Renewal
	 * 8. Endorse Renewal
	 * 9. Navigate to Property Info Tab
	 * 10. Assert that Replacement Reason Cost  = Renewal
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Replacement Cost Value Carry over from Renewal")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-15831")
	public void pas15831_TestReplacementCostReasonEndorsementOnRenewal(@Optional("") String state) {

		template.pas15831_TestReplacementCostReasonEndorsementOnRenewal(getPolicyType());
	}
}