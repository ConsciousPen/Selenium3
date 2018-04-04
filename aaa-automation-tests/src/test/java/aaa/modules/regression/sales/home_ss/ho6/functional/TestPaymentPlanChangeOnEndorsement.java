package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.modules.regression.sales.home_ss.helper.HelperTestPaymentPlanChangeOnEndorsement;
import toolkit.utils.TestInfo;

public class TestPaymentPlanChangeOnEndorsement extends HomeSSHO6BaseTest {

	private HelperTestPaymentPlanChangeOnEndorsement helper = new HelperTestPaymentPlanChangeOnEndorsement();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();

	//-----------Tests with 'Monthly Low Down' payment plan--------------

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#1)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3. Initiate endorsement for the policy
	 * 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * 5. Calculate Premium and Bind the policy.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")
	public void pas11338_AC1_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY;//TODO-mstrazds:change to 'Monthly Low Down'
		helper.pas11338_AC1(getPolicyType(), paymentPlan);
	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#2, AC3 endorsement part)
	 * @scenario
	 *      AC#2
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3. Initiate endorsement for the policy to change payment plan
	 * 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * 5. Chang payment to other than 'Low Down' payment plan
	 * 6. Validate that 'Low Down' payment plan option still is available in dropdown
	 * 7. Navigate to different tab and then back to Quote Tab
	 * 8. Validate that payment plan as in Step 5 is still selected
	 * 9. Validate that 'Low Down' payment plan option still is available in dropdown
	 * 10. Calculate premium, Save and exit.
	 * 11. Re-open Endorsement in data gathering and validate that 'Low Down' payment plan option still is available in dropdown
	 * 12. Bind the policy
	 *
	 *      AC3 endorsement part
	 * 13. Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-11338")
	public void pas11338_AC2_AC3_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY;//TODO-mstrazds:change to 'Monthly Low Down'
		helper.pas11338_AC2_AC3(getPolicyType(), paymentPlan);
	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#2 negative)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Home SS policy with OTHER than 'Low Down' payment plan option
	 * 3. Initiate endorsement for the policy
	 * 4. Navigate to Quote tab
	 * 5. Validate that payment plan option is still the same as at NB
	 * 6. Validate that 'Low Down' payment plan options ARE NOT available in dropdown
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")

	public void pas11338_AC2_negative_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.PAY_IN_FULL; //other than Low Down
		helper.pas11338_AC2_negative(getPolicyType(), paymentPlan);

	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#3 renewal part, AC#6)
	 * @scenario
	 *      AC#3
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3. Generate Renewal Image
	 * 4. Retrieve Renewal Image in Data Gathering Mode
	 * 5. Validate that "Payment plan" is changed to "Monthly" (AC#6)//TODO-mstrazds:clarify correct plan
	 * 6. Validate that 'Low Down' payment plan option is NOT available in dropdown (AC#3)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")

	public void pas11338_AC3_AC6_Renewal_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.PAY_IN_FULL;//TODO-mstrazds:change to 'Monthly Low Down'
		helper.pas11338_AC3_AC6_Renewal(getPolicyType(), paymentPlan, getTimePoints());
	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#4)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3.Generate renewal Image
	 * 4. Initiate endorsement for the policy to change payment plan
	 * 5. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * 6. Chang payment to other than 'Low Down' payment plan
	 * 7. Validate that 'Low Down' payment plan option still is available in dropdown
	 * 8. Navigate to different tab and then back to Quote Tab
	 * 9. Validate that payment plan as in Step 5 is still selected
	 * 10. Validate that 'Low Down' payment plan option still is available in dropdown
	 * 11. Calculate premium, Save and exit.
	 * 12. Re-open Endorsement in data gathering and validate that 'Low Down' payment plan option still is available in dropdown
	 * 13. Bind the policy
	 * 14. Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")

	public void pas11338_AC4_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY;//TODO-mstrazds:change to 'Monthly Low Down'
		helper.pas11338_AC4(getPolicyType(), paymentPlan, getTimePoints());

	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#5)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3. Initiate endorsement for the policy
	 * 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * 5. Calculate Premium and Bind the policy.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")

	public void pas11338_AC5_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY;//TODO-mstrazds:change to 'Monthly Low Down'
		helper.pas11338_AC5(getPolicyType(), paymentPlan, getTimePoints());
	}

	//-----------Tests with 'Eleven Pay Low Down' payment plan--------------

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#1)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3. Initiate endorsement for the policy
	 * 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * 5. Calculate Premium and Bind the policy.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")
	public void pas11338_AC1_elevenPayLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY;//TODO-mstrazds:change to 'Eleven Pay Low Down'
		helper.pas11338_AC1(getPolicyType(), paymentPlan);
	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#2, AC3 endorsement part)
	 * @scenario
	 *      AC#2
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3. Initiate endorsement for the policy to change payment plan
	 * 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * 5. Chang payment to other than 'Low Down' payment plan
	 * 6. Validate that 'Low Down' payment plan option still is available in dropdown
	 * 7. Navigate to different tab and then back to Quote Tab
	 * 8. Validate that payment plan as in Step 5 is still selected
	 * 9. Validate that 'Low Down' payment plan option still is available in dropdown
	 * 10. Calculate premium, Save and exit.
	 * 11. Re-open Endorsement in data gathering and validate that 'Low Down' payment plan option still is available in dropdown
	 * 12. Bind the policy
	 *
	 *      AC3 endorsement part
	 * 13. Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")
	public void pas11338_AC2_AC3_elevenPayLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY;//TODO-mstrazds:change to 'Eleven Pay Low Down'
		helper.pas11338_AC2_AC3(getPolicyType(), paymentPlan);
	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#2 negative)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Home SS policy with OTHER than 'Low Down' payment plan option
	 * 3. Initiate endorsement for the policy
	 * 4. Navigate to Quote tab
	 * 5. Validate that payment plan option is still the same as at NB
	 * 6. Validate that 'Low Down' payment plan options ARE NOT available in dropdown
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")

	public void pas11338_AC2_negative_elevenPayLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.PAY_IN_FULL; //other than Low Down
		helper.pas11338_AC2_negative(getPolicyType(), paymentPlan);

	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#3 renewal part, AC#6)
	 * @scenario
	 *      AC#3
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3. Generate Renewal Image
	 * 4. Retrieve Renewal Image in Data Gathering Mode
	 * 5. Validate that "Payment plan" is changed to "Monthly" (AC#6)//TODO-mstrazds:clarify correct plan
	 * 6. Validate that 'Low Down' payment plan option is NOT available in dropdown (AC#3)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")

	public void pas11338_AC3_AC6_Renewal_elevenPayLowDow(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.PAY_IN_FULL;//TODO-mstrazds:change to 'Eleven Pay Low Down'
		helper.pas11338_AC3_AC6_Renewal(getPolicyType(), paymentPlan, getTimePoints());
	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#4)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3.Generate renewal Image
	 * 4. Initiate endorsement for the policy to change payment plan
	 * 5. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * 6. Chang payment to other than 'Low Down' payment plan
	 * 7. Validate that 'Low Down' payment plan option still is available in dropdown
	 * 8. Navigate to different tab and then back to Quote Tab
	 * 9. Validate that payment plan as in Step 5 is still selected
	 * 10. Validate that 'Low Down' payment plan option still is available in dropdown
	 * 11. Calculate premium, Save and exit.
	 * 12. Re-open Endorsement in data gathering and validate that 'Low Down' payment plan option still is available in dropdown
	 * 13. Bind the policy
	 * 14. Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")

	public void pas11338_AC4_elevenPayLowDow(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY;//TODO-mstrazds:change to 'Eleven Pay Low Down'
		helper.pas11338_AC4(getPolicyType(), paymentPlan, getTimePoints());

	}

	/**
	 * @author Maris Strazds
	 * @name Do not force payment plan change on mid-term changes - SS_HO (AC#5)
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Home SS policy with 'Low Down' payment plan option
	 * 3. Initiate endorsement for the policy
	 * 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * 5. Calculate Premium and Bind the policy.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-11338")

	public void pas11338_AC5_elevenPayLowDow(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY;//TODO-mstrazds:change to 'Eleven Pay Low Down'
		helper.pas11338_AC5(getPolicyType(), paymentPlan, getTimePoints());
	}

}
