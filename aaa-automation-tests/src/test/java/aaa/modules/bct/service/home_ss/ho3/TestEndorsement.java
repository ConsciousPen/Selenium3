package aaa.modules.bct.service.home_ss.ho3;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.bct.service.EndorsementTemplate;
import aaa.utils.StateList;

public class TestEndorsement extends EndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	private ReportsTab reportsTab = new ReportsTab();
	private BindTab bindTab = new BindTab();
	private GeneralTab generalTab = new GeneralTab();

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementHomeSSHo3(String state, String policyNumber) {
		Dollar policyPremium = getPreEndorsementPremium(getPolicyType().get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(getPolicyType(),"TestDataInquirySSHo3", generalTab, bindTab);

		performNonBearingEndorsement(getPolicyType(), TESTDATA_NAME_ENDORSE_HOME_SS);
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();

		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
	}

}
