package aaa.modules.e2e.home_ss.ho4;

import aaa.main.enums.BillingConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario8;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario8 extends Scenario8 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_Second_Bill(@Optional("") String state) {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Change_Payment_To_Quarterly(@Optional("") String state) {
		TestData td = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.QUARTERLY, 4);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Generate_Second_Quterly_Bill(@Optional("") String state) {
		generateAndCheckBill(installmentDueDates.get(3), policyEffectiveDate, pligaOrMvleFeeLastTransactionDate);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Second_Quterly_Bill(@Optional("") String state) {
		payAndCheckBill(installmentDueDates.get(3));
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Change_Payment_To_Monthly(@Optional("") String state) {
		TestData td = getTestSpecificTD("TestData_Endorsement2").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.ELEVEN_PAY, 9);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Forth_Monthly_Bill(@Optional("") String state) {
		generateAndCheckBill(installmentDueDates.get(4), policyEffectiveDate, pligaOrMvleFeeLastTransactionDate);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Pay_Forth_Monthly_Bill(@Optional("") String state) {
		payAndCheckBill(installmentDueDates.get(4));
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Generate_Five_Monthly_Bill(@Optional("") String state) {
		generateAndCheckBill(installmentDueDates.get(5), policyEffectiveDate, null);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Change_Payment_To_Semi_Annaual(@Optional("") String state) {
		TestData td = getTestSpecificTD("TestData_Endorsement3").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.SEMI_ANNUAL, 5);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Generate_Semi_Annual_Bill(@Optional("") String state) {
		generateAndCheckBill(installmentDueDates.get(6), policyEffectiveDate, null);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Pay_Semi_Annual_Bill(@Optional("") String state) {
		payAndCheckBill(installmentDueDates.get(6));
	}

	/**
	 * TODO Add Test Change current Payment plan
	 */

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Image_Generation(@Optional("") String state) {
		super.renewalImageGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Preview_Generation(@Optional("") String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Change_Payment_In_Renewal_To_Monthly(@Optional("") String state) {
		TestData td = getTestSpecificTD("TestData_Endorsement4").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		/** TODO Why 5??? */
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.SEMI_ANNUAL, true, true, 5, policyEffectiveDate);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Change_Payment_In_Policy_To_Quarterly(@Optional("") String state) {
		TestData td = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		/** TODO Why 5??? */
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.QUARTERLY, false, true, 5, policyEffectiveDate);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Renewal_Offer_Generation(@Optional("") String state) {
		super.renewalOfferGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Change_Payment_Plan_To_Quarterly(@Optional("") String state) {
		TestData td = getTestSpecificTD("TestData_Endorsement5").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL, true, false, 9, policyExpirationDate);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Renewal_Premium_Notice(@Optional("") String state) {
		super.renewalPremiumNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Change_Payment_Plan_To_Monthly(@Optional("") String state) {
		TestData td = getTestSpecificTD("TestData_Endorsement4").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		super.changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.ELEVEN_PAY_RENEWAL, true, false, 16, policyExpirationDate);
	}
}
