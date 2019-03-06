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
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
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
