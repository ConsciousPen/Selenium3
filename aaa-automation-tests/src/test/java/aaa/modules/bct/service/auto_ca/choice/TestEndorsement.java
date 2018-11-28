package aaa.modules.bct.service.auto_ca.choice;

import static aaa.common.enums.Constants.States.CA;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.bct.EndorsementTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestEndorsement extends EndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {CA})
	public void BCT_ONL_EmptyEndorsementAutoCAChoice(String policyNumber) {
		TestData td = getTestSpecificTD("TestDataEndorseAutoCA");

		Dollar policyPremium = getPreEndorsementPremium(getPolicyType().get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(getPolicyType(), "TestDataInquiryAutoCA", new GeneralTab(), documentsAndBindTab);
		assertThat(DocumentsAndBindTab.btnPurchase.isPresent()).isTrue();
		documentsAndBindTab.cancel();

		getPolicyType().get().endorse().perform(td);
		getPolicyType().get().dataGather().getView()
				.fillFromTo(td, GeneralTab.class, PremiumAndCoveragesTab.class, false);

		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesTab.getPolicyTermPremium());
		new PremiumAndCoveragesTab().calculatePremium();

		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumAndCoveragesTab.getPolicyTermPremium());
	}
}
