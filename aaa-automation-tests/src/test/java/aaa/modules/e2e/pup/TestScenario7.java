package aaa.modules.e2e.pup;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.modules.e2e.templates.Scenario7;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario7 extends Scenario7 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.OH, States.OK, States.UT})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumAndCoveragesQuoteTab();
		errorTab = new ErrorTab();

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			payFirstBill();
			generateSecondBill(softly);
			payTotalDue();
			generateThirdBill();
			if (getState().equals(Constants.States.CA)) {
				renewalImageGeneration();
			}
			generateTenthBill();
			//		super.cantChangePaymentPlan();
			if (!getState().equals(Constants.States.CA)) {
				renewalImageGeneration();
			}
			renewalPreviewGeneration();
			endorsementRPBeforeRenewal();
			endorsementAPBeforeRenewal();
			renewalOfferGeneration(softly);
			endorsementRPAfterRenewal();
			endorsementAPAfterRenewal();
			if (!getState().equals(Constants.States.CA)) {
				renewalPremiumNotice();
			}
			checkRenewalStatusAndPaymentNotGenerated();
			expirePolicy();
			generateFirstRenewalBill();
			customerDeclineRenewal();
			createRemittanceFile();
			payRenewalBillByRemittance();
		});
	}
}
