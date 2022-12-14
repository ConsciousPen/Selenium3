package aaa.modules.regression.sales.pup.functional;

import java.time.LocalDateTime;
import java.time.Month;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.modules.regression.sales.template.functional.PaymentPlanChangeOnEndorsementTemplate;
import toolkit.utils.TestInfo;

public class TestPaymentPlanChangeOnEndorsement extends PersonalUmbrellaBaseTest {
	private PaymentPlanChangeOnEndorsementTemplate helper = new PaymentPlanChangeOnEndorsementTemplate(getPolicyType());

	//-----------Tests with 'Monthly Low Down' payment plan--------------

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#1) </b>
	 * <p> Steps:
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3. Initiate endorsement for the policy
	 * <p> 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * <p> 5. Calculate Premium and Bind the policy.
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")
	public void pas11785_AC1_monthlyLowDown(@Optional("") String state) {
		TimeSetterUtil.getInstance().confirmDateIsAfter(LocalDateTime.of(2018, Month.JULY, 1, 0, 0));
		String paymentPlan = BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN;
		helper.pas11338_pas11785_AC1(paymentPlan);

	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#2, AC3 endorsement part) </b>
	 * <p> Steps:
	 * <p>      AC#2
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3. Initiate endorsement for the policy to change payment plan
	 * <p> 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * <p> 5. Chang payment to other than 'Low Down' payment plan
	 * <p> 6. Validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 7. Navigate to different tab and then back to Quote Tab
	 * <p> 8. Validate that payment plan as in Step 5 is still selected
	 * <p> 9. Validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 10. Calculate premium, Save and exit.
	 * <p> 11. Re-open Endorsement in data gathering and validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 12. Bind the policy
	 *
	 * <p>      AC3 endorsement part
	 * <p> 13. Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")
	public void pas11785_AC2_AC3_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN;
		helper.pas11338_pas11785_AC2_AC3(paymentPlan);
	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#2 negative) </b>
	 * <p> Steps:
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with OTHER than 'Low Down' payment plan option
	 * <p> 3. Initiate endorsement for the policy
	 * <p> 4. Navigate to Quote tab
	 * <p> 5. Validate that payment plan option is still the same as at NB
	 * <p> 6. Validate that 'Low Down' payment plan options ARE NOT available in dropdown
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")

	public void pas11785_AC2_negative_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.PAY_IN_FULL; //other than Low Down
		helper.pas11338_pas11785_AC2_negative(paymentPlan);

	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#3 renewal part, AC#6) </b>
	 * <p> Steps:
	 * <p>      AC#3
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3. Generate Renewal Image
	 * <p> 4. Retrieve Renewal Image in Data Gathering Mode
	 * <p> 5. Validate that "Payment plan" is changed to MONTHLY_RENEWAL / ELEVEN_PAY_RENEWAL (AC#6). Note:  At NB 'Payment plan at renewal' gets defaulted to MONTHLY_RENEWAL / ELEVEN_PAY_RENEWAL (existing functionality) (AC#6)
	 * <p> 6. Validate that 'Low Down' payment plan option is NOT available in dropdown (AC#3)
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")

	public void pas11785_AC3_AC6_Renewal_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN;
		helper.pas11338_pas11785_AC3_AC6_Renewal(paymentPlan, getTimePoints());
	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#4) </b>
	 * <p> Steps:
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3.Generate renewal Image
	 * <p> 4. Initiate endorsement for the policy to change payment plan
	 * <p> 5. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * <p> 6. Chang payment to other than 'Low Down' payment plan
	 * <p> 7. Validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 8. Navigate to different tab and then back to Quote Tab
	 * <p> 9. Validate that payment plan as in Step 5 is still selected
	 * <p> 10. Validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 11. Calculate premium, Save and exit.
	 * <p> 12. Re-open Endorsement in data gathering and validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 13. Bind the policy
	 * <p> 14. Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")

	public void pas11785_AC4_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN;
		helper.pas11338_pas11785_AC4(paymentPlan, getTimePoints());

	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#5) </b>
	 * <p> Steps:
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3. Generate renewal Image
	 * <p> 4. Initiate endorsement for the current term
	 * <p> 5. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * <p> 6. Calculate Premium and Bind the policy.
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")

	public void pas11785_AC5_monthlyLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN;
		helper.pas11338_pas11785_AC5(paymentPlan, getTimePoints());
	}

	//-----------Tests with 'Eleven Pay Low Down' payment plan--------------

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#1) </b>
	 * <p> Steps:
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3. Initiate endorsement for the policy
	 * <p> 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * <p> 5. Calculate Premium and Bind the policy.
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")
	public void pas11785_AC1_elevenPayLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN;
		helper.pas11338_pas11785_AC1(paymentPlan);
	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#2, AC3 endorsement part) </b>
	 * <p> Steps:
	 * <p>      AC#2
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3. Initiate endorsement for the policy to change payment plan
	 * <p> 4. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * <p> 5. Chang payment to other than 'Low Down' payment plan
	 * <p> 6. Validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 7. Navigate to different tab and then back to Quote Tab
	 * <p> 8. Validate that payment plan as in Step 5 is still selected
	 * <p> 9. Validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 10. Calculate premium, Save and exit.
	 * <p> 11. Re-open Endorsement in data gathering and validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 12. Bind the policy
	 *
	 * <p>      AC3 endorsement part
	 * <p> 13. Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")
	public void pas11785_AC2_AC3_elevenPayLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN;
		helper.pas11338_pas11785_AC2_AC3(paymentPlan);
	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#2 negative) </b>
	 * <p> Steps:
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with OTHER than 'Low Down' payment plan option
	 * <p> 3. Initiate endorsement for the policy
	 * <p> 4. Navigate to Quote tab
	 * <p> 5. Validate that payment plan option is still the same as at NB
	 * <p> 6. Validate that 'Low Down' payment plan options ARE NOT available in dropdown
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")

	public void pas11785_AC2_negative_elevenPayLowDown(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.PAY_IN_FULL; //other than Low Down
		helper.pas11338_pas11785_AC2_negative(paymentPlan);

	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#3 renewal part, AC#6) </b>
	 * <p> Steps:
	 * <p>      AC#3
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3. Generate Renewal Image
	 * <p> 4. Retrieve Renewal Image in Data Gathering Mode
	 * <p> 5. Validate that "Payment plan" is changed to MONTHLY_RENEWAL / ELEVEN_PAY_RENEWAL (AC#6). Note:  At NB 'Payment plan at renewal' gets defaulted to MONTHLY_RENEWAL / ELEVEN_PAY_RENEWAL (existing functionality) (AC#6)
	 * <p> 6. Validate that 'Low Down' payment plan option is NOT available in dropdown (AC#3)
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")

	public void pas11785_AC3_AC6_Renewal_elevenPayLowDow(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN;
		helper.pas11338_pas11785_AC3_AC6_Renewal(paymentPlan, getTimePoints());
	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#4) </b>
	 * <p> Steps:
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3.Generate renewal Image
	 * <p> 4. Initiate endorsement for the policy to change payment plan
	 * <p> 5. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * <p> 6. Chang payment to other than 'Low Down' payment plan
	 * <p> 7. Validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 8. Navigate to different tab and then back to Quote Tab
	 * <p> 9. Validate that payment plan as in Step 5 is still selected
	 * <p> 10. Validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 11. Calculate premium, Save and exit.
	 * <p> 12. Re-open Endorsement in data gathering and validate that 'Low Down' payment plan option still is available in dropdown
	 * <p> 13. Bind the policy
	 * <p> 14. Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")

	public void pas11785_AC4_elevenPayLowDow(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN;
		helper.pas11338_pas11785_AC4(paymentPlan, getTimePoints());

	}

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#5) </b>
	 * <p> Steps:
	 * <p> 1. Create Customer.
	 * <p> 2. Create Home SS policy with 'Low Down' payment plan option
	 * <p> 3.Generate renewal Image
	 * <p> 4. Initiate endorsement for the current term
	 * <p> 5. Validate that "Payment plan" hasn't changed and still is 'Low Down'
	 * <p> 6. Calculate Premium and Bind the policy.
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")

	public void pas11785_AC5_elevenPayLowDow(@Optional("") String state) {
		String paymentPlan = BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN;
		helper.pas11338_pas11785_AC5(paymentPlan, getTimePoints());
	}

	///-----------Test randomly with 'Monthly Low Down' or "Eleven Pay Low Down" payment plan--------------

	/**
	 * @author Maris Strazds
	 * <b> Do not force payment plan change on mid-term changes - SS_HO (AC#2) </b>
	 * <p> Steps:
	 * <p> 1. Create Customer.
	 * <p> 2. Create PUP policy with 'Low Down' payment plan option
	 * <p> 3. Initiate endorsement for the policy
	 * <p> 4. Change payment plan to another Low Down payment plan
	 * <p> 5. Calculate Premium and Bind the policy.
	 * <p> 6. Initiate new Midterm Endorsement
	 * <p> 7. Validate that "Payment Plan" still is selected as in step 4.
	 * <p> 8. Validate that Low Down payment plans still are listed in "Payment Plan" dropdown
	 *
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "2018 Production Defects Delivered via Stories")
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-11338")

	public void pas11338_AC2_randomLowDown(@Optional("") String state) {
		String paymentPlan = helper.getRandomLowDownPaymentPlan();
		helper.pas11338_pas11785_AC2_randomLowDown(paymentPlan);
	}

}
