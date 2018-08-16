package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.CA;
import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ModifyPaymentMethodTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_032_Modify_Payment_Method(@Optional("") String state) {
		mainApp().open();
		IPolicy policy = PolicyType.AUTO_SS.get();
		String policyNumber = getPoliciesByQuery("BCT_ONL_032_ModifyPaymentMethod_PaymentPlan", "SelectPolicy").get(0);

		SearchPage.openPolicy(policyNumber);
		deletePendingTransaction(policy);
		policy.endorse().performAndFill(getTestSpecificTD("TestData"));
		if (new ErrorTab().buttonOverride.isPresent()) {
			policy.dataGather().getView().fillFromTo(getTestSpecificTD("TestData_Override"), ErrorTab.class, PurchaseTab.class, false);
		}
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell("Payment Plan")).hasValue(getTestSpecificTD("TestData").getTestData("PremiumAndCoveragesTab").getValue("Payment Plan"));
	}
}
