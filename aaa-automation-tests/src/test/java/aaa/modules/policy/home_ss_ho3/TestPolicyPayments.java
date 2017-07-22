package aaa.modules.policy.home_ss_ho3;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test Policy Payments
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Create new HSS quote.
 * 3. Fill all tabs and navigate to Bind tab. 
 * 4. On Bind tab click Purchase button. 
 * 6. On Purchase screen add Credit Card and EFT payment methods. 
 * 7. Fill AutoPay section.
 * 8. Make 4 payments with all possible methods: 
 * 		Cash, Check, Credit Card, EFT to purchase the policy.
 * 9. Navigate to Billing tab and verify that all deposit payments are displaying. 
 * @details
 */

public class TestPolicyPayments extends HomeSSBaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyPayments() {
		mainApp().open();

		createCustomerIndividual();

		TestData tdPolicy = testDataManager.policy.get(getPolicyType());

		policy.initiate();
		policy.getDefaultView().fillUpTo(getStateTestData(tdPolicy, "DataGather", "TestData"), BindTab.class, true);
		new BindTab().submitTab();

		new PurchaseTab().fillTab(tdPolicy.getTestData(this.getClass().getSimpleName(), "TestData")).submitTab();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		log.info("TEST: Payments for HSS Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

		BillingSummaryPage.open();

		Dollar depositAmount = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getColumn(11).getCell(1).getValue()).subtract(new Dollar(100));

		checkPaymentIsDisplaying(depositAmount.toString());
		checkPaymentIsDisplaying("$50.00");
		checkPaymentIsDisplaying("$40.00");
		checkPaymentIsDisplaying("$10.00");

	}

	private void checkPaymentIsDisplaying(String amount) {

		Map<String, String> query = new HashMap<>();
		query.put("Type", "Payment");
		query.put("Subtype/Reason", "Deposit Payment");
		query.put("Amount", "(" + amount + ")");

		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query).verify.present();
	}
}
