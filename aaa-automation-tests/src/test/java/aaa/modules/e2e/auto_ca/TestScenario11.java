package aaa.modules.e2e.auto_ca;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario11;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

public class TestScenario11 extends Scenario11 {

	private Dollar toleranceAmount = new Dollar(15.00);

	//For CA Choice
	//private Dollar toleranceAmount = new Dollar(12.00);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
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
			updatePolicyStatus();
			payRenewalOfferNotInFullAmount(toleranceAmount);
			payRenewalOfferInFullAmount(toleranceAmount);
			makeOverpayment();
			cancellationPolicy();
			refundGeneration();
		});
	}
}
