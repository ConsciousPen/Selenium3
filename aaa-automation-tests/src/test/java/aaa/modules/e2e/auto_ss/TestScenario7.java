package aaa.modules.e2e.auto_ss;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.modules.e2e.templates.Scenario7;
import toolkit.datax.TestData;

public class TestScenario7 extends Scenario7 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumAndCoveragesTab();
		errorTab = new ErrorTab();
		tableDiscounts = PremiumAndCoveragesTab.tableDiscounts;

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		SoftAssertions.assertSoftly(softly -> {
			generateFirstBill();
			payFirstBill();
			generateSecondBill();
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
			endorsementAPBeforeRenewal();
			renewalOfferGeneration();
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
