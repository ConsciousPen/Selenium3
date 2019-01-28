package aaa.modules.regression.sales.auto_ca.select.functional;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.RefundActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test Refund transactions change
 * @scenario
 * 1. Create Customer 
 * 2. Create CA Select Auto Policy
 * 3. Navigate to Billing Tab
 * 4. Generate Future Statement
 * 5. Pay total due amount
 * 6. Refund total due amount
 * 7. Change refund action. Submit change
 * 8. Reject Refund
 * 9. Check that there is no Error 500. get total due
 * @details
 */
public class TestRefundTransactionChange extends AutoCaSelectBaseTest {

	private BillingAccount billingAccount = new BillingAccount();

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.HIGH, Groups.FUNCTIONAL}, description = "Test Refund Action Change")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-22388")
	public void pas22388_testRefundTransactionChange(@Optional("CA") String state) {

		// Testdata for changed payment plan
		TestData policyTD = getPolicyTD().adjust(TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
				AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.QUARTERLY);

		TestData billingTD = testDataManager.billingAccount;

		openAppAndCreatePolicy(policyTD);

		// Navigate to Billing Account Generate Future Statement
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.generateFutureStatement().perform();

		// Pay Total Due. Refund the payment
		Dollar totalDue = new Dollar(BillingSummaryPage.getTotalDue());
		billingAccount.acceptPayment().perform(billingTD.getTestData("AcceptPayment", "TestData_Cash"), totalDue);
		billingAccount.refund().perform(billingTD.getTestData("Refund", "TestData_Cash"), totalDue);

		// Change Refund and submit action
		LocalDateTime refundDueDate = TimeSetterUtil.getInstance().getCurrentTime();
		BillingHelper.changePendingTransaction(refundDueDate, BillingConstants.BillingPendingTransactionsType.REFUND);
		new RefundActionTab().submitTab();

		// Check that there is no Error 500. Just get total due
		BillingHelper.rejectPendingTransaction(refundDueDate, BillingConstants.BillingPendingTransactionsType.REFUND);
		BillingSummaryPage.getTotalDue();
	}
}