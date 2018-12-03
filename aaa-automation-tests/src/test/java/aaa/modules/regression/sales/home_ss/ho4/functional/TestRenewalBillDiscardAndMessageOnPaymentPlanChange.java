package aaa.modules.regression.sales.home_ss.ho4.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestRenewalBillDiscardAndMsgOnPaymentPlanChangeTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestRenewalBillDiscardAndMessageOnPaymentPlanChange extends TestRenewalBillDiscardAndMsgOnPaymentPlanChangeTemplate {

	@Override
	protected PolicyType getPolicyType() { return PolicyType.HOME_SS_HO4; }

	///-----------Payment plan: Quarterly -> Semi-Annual, Not on Automatic Payment, Bill generated via scheduler job --------------
	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Quarterly' payment plan (Not on Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. Move time to R-20
	 * 6. Create Bill via job
	 * 7. Check that Bill was generated in DB
	 * 8. On Renewal change payment plan to 'Semi-Annual'
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * 12. Click Ok
	 * 13. Go to Billing page
	 * 14. Check that original bill is discarded and a new bill is displayed on the Billing tab
	 * 15. Check that another Bill on Renewal is not generated in DB
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-16405, PAS-16526")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_QuarterlyToSemiAnnual(@Optional("") String state) {

		testRenewalBillDiscardAndMessageOnPaymentPlanChange(BillingConstants.PaymentPlan.QUARTERLY,
				false, false, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL,
				notAutomaticPaymentMessage, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
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
	 * 5. Move time to R-20
	 * 6. Create Bill via job
	 * 7. Check that Bill was generated in DB
	 * 8. On Renewal change payment plan to 'Quarterly'
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * 12. Click Ok
	 * 13. Go to Billing page
	 * 14. Check that original bill is discarded and a new bill is displayed on the Billing tab
	 * 15. Check that another Bill on Renewal is not generated in DB
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-16405, PAS-16526")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_SemiAnnualToQuarterly(@Optional("") String state) {

		testRenewalBillDiscardAndMessageOnPaymentPlanChange(BillingConstants.PaymentPlan.SEMI_ANNUAL,
				false, false, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL,
				notAutomaticPaymentMessage, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
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
	 * 5. Move time to R-20
	 * 6. Navigate to Billing page of the Policy
	 * 7. Generate Paper Bill manually
	 * 8. On Renewal change payment plan to 'Semi-Annual'
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * 12. Click Ok
	 * 13. Go to Billing page
	 * 14. Check that original bill is discarded and a new bill is displayed on the Billing tab
	 * 15. Check that another Bill on Renewal is not generated in DB
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-16405, PAS-16526")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_AutoPay_ManualBillGeneration(@Optional("") String state) {

		testRenewalBillDiscardAndMessageOnPaymentPlanChange(BillingConstants.PaymentPlan.QUARTERLY,
				true, true, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL,
				automaticPaymentMessage, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
	}
}
