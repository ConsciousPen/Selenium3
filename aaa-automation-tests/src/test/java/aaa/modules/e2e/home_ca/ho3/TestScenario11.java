package aaa.modules.e2e.home_ca.ho3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario11;
import toolkit.datax.TestData;

public class TestScenario11 extends Scenario11 {
	private Dollar toleranceAmount = new Dollar(12.00);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), HomeCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
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
			renewalOfferGeneration();
			updatePolicyStatus();
			payRenewalOfferNotInFullAmount(toleranceAmount);
			payRenewalOfferInFullAmount(toleranceAmount);
			makeOverpayment();
			cancellationPolicy();
			refundGeneration();
		});
	}

}
