package aaa.modules.bct.renewal;

import static aaa.common.enums.Constants.States.CA;
import static aaa.common.enums.Constants.States.NJ;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class RenewalTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	@StateList(states = NJ)
	public void BCT_ONL_107_ManualChangesRenewal(@Optional("NJ") String state) {
		mainApp().open();

		String policyNumber = getPoliciesByQuery("BCT_ONL_107_ManualChangesRenewal", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		assertThat(new PropertyInfoTab().getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
				.getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isEnabled(false);
	}

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void BCT_HdesRenewConvertedPolicyHO3CA(@Optional("CA") String state) {
		IPolicy policy = findAndOpenPolicy("BCT_HdesRenewConvertedPolicyHO3CA", PolicyType.HOME_CA_HO3);
		policy.renew().start();
		Tab.buttonOk.click();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab().calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new aaa.main.modules.policy.home_ca.defaulttabs.BindTab().submitTab();
		Page.dialogConfirmation.confirm();

	}
}
