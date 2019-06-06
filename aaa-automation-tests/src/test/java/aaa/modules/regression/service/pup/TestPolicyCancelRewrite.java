package aaa.modules.regression.service.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;


/**
 * @author Dmitry Kozakevich
 * <b> Test Cancel and Rewrite Personal Umbrella Policy </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create Personal Umbrella Policy
 * <p> 3. Verify that Policy status is "Policy Active"
 * <p> 4. Cancel the policy created in step 2
 * <p> 5. Verify that Policy status is "Cancelled"
 * <p> 6. Initiate Rewrite action for the policy cancelled in step 5
 * <p> 7. Verify that status of the Quote is "Gathering Info"
 * <p> 8. Issue the Quote from the step 7
 * <p> 9. Verify that Policy status is "Policy Active"
 * <p> 10. Verify that Policy Number of the initial policy is not the same as for the rewritten policy
 */
public class TestPolicyCancelRewrite extends PersonalUmbrellaBaseTest {

	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void testPolicyCancelRewrite(@Optional("") String state) {
		mainApp().open();

        createCustomerIndividual();
        createPolicy();
        
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String initialPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Initial Policy Number: " + initialPolicyNumber);

		policy.cancel().perform(getPolicyTD("Cancellation", "TestDataRewrite"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Policy " + initialPolicyNumber + " is cancelled");

		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameNumber"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);

		policy.calculatePremiumAndPurchase(getPolicyTD("DataGather", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String rewrittenPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Rewritten Policy Number: " + rewrittenPolicyNumber);

		assertThat(rewrittenPolicyNumber).as("Rewritten Policy Number %s is the same as Initial Policy Number %s", initialPolicyNumber, rewrittenPolicyNumber)
				.isNotEqualTo(initialPolicyNumber);
	}
}
