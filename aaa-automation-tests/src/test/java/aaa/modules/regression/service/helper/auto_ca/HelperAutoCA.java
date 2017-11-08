package aaa.modules.regression.service.helper.auto_ca;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;

public class HelperAutoCA extends PolicyBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}


	public void emailAddressChangedInEndorsementCheck(String emailAddressChanged) {
		policy.policyInquiry().start();
		GeneralTab generalTab = new GeneralTab();
		generalTab.getInquiryAssetList().getStaticElement("Email").verify.value(emailAddressChanged);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		if (documentsAndBindTab.getInquiryAssetList().getStaticElement("Email").isPresent()) {
			documentsAndBindTab.getInquiryAssetList().getStaticElement("Email").verify.value(emailAddressChanged);
		}
		Tab.buttonCancel.click();

	}

	public void secondEndorsementIssueCheck() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		PremiumAndCoveragesTab.calculatePremium();
		new PremiumAndCoveragesTab().saveAndExit();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
	}
}
