package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class PasDoc_OnlineBatch_Reinstatement extends AutoSSBaseTest {

	/**
	 * <b> Test PasDoc Scenarios - Reinstatement </b>
	 * <p>  Steps:
	 * <p>  Create policy
	 * <p>  Cancel policy
	 * <p>  Reinstatement policy
	 * <p>  Verify document - AHCWXX(true)
	 *
	 * @author Denis Semenov
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario49(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		PasDocImpl.verifyDocumentsGenerated(policyNumber, AHCWXX);
	}

	/**
	 * <b> Test PasDoc Scenarios - Reinstatement </b>
	 * <p>  Steps:
	 * <p>  Create policy
	 * <p>  Cancel policy
	 * <p>  Reinstatement policy in 10 days
	 * <p>  Verify document - AHCWXX(true)
	 *
	 * @author Denis Semenov
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario50(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData_Plus10Days"));
		PasDocImpl.verifyDocumentsGenerated(policyNumber, AH62XX);
	}

	/**
	 * <b> Test PasDoc Scenarios - Reinstatement </b>
	 * <p>  Steps:
	 * <p>  Create policy  with AutoPay
	 * <p>  Cancel policy
	 * <p>  Reinstatement policy
	 * <p>  Verify document - AH35XX(true)
	 *
	 * @author Denis Semenov
	 * @param state
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario51_1(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		TestData tdAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_EnabledAutoPay").resolveLinks());
		String policyNumber = createPolicy(tdAutoPay);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));

		PasDocImpl.verifyDocumentsGenerated(true, policyNumber, AH35XX);
	}

	/**
	 * <b> Test PasDoc Scenarios - Reinstatement </b>
	 * <p>  Steps:
	 * <p>  Create policy without AutoPay
	 * <p>  Cancel policy
	 * <p>  Reinstatement policy
	 * <p>  Verify document - AH35XX(false)
	 *
	 * @author Denis Semenov
	 * @param state
	 */

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario51_2(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));

		PasDocImpl.verifyDocumentsGenerated(false, policyNumber, AH35XX);
	}
}
