package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class ModifyPaymentMethodTest extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
	/**
	 * @author Deloite
	 * @name Update Preferred Payment method
	 * @scenario
	 * @param state
	 * 1. System displays the following Payment Method to apply Minimum Required Down payment:
	 * a.Cash
	 * b.Check
	 * "mid-term, update from direct to recurring using credit card - CC payment method details stored in account
	 * Check:
	 * 1. Following transaction is created and displayed in the Payments & Other Transactions section
	 *     Transaction 1
	 *     Type = Fee
	 *     Subtype = 'Subtype' of the transaction selected
	 *     Amount= Value entered by the user
	 *     Status = Applied
	 * 2. Total Amount due is increased by the
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_032_Modify_Payment_Method(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

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
