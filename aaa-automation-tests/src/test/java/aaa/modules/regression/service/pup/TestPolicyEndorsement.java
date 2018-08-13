package aaa.modules.regression.service.pup;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

public class TestPolicyEndorsement extends PersonalUmbrellaBaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER})
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void testPolicyEndorsement(@Optional("") String state) {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();

		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("TEST: Flat Endorsement for PUP Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

		TestData endorsementTd = getTestSpecificTD("TestData");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));

		CustomSoftAssertions.assertSoftly(softly -> {

			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			//PolicySummaryPage.tableOtherUnderlyingRisks.verify.rowsCount(2);

			softly.assertThat(policyPremium).isNotEqualTo(PolicySummaryPage.TransactionHistory.getEndingPremium());
		});
	}
}
