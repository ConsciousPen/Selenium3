package aaa.modules.e2e.pup;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario4;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario4 extends Scenario4 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.CA, States.CO, States.CT, States.DC, States.ID, States.IN, States.KS, States.MD, States.MT, 
			States.NJ, States.NV, States.OH, States.OK, States.OR, States.PA, States.SD, States.UT, States.VA, States.WV})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), PersonalUmbrellaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			overpayment();
			automaticRefund();
			overpaymentHigh();
			automaticRefundHigh();
			endorsePolicy();
			generateOffCycleBill();
			generateCancelNotice();
			paymentInFullCancellNoticeAmount();
			verifyFormAHCWXX();
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration(softly);
			if (!getState().equals(Constants.States.CA)) {
				renewalPremiumNotice();
			}
			expirePolicy();
			customerDeclineRenewal();
			payRenewOffer();
			bindRenew();
		});
	}
}
