package aaa.modules.regression.sales.home_ss.ho6.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.regression.sales.template.functional.TestClueSimplificationPropertyAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

@StateList(statesExcept = Constants.States.CA)
public class TestClueSimplification extends TestClueSimplificationPropertyAbstract {

	@Override
	protected String getNamedInsuredLabel() {
		return HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel();
	}

	@Override
	protected ComboBox getClaimLossForAsset() {
		return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.LOSS_FOR);
	}

    @Override
    protected ComboBox getClaimSourceAsset() {
        return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.SOURCE);
    }

    @Override
    protected String getBtnAddInsuredLabel() {
        return HomeSSMetaData.ApplicantTab.NamedInsured.BTN_ADD_INSURED.getLabel();
    }

	@Override
	protected ReportsTab getReportsTab() {
		return new ReportsTab();
	}

	@Override
	protected ApplicantTab getApplicantTab() {
		return new ApplicantTab();
	}

	@Override
	protected void navigateToApplicantTab() {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
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
	protected RadioGroup getClaimChargeableAsset() {
		return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.INCLUDED_IN_RATING_AND_ELIGIBILITY);
	}

	@Override
	protected TextBox getClaimNonChargeableReasonAsset() {
		return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.REASON_CLAIM_IS_NOT_CHARGEABLE);
	}

	@Override
	protected RadioGroup getAAAClaimAsset() {
		return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM);
	}

	@Override
	protected TextBox getClaimAmountAsset() {
		return getPropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS);
	}

    @Override
    protected void reorderClueReport() {
        new ReportsTab().getAssetList().getAsset(HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
	    new ReportsTab().getAssetList().getAsset(HomeSSMetaData.ReportsTab.CLUE_REPORT).getTable().getRow(1).getCell("Report").controls.links.getFirst().click();
    }

	/**
	 * @author Dominykas Razgunas
	 * @name Test Ability To Remove Manually Added Claims NB
	 * @scenario
	 * 1. Open App with privileged user.
	 * 2. Create customer.
	 * 3. Create Property Quote.
	 * 4. Fill Quote up to Bind Tab. Manually Add Claims
	 * 5. Save and Exit.
	 * 6. Log in with unprivileged User.
	 * 7. Search Quote.
	 * 8. Enter Datagather.
	 * 9. Navigate to Property Info Tab.
	 * 10. Check that there are 4 claims in claim table.
	 * 11. Remove One Claim.
	 * 12. Check That there are 3 claims in the claim table.
	 * 13. Save and Exit.
	 * 14. Enter DataGather.
	 * 15. Navigate to Property Info Tab.
	 * 16. Remove Claim.
	 * 17. Check that there are 2 Claims in the claim table.
	 * 18. Save and Exit.
	 * 19. Close App.
	 * 20. Log in with privileged User.
	 * 21. Enter Datagather mode.
	 * 22. Navigate to property info tab.
	 * 23. Check That button Remove for claim is present.
	 * 24. Calculate Premium.
	 * 25. Bind Policy.
	 * 26. Endorse Bound Policy.
	 * 27. Navigate to Property Info Tab.
	 * 28. Check that button Remove for claim is present.  // Not working until PAS-20443
	 * 29. Save and Exit.
	 * 30. Close App.
	 * 31. Log in with unprivileged User.
	 * 32. Search for the Policy.
	 * 33. Open endorsement.
	 * 34. Navigate to property info tab.
	 * 35. Check that button remove claim is present.
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Ability To Remove Manually Added Claims")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6759")
	public void pas6759_AbilityToRemoveManuallyEnteredClaimsNB(@Optional("AZ") String state) {

		pas6759_AbilityToRemoveManuallyEnteredClaimsNB();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Ability To Remove Manually Added Claims
	 * @scenario
	 * 1. Create Property Policy.
	 * 2. Close App.
	 * 3. Log in with unprivileged user.
	 * 4. Search for policy.
	 * 5. Endorse the policy.
	 * 6. Navigate to property info tab.
	 * 7. Manually add claims.
	 * 8. Check that there are 4 claims in claim table.
	 * 9. Remove claim.
	 * 10. Check that there are 3 claims in claim table.
	 * 11. Save and Exit.
	 * 12. Start endorsement.
	 * 13. Navigate to property info tab.
	 * 14. remove claim.
	 * 15. Check that there are two claims in claim table.
	 * 16. Save and Exit.
	 * 17. Open App with privileged user.
	 * 18. Navigate to property info tab.
	 * 19. Check That button Remove for claim is present.
	 * 20. Calculate Premium.
	 * 21. Bind Endorsement.
	 * 22. Endorse Bound Policy.
	 * 23. Navigate to Property Info Tab.
	 * 24. Check that button Remove for claim is present.  // Not working until PAS-20443
	 * 25. Save and Exit.
	 * 26. Close App.
	 * 27. Log in with unprivileged User.
	 * 28. Search for the Policy.
	 * 29. Open endorsement.
	 * 30. Navigate to property info tab.
	 * 31. Check that button remove claim is present.
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Ability To Remove Manually Added Claims Endorsement")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6759")
	public void pas6759_AbilityToRemoveManuallyEnteredClaimsEndorsement(@Optional("AZ") String state) {

		pas6759_AbilityToRemoveManuallyEnteredClaimsEndorsement();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Ability To Remove Manually Added Claims
	 * @scenario
	 * 1. Create Property Policy.
	 * 2. Close App.
	 * 3. Log in with unprivileged user.
	 * 4. Search for policy.
	 * 5. Cancel and Rewrite Policy.
	 * 6. Add Claims.
	 * 7. Remove claims check if they were removed.
	 * 8. Save and Exit.
	 * 9. Enter Datagather.
	 * 10. Remove Claims. Make sure they are removed.
	 * 11. Bind Rewrite.
	 * 12. Cancel Policy.
	 * 13. Rewrite Policy.
	 * 14. Check That Agent is not able to remove preciously added claims.
	 * 15. Add new Claims.
	 * 16. Check that Agent is able to remove Claims.
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Ability To Remove Manually Added Claims ReWrite")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6759")
	public void pas6759_AbilityToRemoveManuallyEnteredClaimsReWrite(@Optional("AZ") String state) {

		pas6759_AbilityToRemoveManuallyEnteredClaimsReWrite();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Ability To Remove Manually Added Claims
	 * @scenario
	 * 1. Create Property Policy.
	 * 2. Close App.
	 * 3. Log in with unprivileged user.
	 * 4. Search for policy.
	 * 5. Renew the policy.
	 * 6. Navigate to property info tab.
	 * 7. Manually add claims.
	 * 8. Check that there are 4 claims in claim table.
	 * 9. Remove claim.
	 * 10. Check that there are 3 claims in claim table.
	 * 11. Save and Exit.
	 * 12. Start Renewal.
	 * 13. Navigate to property info tab.
	 * 14. remove claim.
	 * 15. Check that there are two claims in claim table.
	 * 16. Save and Exit.
	 * 17. Open App with privileged user.
	 * 18. Navigate to property info tab.
	 * 19. Check That button Remove for claim is present.
	 * 20. Calculate Premium.
	 * 21. Bind Renewal.
	 * 22. Endorse Bound Policy.
	 * 23. Navigate to Property Info Tab.
	 * 24. Check that button Remove for claim is present.  // Not working until PAS-20443
	 * 25. Save and Exit.
	 * 26. Close App.
	 * 27. Log in with unprivileged User.
	 * 28. Search for the Policy.
	 * 29. Open endorsement.
	 * 30. Navigate to property info tab.
	 * 31. Check that button remove claim is present.
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Ability To Remove Manually Added Claims Renewal")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6759")
	public void pas6759_AbilityToRemoveManuallyEnteredClaimsRenewal(@Optional("AZ") String state) {

		pas6759_AbilityToRemoveManuallyEnteredClaimsRenewal();
	}

    /**
     * @author Dominykas Razgunas, Josh Carpenter
     * @name Test lack of Dependency Between CAT And Chargeable CLUE Claim Mapping
     * @scenario
     * 1. Create Individual Customer Virat Kohli and second named insured Silvia Kohli (both have claims to return)
     * 2. Initiate policy creation
     * 3. Fill Quote till Property Info Tab, validate only full scope claims are populated
     * 4. Select Hail Claim and set CAT = YES chargeable = NO
     * 5. Select Wind Claim and set CAT = YES chargeable = YES.
     * 6. Select Fire Claim and set CAT = NO chargeable = YES.
     * 7. Select Water Claim and set CAT = NO chargeable = NO.
     **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6742, PAS-6695")
	public void pas6695_testClueReconciliationNB(@Optional("") String state) {
		pas6695_testClueClaimsReconciliationNB();

	}

    /**
     * @author Dominykas Razgunas, Josh Carpenter
     * @name Test lack of Dependency Between CAT And Chargeable CLUE Claim Mapping
     * @scenario
     * 1. Create policy with default test data (including customer)
     * 2. Initiate endorsement, add 2 named insured with claims (Virat and Silvia Kohli)
     * 3. Reorder CLUE, validate only full scope claims are populated on property info tab
     * 4. Select Hail Claim and set CAT = YES chargeable = NO
     * 5. Select Wind Claim and set CAT = YES chargeable = YES.
     * 6. Select Fire Claim and set CAT = NO chargeable = YES.
     * 7. Select Water Claim and set CAT = NO chargeable = NO.
     **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6742, PAS-6695")
	public void pas6695_testClueReconciliationEndorsement(@Optional("") String state) {
		pas6695_testClueClaimsReconciliationEndorsement();

	}

    /**
     * @author Dominykas Razgunas, Josh Carpenter
     * @name Test lack of Dependency Between CAT And Chargeable CLUE Claim Mapping
     * @scenario
     * 1. Create policy with default test data (including customer)
     * 2. Create renewal image, add 2 named insured with claims (Virat and Silvia Kohli)
     * 3. Reorder CLUE, validate only full scope claims are populated on property info tab
     * 4. Select Hail Claim and set CAT = YES chargeable = NO
     * 5. Select Wind Claim and set CAT = YES chargeable = YES.
     * 6. Select Fire Claim and set CAT = NO chargeable = YES.
     * 7. Select Water Claim and set CAT = NO chargeable = NO.
     **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6742, PAS-6695")
	public void pas6695_testClueReconciliationRenewal(@Optional("") String state) {
		pas6695_testClueClaimsReconciliationRenewal();

	}

    /**
     * @author Dominykas Razgunas, Josh Carpenter
     * @name Test lack of Dependency Between CAT And Chargeable CLUE Claim Mapping
     * @scenario
     * 1. Create policy with default test data (including customer)
     * 2. Create policy, cancel, and rewrite policy
     * 3. Add 2 named insured with claims (Virat and Silvia Kohli)
     * 4. Reorder CLUE, validate only full scope claims are populated on property info tab
     * 5. Select Hail Claim and set CAT = YES chargeable = NO
     * 6. Select Wind Claim and set CAT = YES chargeable = YES.
     * 7. Select Fire Claim and set CAT = NO chargeable = YES.
     * 8. Select Water Claim and set CAT = NO chargeable = NO.
     **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6742, PAS-6695")
	public void pas6695_testClueReconciliationRewrite(@Optional("") String state) {
		pas6695_testClueClaimsReconciliationRewrite();

	}

	/**
	 * @author Josh Carpenter
	 * @name Test chargeable CLUE claim mapping specifically when NI is claimant only in subject classification (not insured)
	 * @scenario
	 * 1. Create policy with customer "Agustin Miras"
	 * 2. Fill up to Property Info Tab
	 * 3. Validate no claims are showing
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6695")
	public void pas6695_testClueReconciliationClaimantOnly(@Optional("") String state) {
		pas6695_testClueClaimsReconciliationClaimantOnly();

	}

	/**
	 * @author Josh Carpenter
	 * @name Test chargeable CLUE claim mapping specifically when NI is insured only in subject classification (not claimant)
	 * @scenario
	 * 1. Create policy with customer "MARSHA LACKEY"
	 * 2. Fill up to Property Info Tab
	 * 3. Validate one claim is showing
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6695")
	public void pas6695_testClueClaimsReconciliationInsuredAndNotClaimant(@Optional("") String state) {
		pas6695_testClueClaimsReconciliationInsuredAndNotClaimant();

	}

	/**
	 * @author Josh Carpenter
	 * @name Test CLUE claim catastrophe indicator when 'Unknown' defaults to 'No'
	 * @scenario
	 * 1. Create policy with customer "Sachin Kohli"
	 * 2. Fill up to Property Info Tab
	 * 3. Validate two claims are showing
	 * 4. Validate both claims show catastrophe indicator = 'No'
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6703")
	public void pas6703_testCatastropheIndicatorUnknownNB(@Optional("") String state) {
		pas6703_testCatastropheIndicatorUnknownNB();

	}

}