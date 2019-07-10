package aaa.modules.bct.renewal;

import static aaa.common.enums.Constants.States.CA;
import static aaa.common.enums.Constants.States.NJ;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
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
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

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
		TimeSetterUtil.getInstance().getCurrentTime();
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		assertThat(new PropertyInfoTab().getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
				.getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isEnabled(false);
	}

	/**
	 * Ability to renew converted policy
	 * @param state
	 */

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void BCT_AbilityToRenewHdesHO3Policy(@Optional("CA") String state) {
		IPolicy policy = findAndOpenPolicy("BCT_AbilityToRenewHdesPolicy_HO3", PolicyType.HOME_CA_HO3);
		renewAndPurchaseConvertedPolicy(policy);

	}

	/**
	 * Ability to renew converted policy
	 * @param state
	 */

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void BCT_AbilityToRenewHdesHO4Policy(@Optional("CA") String state) {
		IPolicy policy = findAndOpenPolicy("BCT_AbilityToRenewHdesPolicy_HO4", PolicyType.HOME_CA_HO4);
		renewAndPurchaseConvertedPolicy(policy);

	}

	/**
	 * Ability to renew converted policy
	 * @param state
	 */

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void BCT_AbilityToRenewHdesHO6Policy(@Optional("CA") String state) {
		IPolicy policy = findAndOpenPolicy("BCT_AbilityToRenewHdesPolicy_HO6", PolicyType.HOME_CA_HO6);
		renewAndPurchaseConvertedPolicy(policy);

	}

	private void renewAndPurchaseConvertedPolicy(IPolicy policy) {
		if(PolicySummaryPage.tableRenewals.isPresent()){ // if policy is expired
			policy.dataGather().start();
		}
		else{
			policy.renew().start();
			Tab.buttonOk.click();
		}

		if(Page.dialogConfirmation.isPresent()){
			Page.dialogConfirmation.confirm();
		}
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab().calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new aaa.main.modules.policy.home_ca.defaulttabs.BindTab().submitTab();
		//Page.dialogConfirmation.confirm();
	}
}
