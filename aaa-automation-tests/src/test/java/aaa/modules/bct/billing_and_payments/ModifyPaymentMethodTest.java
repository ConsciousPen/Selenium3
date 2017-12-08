package aaa.modules.bct.billing_and_payments;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.impl.SimpleDataProvider;

public class ModifyPaymentMethodTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_032_Modify_Payment_Method(@Optional("") String state) {
		mainApp().open();
		IPolicy policy = PolicyType.AUTO_SS.get();
		String policyNumber = getPoliciesByQuery("BCT_ONL_032_ModifyPaymentMethod_PaymentPlan", "SelectPolicy").get(0);

		SearchPage.openPolicy(policyNumber);
		if (PolicySummaryPage.buttonPendedEndorsement.isEnabled()) {
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.deletePendedTransaction().perform(new SimpleDataProvider());
		}
		policy.endorse().performAndFill(getTestSpecificTD("TestData"));
		if (new ErrorTab().buttonOverride.isPresent()) {
			policy.dataGather().getView().fillFromTo(getTestSpecificTD("TestData_Override"), ErrorTab.class, PurchaseTab.class, false);
		}
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell("Payment Plan").verify.value(BillingConstants.PaymentPlan.MONTHLY_RENEWAL);

	}
}
