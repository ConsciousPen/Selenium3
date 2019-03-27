package aaa.modules.bct.service.home_ca.dp3;

import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.modules.bct.EndorsementTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestEndorsement extends EndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_DP3;
	}

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCADp3(String state, String policyNumber) {
		emptyEndorsementHomeCA(policyNumber);
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
