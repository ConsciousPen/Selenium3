package aaa.modules.e2e.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario11;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

public class TestScenario11 extends Scenario11 {

	private Dollar toleranceAmount = new Dollar(10.00);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.NY, States.UT})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			makeFirstEndorsement();
			generateFirstOffCycleBill();
			payFirstOffCycleBill();
			makeSecondEndorsement();
			generateSecondOffCycleBill();
			paySecondOffCycleBill(); 
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration(softly);
			generateRenewalBill();
			updatePolicyStatus();
			payRenewalBillNotInFullAmount(toleranceAmount);
			payRenewalOfferInFullAmount(toleranceAmount);
			makeOverpayment();
			cancellationPolicy();
			refundGeneration();
		});
	}
}
