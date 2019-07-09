package aaa.modules.regression.service.auto_ca.choice.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesDriversCAHelper;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestMiniServicesDriver extends TestMiniServicesDriversCAHelper {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	/**
	 * @author Maris Strazds
	 * @name Default Marital Status - state deviations for multiple married statuses
	 * @scenario1
	 * 1. Create policy on Pas with the FNI set to non-married status
	 * 2. Create endorsement outside of PAS
	 * 3. Add driver.
	 * 4. Validate the driver's metadata allows all marital statuses.
	 * 5. Set the driver to a married relationship.
	 * 6. Validate the driver's metadata restricts marital statuses.
	 * 7. Update the driver's marital status to a married status.
	 * 8. Validate that the FNI marital status has been updated accordingly.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-15896"})
	public void pas15896_NamedInsuredMaritalStatus_MultipleMarital(@Optional("CA") String state) {
		assertSoftly(softly ->
				pas16548_NamedInsuredMaritalStatus_MultipleMaritalBody()
		);
	}

	/**
	 * @author Maris Strazds
	 * @name Named Insured and the Relationship to the FNI - FNI is Equivalent to Married (Married or Domestic Partner)
	 * @scenario1
	 * 1. Create policy on Pas with the FNI set to Married or Equivalent to Married (Married or Domestic Partner)
	 * 2. Create endorsement outside of PAS
	 * 3. Add driver, update like Spouse or Domestic Partner
	 * 4. Verify that marital statuses for the newly added driver is the same as the FNI
	 * AND the driver is added as a Named Insured
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-15896"})
	public void pas15896_NamedInsuredAndTheRelationshipWhenFniEquivalentToMarried(@Optional("CA") String state) {
		pas16610_NamedInsuredAndTheRelationshipWhenFniEquivalentToMarriedBody();
	}
}
