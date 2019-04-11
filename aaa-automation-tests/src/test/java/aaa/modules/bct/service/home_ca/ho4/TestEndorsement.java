package aaa.modules.bct.service.home_ca.ho4;

import static aaa.common.enums.Constants.States.CA;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.EndorsementTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestEndorsement extends EndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO4;
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCAHo4(String state, String policyNumber) {
		emptyEndorsementHomeCA(policyNumber);
	}

	/**
	 * Ability to Endorse converted policy
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void BCT_AbilityToEndorseHdesHO4Policy(@Optional("CA") String state) {
		findAndOpenPolicy("BCT_AbilityToEndorseHdesPolicy_HO3", PolicyType.HOME_CA_HO3);
		emptyEndorsementHomeCA(PolicySummaryPage.getPolicyNumber());
	}

	@Override
	public void reorderReports() {
		new ReportsTab().reorderReports();
	}

	@Override
	public void performNonBearingEndorsement(String testDataName) {
		TestData testDataEnd = getTestSpecificTD(testDataName);

		policy.endorse().perform(testDataEnd);
		policy.dataGather().getView()
				.fillFromTo(testDataEnd, GeneralTab.class, ReportsTab.class, false);

		reorderReports();

		policy.dataGather().getView()
				.fillFromTo(testDataEnd, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, false);
	}
}
