package aaa.modules.e2e.home_ss.ho3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario3;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario3 extends Scenario3 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.CO, States.CT, States.DC, States.DE, States.ID, States.IN, States.KS, States.KY, States.MT, 
			States.NJ, States.NY, States.OH, States.OK, States.OR, States.PA, States.SD, States.UT, States.VA, States.WV, States.WY})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			generateCancellationNotice();
			cancelPolicy(installmentDueDates.get(1));
			createRemittanceFile();
			payCancellationNoticeByRemittance();
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration(softly);
			renewalPremiumNotice();
			expirePolicy();
			customerDeclineRenewal();
			bindRenew();
		});
	}
}
