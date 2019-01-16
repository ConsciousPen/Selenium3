package aaa.modules.regression.sales.pup;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.UnderwritingAndApprovalTab;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author mlaptsionak
 * @name Capture Underwriting Questions ("C-PU-PUP-CT-446")
 * @scernario
 * 1. Login as an agent (Role F35) and Create a CT HO3 policy and initiate a PUP in Take Action drop down
 * 2. Enter all the mandatory fields in all the tabs and navigate to Underwriting and Approval Tab
 * 3. Validate the Underwriting question
 * "Have any applicants had a prior insurance policy cancelled, refused, or nonrenewed in the past 3 years?" should not be displayed/asked
 */

public class TestCaptureUnderwritingQuestions extends PersonalUmbrellaBaseTest {

	@Parameters({"state"})
	@StateList(states = Constants.States.CT)
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testCaptureUnderwritingQuestions(@Optional("CT") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData testdata = getPolicyDefaultTD();
		PolicyType.PUP.get().initiate();
		policy.getDefaultView().fillUpTo(testdata, UnderwritingAndApprovalTab.class);
		assertThat(new UnderwritingAndApprovalTab().getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED)).isPresent(false);
	}
}
