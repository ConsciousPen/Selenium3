package aaa.modules.bct.service.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.modules.bct.service.EndorsementTemplate;
import aaa.utils.StateList;

public class TestEndorsement extends EndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private ReportsTab reportsTab = new ReportsTab();
	private BindTab bindTab = new BindTab();
	private GeneralTab generalTab = new GeneralTab();

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {Constants.States.CA})
	public void BCT_ONL_EmptyEndorsementHomeCAHo3(String state, String policyNumber) {
		Dollar policyPremium = getPreEndorsementPremium(getPolicyType().get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(getPolicyType(), TESTDATA_INQUIRY_HOME_CA, generalTab,bindTab);
		assertThat(bindTab.btnPurchase.isPresent()).isTrue();
		bindTab.cancel();

		performNonBearingEndorsement(getPolicyType(), TESTDATA_NAME_ENDORSE_HOME_CA);
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();

		Dollar postEndorsementPremium = PropertyQuoteTab.getPolicyTermPremium();

		log.info(String.format("Endorsement Premium: '%s'", postEndorsementPremium));
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(postEndorsementPremium);
	}
}
