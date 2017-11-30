package aaa.modules.e2e.home_ca.ho6;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario2;
import toolkit.datax.TestData;

public class TestScenario2 extends Scenario2 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO6;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		SoftAssertions.assertSoftly(softly -> {
			generateFirstBill();
			payFirstBill();
			billingOnHold();
			billNotGenerated();
			generateSecondBill();
			paySecondBill();
			generateThirdBill();
			payThirdBill();
			generateFourthBill();
			payFourthBill();
			generateFifthBill();
			payFifthBill();
			generateSixthBill();
			paySixthBill();
			generateSeventhBill();
			paySeventhBill();
			generateEighthBill();
			payEighthBill();
			generateNinthBill();
			payNinthBill();
			renewalImageGeneration();
			generateTenthBill();
			payTenthBill();
			renewalPreviewGeneration();
			renewalOfferGeneration();
			DocGenEnum.Documents[] documents = new DocGenEnum.Documents[] {
					DocGenEnum.Documents._61_3026, DocGenEnum.Documents.AHRBXX, DocGenEnum.Documents._61_3000, DocGenEnum.Documents._61_5121, DocGenEnum.Documents._61_6530};
			verifyDocGenForms(documents);
			removeAutoPay();
			renewalPaymentNotGenerated();
			updatePolicyStatus();
			makeManualPaymentInFullRenewalOfferAmount();
		});
	}
}
