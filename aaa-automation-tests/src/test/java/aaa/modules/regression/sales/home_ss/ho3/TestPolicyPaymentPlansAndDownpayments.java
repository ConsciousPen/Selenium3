package aaa.modules.regression.sales.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Row;

public class TestPolicyPaymentPlansAndDownpayments extends HomeSSHO3BaseTest {

	PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	BindTab bindTab = new BindTab();
	PurchaseTab purchaseTab = new PurchaseTab();

	/**
	 * @author Jurij Kuznecov
	 * <b> Test verify payment calculations for different payment plans </b>
	 * <p> Steps:
	 * <p> 1.  Create new or open existent Customer
	 * <p> 2.  Create a new HO3 policy
	 * <p> 3.  Change payment plan to 'Quarterly' and calculate premium
	 * <p> 4.  Navigate to 'Payment Tab' an verify:
	 * <p> 		- Down payment = Total Premium * <p> 25.0%
	 * <p> 		- Number of payments = 3
	 * <p> 		- Installment amount > 0
	 * <p> 		- Remaining balance due today value = Down payment
	 * <p> 		- Total remaining term premium = Total Premium - Down payment
	 * <p> 5.  Change payment plan to 'Eleven Pay' and calculate premium
	 * <p> 6.  Navigate to 'Payment Tab' an verify:
	 * <p> 		- Down payment = Total Premium * <p> 16.67%
	 * <p> 		- Number of payments = 10
	 * <p> 		- Installment amount > 0
	 * <p> 		- Remaining balance due today value = Down payment
	 * <p> 		- Total remaining term premium = Total Premium - Down payment
	 * <p> 9.  Change payment plan to 'Pay in Full' and calculate premium
	 * <p> 10. Navigate to 'Payment Tab' an verify:
	 * <p> 		- Down payment = Total Premium * <p> 100.00%
	 * <p> 		- Number of payments = 0
	 * <p> 		- Installment amount = 0
	 * <p> 		- Remaining balance due today value = Down payment
	 * <p> 		- Total remaining term premium = Total Premium - Down payment
	 * <p> 11. Change payment plan to 'Mortgagee Bill' and calculate premium
	 * <p> 12. Navigate to 'Payment Tab' an verify:
	 * <p> 		- Down payment = Total Premium * 0.00%
	 * <p> 		- Number of payments = 1
	 * <p> 		- Installment amount > 0
	 * <p> 13. Change payment plan to 'Semi Annual' and calculate premium
	 * <p> 14. Navigate to 'Payment Tab' an verify:
	 * <p> 		- Down payment = Total Premium * <p> 50.00%
	 * <p> 		- Number of payments = 1
	 * <p> 		- Installment amount > 0
	 * <p> 		- Remaining balance due today value = Down payment
	 * <p> 		- Total remaining term premium = Total Premium - Down payment
	 */

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testVerifyPaymentsFiguresForDifferentPaymentPlans(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createQuote();

		// Quarterly
		policy.dataGather().start();
		changePlan(BillingConstants.PaymentPlan.QUARTERLY);
		verifyFigures(25.00, 3, true);

		// Eleven Pay Standard
		policy.dataGather().start();
		changePlan(BillingConstants.PaymentPlan.ELEVEN_PAY);
		verifyFigures(16.67, 10, true);

		// Pay in Full
		policy.dataGather().start();
		changePlan(BillingConstants.PaymentPlan.PAY_IN_FULL);
		verifyFigures(100.00, 0, false);

		// Mortgagee Bill
		policy.dataGather().start();
		changePlan(BillingConstants.PaymentPlan.MORTGAGEE_BILL);
		verifyFigures(0.00, 1, true);

		// Semi Annual
		policy.dataGather().start();
		changePlan(BillingConstants.PaymentPlan.SEMI_ANNUAL);
		verifyFigures(50.00, 1, true);
	}
	/**
	 * @author Jurij Kuznecov
	 * <b> Test the ability to calculate the installment amount when the </b>
	 * <p>       number of installments increases due to a payment plan change
	 * <p> Steps:
	 * <p> 1.  Create new or open existent Customer
	 * <p> 2.  Create a new HO3 policy with payment plan 'Semi Annual'
	 * <p> 3.  Endorse the policy and change payment plan to 'Quarterly'
	 * <p> 4.  Bind policy
	 * <p> 5.  Go to Billing tab
	 * <p> 6.  Verify Number of installments = 3
	 * <p> 7.  Verify that sum of installments values + deposit = term premium
	 * <p> 8.  Verify that deposit value doesn't change
	 * <p> 9.  Endorse the policy and change payment plan to 'Eleven Pay Standard'
	 * <p> 10. Bind policy
	 * <p> 11. Go to Billing tab
	 * <p> 12. Verify Number of installments = 10
	 * <p> 13. Verify that sum of installments values + deposit = term premium
	 * <p> 14. Verify that deposit value doesn't change
	 */

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testChangePaymentPlanLowerToHigherInstallments(@Optional("") String state) {
		int installmentsQuarterly = 3;
		int installmentsElevenPay = 10;

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD().adjust(
			TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.SEMI_ANNUAL));

		BillingSummaryPage.open();

		endorsePolicyWithNewPlanAndVerify(policyNumber, BillingConstants.PaymentPlan.QUARTERLY, installmentsQuarterly);
		endorsePolicyWithNewPlanAndVerify(policyNumber, BillingConstants.PaymentPlan.ELEVEN_PAY, installmentsElevenPay);
	}

	/**
	 * @author Jurij Kuznecov
	 * <b> Test change the minimum required down payment </b>
	 * <p> Steps:
	 * <p> 1. Create new or open existent Customer
	 * <p> 2. Initiate a new HO3 policy creation, fill all mandatory fields and bind quote
	 * <p> 3. In 'Purchase Tab'
	 * <p> 		- Enable "Change Minimum Down Payment" checkbox
	 *		- Enter incorrect value to "Minimum required DownPayment"
	 *			(incorrect value = System calculated downpayment + 50$)
	 * <p> 4. Check that error message appears
	 * <p> 5. Check that 'Apply payment' button is disabled
	 * <p> 6. Enter correct value to "Minimum required DownPayment"
	 * <p> 7. Check that 'Apply payment' button is enabled
	 * <p> 8. Apply payment and check policy status = "Active"
	 */

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testChangeMinimumRequiredDownpayment(@Optional("") String state) {
		String expectedErrorMessage = "Downpayment should not exceed the value of \"Total policy term premium + fees (if any)\"";
		Dollar excessAmount = new Dollar(50);

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		policy.getDefaultView().fillUpTo(getPolicyTD(), PurchaseTab.class);

		Dollar origMinimumRequiredDownPayment = new Dollar(Purchase.remainingBalanceDueToday.getValue());
		purchaseTab.fillTab(getTestSpecificTD("TestData_IncorrectValue").adjust(
			TestData.makeKeyPath(PurchaseTab.class.getSimpleName(), PurchaseMetaData.PurchaseTab.MINIMUM_REQUIRED_DOWNPAYMENT.getLabel()), origMinimumRequiredDownPayment.add(excessAmount).toString())
			.adjust(TestData.makeKeyPath(PurchaseTab.class.getSimpleName(), PurchaseMetaData.PurchaseTab.PAYMENT_ALLOCATION.getLabel(), PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CASH.getLabel()),
				origMinimumRequiredDownPayment.add(excessAmount).toString()));
		assertThat(Purchase.btnApplyPayment).isDisabled();
		assertThat(purchaseTab.getBottomWarning()).valueContains(expectedErrorMessage);
		purchaseTab.fillTab(getTestSpecificTD("TestData_CorrectValue").adjust(
			TestData.makeKeyPath(PurchaseTab.class.getSimpleName(), PurchaseMetaData.PurchaseTab.PAYMENT_ALLOCATION.getLabel(), PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CASH.getLabel()),
			origMinimumRequiredDownPayment.toString()));
		assertThat(Purchase.btnApplyPayment).isEnabled();
		Purchase.btnApplyPayment.click();
		Purchase.confirmPurchase.confirm();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	/**
	 * @author Jurij Kuznecov
	 * <b> Test rewrite determine Downpayment </b>
	 * <p> Steps:
	 * <p> 1.  Create new or open existent Customer
	 * <p> 2.
	 */

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	//@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testRewriteDetermineDownpayment(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();
		policy.copyQuote().perform(getPolicyTD("CopyFromQuote", "TestData"));

		policy.dataGather().start();
		policy.getDefaultView()
			.fill(
				getPolicyTD("CopyFromQuote", "TestData_ChangePlan").adjust(
					TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()),
					BillingConstants.PaymentPlan.ELEVEN_PAY));

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData_NonPaymentOfPremium"));
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataNewNumber"));
		policy.dataGather().start();
		policy.getDefaultView().fillUpTo(
			getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy").adjust(
				TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.QUARTERLY),
			PurchaseTab.class, false);
	}

	private void changePlan(String plan) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel(), ComboBox.class).setValue(plan);
		premiumsAndCoveragesQuoteTab.calculatePremium();
		if (plan.equals(BillingConstants.PaymentPlan.MORTGAGEE_BILL)) {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
			new MortgageesTab().fillTab(getTestSpecificTD("TestData"));
		}
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();
	}

	private void verifyFigures(double percentOfTotalPremium, int numberOfPayments, boolean isMoreThenNull) {
		Dollar premium = new Dollar(Purchase.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.PREMIUM).getValue());
		Dollar downPayment = premium.multiply(percentOfTotalPremium).divide(100.0);
		new Dollar(Purchase.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.MINIMUM_DOWNPAYMENT).getValue()).verify.equals(downPayment);
		assertThat(Purchase.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.NUMBER_OF_REMAINING_INSTALLMENTS)).hasValue(String.valueOf(numberOfPayments));

		if (isMoreThenNull) {
			new Dollar(Purchase.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.INSTALLMENT_AMOUNT).getValue()).verify.moreThan(new Dollar(0));
		} else {
			new Dollar(Purchase.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.INSTALLMENT_AMOUNT).getValue()).verify.equals(new Dollar(0));
		}

		Dollar fee = new Dollar();
		if (getState().equals(Constants.States.NJ)) {
			fee = new Dollar(purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PLIGA_FEE.getLabel(), TextBox.class).getValue());
		}
		Dollar remainingBalance = new Dollar(Purchase.remainingBalanceDueToday.getValue());
		if (remainingBalance.moreThan(new Dollar(0))) {
			remainingBalance = remainingBalance.subtract(fee);
		}
		remainingBalance.verify.equals(downPayment);
		purchaseTab.fillTab(getPolicyTD().ksam(PurchaseTab.class.getSimpleName()));
		Dollar totalRemainingTermPremium = new Dollar(Purchase.totalRemainingTermPremium.getValue());
		if (remainingBalance.equals(new Dollar(0))) {
			totalRemainingTermPremium = totalRemainingTermPremium.subtract(fee);
		}
		totalRemainingTermPremium.verify.equals(premium.subtract(downPayment));

		PurchaseTab.buttonCancel.click();
	}

	private void endorsePolicyWithNewPlanAndVerify(String policyNumber, String plan, int numberOfInstallment) {
		Dollar origDepositAmount = new Dollar(BillingSummaryPage.getInstallmentAmount(BillingSummaryPage.tableInstallmentSchedule.getRow(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION,
				BillingConstants.InstallmentDescription.DEPOSIT).getIndex()));
		BillingSummaryPage.openPolicy(BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM, policyNumber).getIndex());

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		changePlan(plan);
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		HashMap<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.INSTALLMENT);
		assertThat(BillingSummaryPage.tableInstallmentSchedule).hasMatchingRows(numberOfInstallment, query);

		assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION, BillingConstants.InstallmentDescription.DEPOSIT)
				.getCell(BillingConstants.BillingInstallmentScheduleTable.SCHEDULE_DUE_AMOUNT))
				.hasValue(origDepositAmount.toString());

		Dollar totalAmount = new Dollar(0);
		List<Row> installments = BillingSummaryPage.tableInstallmentSchedule.getRows();
		for (Row installment : installments) {
			totalAmount = totalAmount.add(new Dollar(installment.getCell(BillingConstants.BillingInstallmentScheduleTable.SCHEDULE_DUE_AMOUNT).getValue()));
		}
		totalAmount = totalAmount.add(BillingHelper.getFeesValue(policyEffectiveDate));

		BillingSummaryPage.getBillableAmount().verify.equals(totalAmount);
	}
}
