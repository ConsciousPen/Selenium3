package aaa.modules.bct.service.pup;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.modules.bct.EndorsementTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestEndorsement extends EndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	private PrefillTab prefillTab = new PrefillTab();
	private BindTab bindTab = new BindTab();

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CA, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementPUP(String state, String policyNumber) {
		TestData td = getTestSpecificTD("TestDataEndorsePUP");

		Dollar policyPremium = getPreEndorsementPremium(getPolicyType().get(), policyNumber);
		log.info(String.format("Policy premium on Policy Summary page: '%s'", policyPremium));

		checkAbilityToOpenAllTabsInInquiryMode(getPolicyType(),"TestDataInquiryPUP", prefillTab, bindTab);
		bindTab.cancel();

		getPolicyType().get().endorse().perform(td);
		getPolicyType().get().dataGather().getView().fillFromTo(td, PrefillTab.class, PremiumAndCoveragesQuoteTab.class, false);
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesQuoteTab.getPolicyTermPremium());

		PremiumAndCoveragesQuoteTab.btnCalculatePremium.click();
		Dollar policyTermPremium = PremiumAndCoveragesQuoteTab.getPolicyActualPremium();

		log.info(String.format("Endorsement Premium: '%s'", policyTermPremium));
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(policyTermPremium);
	}
}
