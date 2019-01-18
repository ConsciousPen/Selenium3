package aaa.modules.regression.billing_and_payments.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.template.functional.TestRefundProcessTemplate;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestRefundProcess extends PolicyBaseTest implements TestRefundProcessPreConditions {
	private TestRefundProcessTemplate testRefundProcessTemplate = new TestRefundProcessTemplate(PolicyType.AUTO_CA_SELECT);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Test(description = "Precondition for TestRefundProcess tests", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public void precondJobAdding() {
		testRefundProcessTemplate.precondJobAdding();
	}

	@Test(description = "Precondition for TestRefundProcess tests", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void pendingRefundPaymentMethodConfigCheck() {
		assertThat(DBService.get().getValue(PENDING_REFUND_PAYMENT_METHOD_CONFIG_CHECK))
				.as("The configuration is missing, run pendingRefundConfigurationUpdate and restart the env.").isEqualTo("pendingRefund");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_SELECT, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsCC(@org.testng.annotations.Optional("CA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingManualRefundsCC(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_SELECT, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsACH(@org.testng.annotations.Optional("CA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingManualRefundsACH(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_SELECT, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsCC(@org.testng.annotations.Optional("CA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingAutomatedRefundsCC(getState());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_SELECT, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsACH(@org.testng.annotations.Optional("CA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingAutomatedRefundsACH(getState());
	}

	@AfterSuite(alwaysRun = true)
	private void deleteMultipleLastPaymentRequests() {
		testRefundProcessTemplate.deleteMultiplePaperlessPreferencesRequests();
	}

}
