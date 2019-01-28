package aaa.modules.regression.billing_and_payments.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test CAH Policy Payments
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Create new CA HSS quote.
 * 3. Fill all tabs and navigate to Bind tab. 
 * 4. On Bind tab click Purchase button. 
 * 6. On Purchase screen add Credit Card and EFT payment methods. 
 * 7. Fill AutoPay section.
 * 8. Make 4 payments with all possible methods: 
 * 		Cash, Check, Credit Card, EFT to purchase the policy.
 * 9. Navigate to Billing tab and verify that all deposit payments are displaying. 
 * @details
 */
public class TestPolicyPayments extends HomeCaHO3BaseTest {

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3) 
	public void testPolicyPayments(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD("DataGather", "TestData"), BindTab.class, true);
		new BindTab().submitTab();

		new PurchaseTab().fillTab(getTestSpecificTD("TestData")).submitTab();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

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

		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query)).exists();
	}

}
