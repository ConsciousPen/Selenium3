package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.modules.regression.sales.template.functional.TestClueSimplificationPropertyTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

@StateList(statesExcept = Constants.States.CA)
public class TestAbilityToRemoveManuallyAddedClaims extends TestClueSimplificationPropertyTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected BindTab getBindTab() {
		return new BindTab();
	}

	@Override
	protected PurchaseTab getPurchaseTab() {
		return new PurchaseTab();
	}

	@Override
	protected void navigateToBindTab() {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
	}

	@Override
	protected PropertyInfoTab getPropertyInfoTab() {
		return new PropertyInfoTab();
	}

	@Override
	protected PremiumsAndCoveragesQuoteTab getPremiumAndCoveragesQuoteTab() {
		return new PremiumsAndCoveragesQuoteTab();
	}

	@Override
	protected void calculatePremiumAndOpenVRD() {
		getPremiumAndCoveragesQuoteTab().calculatePremium();
		PropertyQuoteTab.RatingDetailsView.open();
	}

	@Override
	protected Table getClaimHistoryTable() {
		return getPropertyInfoTab().tblClaimsList;
	}

	@Override
	protected String getClaimHistoryLabel() {
		return HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel();
	}

	@Override
	protected void navigateToPropertyInfoTab() {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
	}

	@Override
	protected TextBox getClaimDateOfLossAsset() {
		return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS);
	}

	@Override
	protected RadioGroup getClaimCatastropheAsset() {
		return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CATASTROPHE_LOSS);
	}

	@Override
	protected RadioGroup getAAAClaimAsset() {
		return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM);
	}

	@Override
	protected TextBox getClaimAmountAsset() {
		return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test
	 * @scenario
	 * 1. Create
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6759")
	public void pas6759_AbilityToRemoveManuallyEnteredClaims(@Optional("AZ") String state) {



		pas6759_AbilityToRemoveManuallyEnteredClaimsNB();

		pas6759_AbilityToRemoveManuallyEnteredClaimsEndorsement();
	}




}