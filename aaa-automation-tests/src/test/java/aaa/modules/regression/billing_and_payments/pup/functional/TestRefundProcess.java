package aaa.modules.regression.billing_and_payments.pup.functional;

import java.io.IOException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.helpers.RefundProcessHelper;
import aaa.modules.regression.billing_and_payments.template.functional.TestRefundProcessTemplate;
import toolkit.utils.TestInfo;

public class TestRefundProcess extends PolicyBaseTest implements TestRefundProcessPreConditions {

	private static final String PENDING_REFUND_AMOUNT = "1000";
	private static final String APPROVED_REFUND_AMOUNT = "999.99";
	private RefundProcessHelper refundProcessHelper = new RefundProcessHelper();
	private TestRefundProcessTemplate testRefundProcessTemplate = new TestRefundProcessTemplate(PolicyType.PUP);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Test(description = "Precondition for TestRefundProcess tests")
	public void precondJobAdding() {
		testRefundProcessTemplate.precondJobAdding();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7039", "PAS-7196", "PAS-450"})
	public void pas7039_newDataElementsDeceasedYes(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		testRefundProcessTemplate.pas7039_newDataElementsDeceasedYes(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7039", "PAS-7196", "PAS-450"})
	public void pas7039_newDataElementsDeceasedNo(@Optional("VA") String state) throws SftpException, JSchException, IOException {
		testRefundProcessTemplate.pas7039_newDataElementsDeceasedNo(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsCC(@Optional("VA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingManualRefundsCC(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsCheck(@Optional("VA") String state) {

		String paymentMethod = "Check";

		preconditionPolicyCreationPup();

		refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsCC(@Optional("VA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingAutomatedRefundsCC(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.PUP, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsCheck(@Optional("VA") String state) {

		String paymentMethod = "Check";

		String policyNumber = preconditionPolicyCreationPup();

		refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, getTimePoints());
	}

	private String preconditionPolicyCreationPup() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("policyNumber: {}", policyNumber);
		return policyNumber;
	}

	@AfterSuite(alwaysRun = true)
	private void deleteMultipleLastPaymentRequests() {
		testRefundProcessTemplate.deleteMultiplePaperlessPreferencesRequests();
	}

}
