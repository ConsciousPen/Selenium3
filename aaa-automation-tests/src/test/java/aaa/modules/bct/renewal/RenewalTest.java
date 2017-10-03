package aaa.modules.bct.renewal;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class RenewalTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_107_ManualChangesRenewal(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_107_ManualChangesRenewal", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		new PropertyInfoTab().getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
				.getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK).verify.enabled(false);
	}
}
