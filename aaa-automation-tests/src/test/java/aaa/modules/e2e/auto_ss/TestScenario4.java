package aaa.modules.e2e.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario4;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

public class TestScenario4 extends Scenario4 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.CO, States.IN, States.KS, States.KY, States.NJ, States.NY, 
			States.OH, States.OR, States.PA, States.SD, States.UT, States.VA})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		cashOverpaymentLow = new Dollar(499.99);
		cashOverpaymentHigh = new Dollar(500);

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
			if (!getState().equals(Constants.States.NJ)) {
				verifyFormAHCWXX();
			}
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration(softly);
			renewalPremiumNotice();
			expirePolicy();
			customerDeclineRenewal();
			payRenewOffer();
			bindRenew();
		});
	}
}
