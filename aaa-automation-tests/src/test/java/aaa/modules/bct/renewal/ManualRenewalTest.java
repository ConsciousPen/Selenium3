package aaa.modules.bct.renewal;

import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.impl.SimpleDataProvider;

public class ManualRenewalTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
//	@Test
	public void BCT_ONL_001_ManualRenewal(@Optional("") String state) {
		mainApp().open();

		String policyNumber = getPoliciesByQuery("BCT_ONL_001_ManualRenewal", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();

		SearchPage.openPolicy(policyNumber);
		policy.renew().perform(new SimpleDataProvider());

		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

	}

	@Parameters({"state"})
//	@Test
	public void BCT_ONL_055_ManualRenewal(@Optional("") String state) {
		mainApp().open();

		String policyNumber = getPoliciesByQuery("BCT_ONL_055_ManualRenewal", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();

		SearchPage.openPolicy(policyNumber);

		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

	}
}
