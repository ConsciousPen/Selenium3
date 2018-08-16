package aaa.modules.e2e.auto_ss;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.modules.e2e.templates.Scenario7;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario7 extends Scenario7 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.CO, States.IN, States.KS, States.KY, States.NJ, States.NY, 
			States.OH, States.OR, States.PA, States.SD, States.UT, States.VA})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumAndCoveragesTab();
		errorTab = new ErrorTab();
		tableDiscounts = PremiumAndCoveragesTab.tableDiscounts;

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			payFirstBill();
			generateSecondBill(softly);
			payTotalDue();
			generateThirdBill();
			renewalImageGeneration();
			if (getState().equals(Constants.States.KY)) {
				renewalPreviewGeneration();
			}
			generateTenthBill();
			cantChangePaymentPlan();
			if (!getState().equals(Constants.States.KY)) {
				renewalPreviewGeneration();
			}
			endorsementRPBeforeRenewal();
			endorsementAPBeforeRenewal();
			renewalOfferGeneration(softly);
			endorsementRPAfterRenewal();
			endorsementAPAfterRenewal();
			renewalPremiumNotice();
			checkRenewalStatusAndPaymentNotGenerated();
			expirePolicy();
			generateFirstRenewalBill();
			customerDeclineRenewal();
			createRemittanceFile();
			payRenewalBillByRemittance();
			qualifyForManualRenewalTaskCreated();
		});
	}
}
