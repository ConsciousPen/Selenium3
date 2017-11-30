package aaa.modules.e2e.home_ss.ho3;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario6;
import toolkit.datax.TestData;

public class TestScenario6 extends Scenario6 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[]{new EndorsementActionTab().getMetaKey(), HomeSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		SoftAssertions.assertSoftly(softly -> {
			generateFirstBill();
			verifyFormAHIBXX();
			endorsePolicy();
			payFirstBill();
			generateCancellNotice();
			generateSecondBill();
			paySecondBill();
			generateThirdBill();
			payThirdBill();
			setDoNotRenewFlag();
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration();
			manualRenewPolicy();
			verifyFormAHR1XX_And_HSRNXX();
			payRenewOffer();
			updatePolicyStatus();
			automaticRefundNotGenerated();
		});
	}
}
