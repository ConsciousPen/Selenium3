package aaa.modules.regression.service.pup;

import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestPolicyEndorsement extends PersonalUmbrellaBaseTest {

	@Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER})
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void testPolicyEndorsement() {
		mainApp().open();

		getCopiedPolicy();

		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("TEST: Flat Endorsement for PUP Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

		TestData endorsementTd = getTestSpecificTD("TestData");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));

		CustomAssert.enableSoftMode();

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//PolicySummaryPage.tableOtherUnderlyingRisks.verify.rowsCount(2);

		CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}
