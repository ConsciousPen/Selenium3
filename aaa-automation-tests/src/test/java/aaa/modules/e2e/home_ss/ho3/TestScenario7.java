package aaa.modules.e2e.home_ss.ho3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.e2e.templates.Scenario7;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario7 extends Scenario7 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.OH, States.OK, States.UT})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumsAndCoveragesQuoteTab();
		errorTab = new ErrorTab();
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			payFirstBill();
			generateSecondBill(softly);
			payTotalDue();
			generateThirdBill();
			generateTenthBill();
			//		super.cantChangePaymentPlan();
			renewalImageGeneration();
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
