package aaa.modules.regression.sales.home_ss.ho4.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.modules.regression.sales.template.functional.TestClaimPointsVRDPageAbstract;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

@StateList(statesExcept = Constants.States.CA)
public class TestClaimPointsVRDPage extends TestClaimPointsVRDPageAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
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
		PropertyQuoteTab.RatingDetailsView.close();
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
	 * @author Josh Carpenter
	 * @name Test Claims points refresh on VRD page for SS HO4 policies during NB
	 * @scenario
	 * 1.  Create customer
	 * 2.  Initiate SS HO4 quote
	 * 3.  Fill quote with 4 claims:
	 * 		a. Fire, $500, Closed, date of loss = 12 months ago, AAA Claim = 'Yes', Catastrophe = 'No'
	 * 		b. Water, $5000, Closed, date of loss = 10 months ago, AAA Claim = 'Yes', Catastrophe = 'Yes'
	 * 		c. Theft, $2500, Closed, date of loss = 8 months ago,  AAA Claim = 'Yes', Catastrophe = 'No'
	 * 		d. Liability, $7000, Closed, date of loss = 6 months ago,  AAA Claim = 'Yes', Catastrophe = 'No'
	 * 4.  Navigate to P & C tab and calculate premium
	 * 5.  Validate claims display on VRD page with correct points
	 * 6.  Navigate to Property Info tab
	 * 7.  Adjust date of loss for liability claim to 37 months ago; water claim to catastrophe = 'No'
	 * 9.  Navigate to P & C tab and calculate premium
	 * 10. Validate claims display on VRD page with correct points
	 * 10. Navigate to Property Info tab
	 * 11. Adjust liability claim date of loss to 2 years ago
	 * 12. Navigate to P & C tab and calculate premium
	 * 13. Validate claims display on VRD page with correct points
	 * 14. Navigate to Property Info tab
	 * 15. Adjust liability claim date of loss back to 6 months ago; all claims to AAA Claim = 'No'
	 * 17. Navigate to P & C tab and calculate premium
	 * 18. Validate claims display on VRD page with correct points
	 * 19. Navigate to Property Info tab
	 * 20. Adjust fire & water claims back to AAA Claim = 'Yes'; fire claim amount to $1001; theft claim to catastrophe = 'Yes'
	 * 21. Navigate to P & C tab and calculate premium
	 * 22. Validate claims display on VRD page with correct points
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-17772")
	public void pas17772_testClaimPointsVRDPage(@Optional("") String state) {

		testClaimsPointsVRDPage();

	}

}

