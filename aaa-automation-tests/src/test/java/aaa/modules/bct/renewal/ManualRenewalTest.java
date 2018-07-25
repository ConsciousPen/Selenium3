package aaa.modules.bct.renewal;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.impl.SimpleDataProvider;

public class ManualRenewalTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_047_ManualRenewal(@Optional("") String state) {
		IPolicy policy = findAndOpenPolicy("BCT_ONL_047_ManualRenewal", PolicyType.AUTO_SS);
		policy.doNotRenew().start();
		Page.dialogConfirmation.confirm();
		policy.doNotRenew().getView().fill(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DoNotRenew", "TestData"));
		policy.doNotRenew().submit();
		assertThat(PolicySummaryPage.labelDoNotRenew).isPresent();
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_055_ManualRenewal(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_055_ManualRenewal", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_001_ManualRenewal(@Optional("") String state) {
		IPolicy policy = findAndOpenPolicy("BCT_ONL_001_ManualRenewal", PolicyType.AUTO_SS);
		policy.renew().start();
		Tab.buttonOk.click();
		Page.dialogConfirmation.confirm();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_003_ManualRenewal(@Optional("") String state) {
		IPolicy policy = findAndOpenPolicy("BCT_ONL_003_ManualRenewal", PolicyType.AUTO_SS);
		deletePendingRenewals(policy);
		policy.doNotRenew().perform(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DoNotRenew", "TestData"));
		assertThat(PolicySummaryPage.labelDoNotRenew).isPresent();
		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		assertThat(PolicySummaryPage.labelDoNotRenew).isPresent(false);
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_004_ManualRenewal(@Optional("") String state) {
		IPolicy policy = findAndOpenPolicy("BCT_ONL_004_ManualRenewal", PolicyType.AUTO_SS);
		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		assertThat(PolicySummaryPage.labelDoNotRenew).isPresent(false);
	}
}
