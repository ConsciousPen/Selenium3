package aaa.modules.e2e.home_ss.ho4;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario8;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario8 extends Scenario8 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.OK, States.UT})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateAndCheckBill(installmentDueDates.get(1), softly);
			TestData td = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
			changePaymentPlanAndCheckInstallments(td, BillingConstants.PaymentPlan.QUARTERLY, 4);
			//if (getState().equals(Constants.States.AZ) || getState().equals(Constants.States.NJ) || getState().equals(Constants.States.UT)) {
			//	generateAndCheckBillAfterEndorsement(installmentDueDates.get(3), policyEffectiveDate, softly);
			//} else {
				generateAndCheckBill(installmentDueDates.get(3), policyEffectiveDate, getPligaOrMvleFee(pligaOrMvleFeeLastTransactionDate), softly);
			//}
			payAndCheckBill(installmentDueDates.get(3));
			TestData td2 = getTestSpecificTD("TestData_Endorsement2").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
			changePaymentPlanAndCheckInstallments(td2, BillingConstants.PaymentPlan.ELEVEN_PAY, 9);
			generateAndCheckBill(installmentDueDates.get(4), policyEffectiveDate, getPligaOrMvleFee(pligaOrMvleFeeLastTransactionDate), softly);
			payAndCheckBill(installmentDueDates.get(4));
			generateAndCheckBill(installmentDueDates.get(5), policyEffectiveDate, softly);
			TestData td3 = getTestSpecificTD("TestData_Endorsement3").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
			changePaymentPlanAndCheckInstallments(td3, BillingConstants.PaymentPlan.SEMI_ANNUAL, 5);
			generateAndCheckBill(installmentDueDates.get(6), policyEffectiveDate, softly);
			payAndCheckBill(installmentDueDates.get(6));

			/**
			 * TODO Add Test Change current Payment plan
			 */

			renewalImageGeneration();
			renewalPreviewGeneration();
			TestData td4 = getTestSpecificTD("TestData_Endorsement4").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
			/** TODO Why 5??? */
			changePaymentPlanAndCheckInstallments(td4, BillingConstants.PaymentPlan.SEMI_ANNUAL, true, true, 5, policyEffectiveDate);
			TestData td5 = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
			/** TODO Why 5??? */
			changePaymentPlanAndCheckInstallments(td5, BillingConstants.PaymentPlan.QUARTERLY, false, true, 5, policyEffectiveDate);
			renewalOfferGeneration(softly);
			TestData td6 = getTestSpecificTD("TestData_Endorsement5").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
			changePaymentPlanAndCheckInstallments(td6, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL, true, false, 9, policyExpirationDate);
			renewalPremiumNotice();
			TestData td7 = getTestSpecificTD("TestData_Endorsement4").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
			changePaymentPlanAndCheckInstallments(td7, BillingConstants.PaymentPlan.ELEVEN_PAY_RENEWAL, true, false, 16, policyExpirationDate);
		});
	}
}
