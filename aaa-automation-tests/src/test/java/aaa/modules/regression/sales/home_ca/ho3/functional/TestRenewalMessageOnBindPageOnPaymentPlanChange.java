package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.regression.sales.template.functional.TestRenewalMsgOnBindPageOnPaymentPlanChangeTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestRenewalMessageOnBindPageOnPaymentPlanChange extends HomeCaHO3BaseTest {

	TestRenewalMsgOnBindPageOnPaymentPlanChangeTemplate template = new TestRenewalMsgOnBindPageOnPaymentPlanChangeTemplate();

	///-----------Payment plan: Quarterly -> Semi-Annual, Not on Automatic Payment, Bill generated via scheduler job --------------
	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Quarterly' payment plan (Not on Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. On Renewal change payment plan to 'Semi-Annual'
	 * 6. Go to Bind Page
	 * 7. Click Propose
	 * 8. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-16405, PAS-16526, PAS-16883")
	public void testRenewalMessageOnBindPageOnPaymentPlanChange_QuarterlyToSemiAnnual(@Optional("") String state) {

		template.testRenewalMessageOnBindPageOnPaymentPlanChange(getPolicyType(), BillingConstants.PaymentPlan.QUARTERLY,
				false, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL, template.notAutomaticPaymentMessage,
				BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
	}

	///-----------Payment plan: Semi-Annual -> Quarterly, Not on Automatic Payment, Bill generated via scheduler job --------------

	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Semi-Annual' payment plan (Not on Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. On Renewal change payment plan to 'Quarterly'
	 * 6. Go to Bind Page
	 * 7. Click Propose
	 * 8. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-16405, PAS-16526, PAS-16883")
	public void testRenewalMessageOnBindPageOnPaymentPlanChange_SemiAnnualToQuarterly(@Optional("") String state) {

		template.testRenewalMessageOnBindPageOnPaymentPlanChange(getPolicyType(), BillingConstants.PaymentPlan.SEMI_ANNUAL,
				false, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL, template.notAutomaticPaymentMessage,
				BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
	}

	///-----------Payment plan: Quarterly -> Mortgagee Bill, Not on Automatic Payment, Bill generated via scheduler job --------------

	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Quarterly' payment plan (Not on Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. On Renewal change payment plan to 'Mortgagee Bill'
	 * 6. Go to 'Mortgagee & Additional interests' tab
	 * 7. Set Mortgagee radio button to - Yes
	 * 8. Fill all information
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-16405, PAS-16526, PAS-16883")
	public void testRenewalMessageOnBindPageOnPaymentPlanChange_QuarterlyToMortgageeBill(@Optional("") String state) {

		template.testRenewalMessageOnBindPageOnPaymentPlanChange(getPolicyType(), BillingConstants.PaymentPlan.QUARTERLY,
				false, BillingConstants.PaymentPlan.MORTGAGEE_BILL_RENEWAL, template.billToInsuredToBillToMortgageeMessage,
				BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
	}

	///-----------Payment plan changed: Mortgagee Bill - >Monthly, Not on Automatic Payment, Bill generated via scheduler job --------------

	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Mortgagee Bill' payment plan (Not on Automatic Payment)
	 * 3. Go to 'Mortgagee & Additional interests' tab
	 * 4. Set Mortgagee radio button to - Yes
	 * 5. Fill all information
	 * 6. Move time to R-35
	 * 7. Check that Bill was generated in DB
	 * 8. On Renewal change payment plan to 'Monthly'
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-16405, PAS-16526, PAS-16883")
	public void testRenewalMessageOnBindPageOnPaymentPlanChange_MortgageeBillToMonthly(@Optional("") String state) {

		template.testRenewalMessageOnBindPageOnPaymentPlanChange(getPolicyType(), BillingConstants.PaymentPlan.MORTGAGEE_BILL,
				false, BillingConstants.PaymentPlan.MONTHLY_STANDARD_RENEWAL, template.billToMortgageeToBillToInsuredMessage,
				BillingConstants.PaymentPlan.MORTGAGEE_BILL_RENEWAL);
	}

	///-----------Payment plan: Quarterly -> Semi-Annual,On Automatic Payment, Bill generated manually --------------
	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Quarterly' payment plan (On Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. On Renewal change payment plan to 'Semi-Annual'
	 * 6. Go to Bind Page
	 * 7. Click Propose
	 * 8. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-16405, PAS-16526, PAS-16883")
	public void testRenewalMessageOnBindPageOnPaymentPlanChange_AutoPay(@Optional("") String state) {

		template.testRenewalMessageOnBindPageOnPaymentPlanChange(getPolicyType(), BillingConstants.PaymentPlan.QUARTERLY,
				true, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL, template.automaticPaymentMessage,
				BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
	}
}
