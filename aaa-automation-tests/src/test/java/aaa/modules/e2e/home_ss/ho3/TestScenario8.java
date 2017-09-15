package aaa.modules.e2e.home_ss.ho3;

import aaa.main.enums.BillingConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario8;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario8 extends Scenario8 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_Second_Bill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Change_Payment_To_Quarterly() {
		TestData td = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.QUARTERLY, 4);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Generate_Second_Quterly_Bill() {
		generateAndCheckBill(installmentDueDates.get(3), policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Second_Quterly_Bill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Change_Payment_To_Monthly() {
		TestData td = getTestSpecificTD("TestData_Endorsement2").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.ELEVEN_PAY, 9);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Forth_Monthly_Bill() {
		generateAndCheckBill(installmentDueDates.get(4), policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Pay_Forth_Monthly_Bill() {
		payAndCheckBill(installmentDueDates.get(4));
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Generate_Five_Monthly_Bill() {
		generateAndCheckBill(installmentDueDates.get(5), policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Change_Payment_To_Semi_Annaual() {
		TestData td = getTestSpecificTD("TestData_Endorsement3").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.SEMI_ANNUAL, 5);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Generate_Semi_Annual_Bill() {
		generateAndCheckBill(installmentDueDates.get(6), policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Pay_Semi_Annual_Bill() {
		payAndCheckBill(installmentDueDates.get(6));
	}

	/**
	 * TODO Add Test Change current Payment plan
	 */

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Image_Generation() {
		super.renewalImageGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Preview_Generation() {
		super.renewalPreviewGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Change_Payment_In_Renewal_To_Monthly() {
		TestData td = getTestSpecificTD("TestData_Endorsement4").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		/** TODO Why 5??? */
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.SEMI_ANNUAL, true, true, 5, policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Change_Payment_In_Policy_To_Quarterly() {
		TestData td = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		/** TODO Why 5??? */
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.QUARTERLY, false, true, 5, policyEffectiveDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Renewal_Offer_Generation() {
		super.renewalOfferGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Change_Payment_Plan_To_Quarterly() {
		TestData td = getTestSpecificTD("TestData_Endorsement5").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL, true, false, 9, policyExpirationDate);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Renewal_Premium_Notice() {
		super.renewalPremiumNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Change_Payment_Plan_To_Monthly() {
		TestData td = getTestSpecificTD("TestData_Endorsement4").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.ELEVEN_PAY_RENEWAL, true, false, 16, policyExpirationDate);
	}
}
