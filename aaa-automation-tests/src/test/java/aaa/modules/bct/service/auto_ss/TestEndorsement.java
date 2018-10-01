package aaa.modules.bct.service.auto_ss;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.bct.EndorsementTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestEndorsement extends EndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	@Test(dataProvider = "getPoliciesForEmptyEndorsementTests")
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_EmptyEndorsementAutoSS(String state, String policyNumber) {
		Dollar policyPremium = getPreEndorsementPremium(getPolicyType().get(), policyNumber);

		checkAbilityToOpenAllTabsInInquiryMode(getPolicyType(), "TestDataInquiryAutoSS", generalTab, documentsAndBindTab);
		assertThat(documentsAndBindTab.getDocumentPrintingDetailsAssetList().isVisible()).isTrue();
		documentsAndBindTab.cancel();

		TestData td = getTestSpecificTD("TestDataEndorseAutoSS");
		getPolicyType().get().endorse().perform(td);
		generalTab.fillTab(td);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(policyPremium).isEqualTo(PremiumAndCoveragesTab.getActualPremium());

		premiumAndCoveragesTab.calculatePremium();
		assertThat(policyPremium).as("Test for state %s has failed due to difference between pre-endorsement and post-endorsement premiums", getState())
				.isEqualTo(PremiumAndCoveragesTab.getActualPremium());
	}

}
