package aaa.modules.e2e.home_ca.ho6;

import toolkit.verification.CustomSoftAssertions;
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
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			payFirstBill();
			billingOnHold();
			billNotGenerated();
			generateSecondBill();
			paySecondBill(softly);
			generateThirdBill(softly);
			payThirdBill();
			generateFourthBill(softly);
			payFourthBill();
			generateFifthBill(softly);
			payFifthBill();
			generateSixthBill(softly);
			paySixthBill();
			generateSeventhBill(softly);
			paySeventhBill();
			generateEighthBill(softly);
			payEighthBill();
			generateNinthBill(softly);
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
