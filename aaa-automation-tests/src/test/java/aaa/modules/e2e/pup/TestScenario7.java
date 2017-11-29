package aaa.modules.e2e.pup;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.modules.e2e.templates.Scenario7;
import toolkit.datax.TestData;

public class TestScenario7 extends Scenario7 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumAndCoveragesQuoteTab();
		errorTab = new ErrorTab();

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		SoftAssertions.assertSoftly(softly -> {
			generateFirstBill();
			payFirstBill();
			generateSecondBill();
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
			renewalOfferGeneration();
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
