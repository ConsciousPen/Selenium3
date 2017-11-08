package aaa.modules.regression.service.helper.home_ss;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;

public class HelperHomeSS_HO3 extends PolicyBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}


	public void emailAddressChangedInEndorsementCheck(String emailAddressChanged) {
		policy.policyInquiry().start();
		GeneralTab generalTab = new GeneralTab();
		generalTab.getInquiryAssetList().getStaticElement("Email").verify.value(emailAddressChanged);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		documentsAndBindTab.getInquiryAssetList().getStaticElement("Email").verify.value(emailAddressChanged);
		Tab.buttonCancel.click();

	}

	public void secondEndorsementIssueCheck() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		PremiumAndCoveragesTab.calculatePremium();
		Tab.buttonSaveAndExit.click();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
	}
}
