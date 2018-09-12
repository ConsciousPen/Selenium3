package aaa.modules.bct.renewal;

import static aaa.common.enums.Constants.States.NJ;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class RenewalTest extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	/**
	 * @author Deloite
	 * @name Renewal-OilPropaneTank
	 * @scenario
	 * @param state
	 * Preconditions:
	 * Retrieve a HO3 Policy with renewal image created
	 * Steps:
	 * 1.Retrieve a HO3 active policy with Renewal image created
	 * 2.Agent clicks on renewal image link and navigates to property info page
	 * 3.System displays the "Oil Fuel or Propane Storage Tank"' field
	 * 4.User  cannot edit the value for the field  ""Oil Fuel or Propane Storage Tank"'."
	 * Check:
	 * "Oil Fuel or Propane Storage Tank"' field is disable at Renewals
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = NJ)
	public void BCT_ONL_107_ManualChangesRenewal(@Optional("NJ") String state) {
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		mainApp().open();

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		assertThat(new PropertyInfoTab().getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
				.getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isEnabled(false);
	}
}
