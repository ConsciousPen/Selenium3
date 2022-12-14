package aaa.modules.e2e.auto_ca;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.e2e.templates.Scenario7;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

public class TestScenario7 extends Scenario7 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
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
			generateTenthBill();
			//renewalImageGeneration();
			renewalPreviewGeneration();
			endorsementRPBeforeRenewal();
			endorsementAPBeforeRenewal();
			renewalOfferGeneration(softly);
			endorsementRPAfterRenewal();
			endorsementAPAfterRenewal();
			checkRenewalStatusAndPaymentNotGenerated();
			expirePolicy();
			//generateFirstRenewalBill();
			customerDeclineRenewal(); //customer declined for CA Auto on R+10 according to PASBB-624/PAS-624
			generateFirstRenewalBill();
			createRemittanceFile();
			payRenewalBillByRemittance();
			verifyDocGenForms(false, DocGenEnum.Documents._55_5003, DocGenEnum.Documents._55_5080);
		});
	}
}
