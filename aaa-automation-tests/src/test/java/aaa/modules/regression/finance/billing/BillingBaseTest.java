package aaa.modules.regression.finance.billing;

import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class BillingBaseTest extends PolicyBaseTest {

	BillingAccount billingAccount = new BillingAccount();
	TestData tdBilling = testDataManager.billingAccount;

	public void makeInstallmentPayment(LocalDateTime paymentDate, String policyNumber, int paymentAdjustment) {
		makeInstallmentPayment(paymentDate, policyNumber, new Dollar(paymentAdjustment));

	}

	public void makeInstallmentPayment
			(LocalDateTime paymentDate, String policyNumber, Dollar paymentAdjustment) {
		TimeSetterUtil.getInstance().nextPhase(paymentDate);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		billingAccount.generateFutureStatement().perform();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Check"),
				BillingSummaryPage.getMinimumDue().add(paymentAdjustment));
	}
}
