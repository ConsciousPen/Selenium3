package aaa.modules.e2e.auto_ss;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario11;
import toolkit.datax.TestData;

public class TestScenario11 extends Scenario11 {

	private Dollar toleranceAmount = new Dollar(10.00);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		SoftAssertions.assertSoftly(softly -> {
			makeFirstEndorsement();
			generateFirstOffCycleBill();
			payFirstOffCycleBill();
			makeSecondEndorsement();
			generateSecondOffCycleBill();
			paySecondOffCycleBill();
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration();
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
