package aaa.modules.regression.sales.home_ss.ho3;

import static aaa.main.metadata.policy.HomeSSMetaData.ProductOfferingTab.*;
import static aaa.main.metadata.policy.HomeSSMetaData.ProductOfferingTab.VariationControls.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ProductOfferingTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


/**
 * Created by lkazarnovskiy on 8/16/2017.
 * <p>
 * Objectives: Validate premium bundle functionality.
 * <p>
 * TC Steps:
 * /*                         // Heritage Bundle
 * 1. Create a quote
 * 2. Test that only Heritage bundle is selected on Offering tab
 * 3. Verify "Select Variation" button on Heritage bundle is disabled
 * Verify "Remove Variation" button on Heritage bundle is disabled
 * Verify "Restore Defaults" button on Heritage bundle is enabled
 * Verify "Add additional variation" is disabled
 * 4. Check List of all endorsements which are included to Heritage bundle on Offering tab.
 * 5. Calculate Premium on Offering Tab
 * 6. Verify Total Premium = Sub Total Coverage Premium + Endorsement Premium On Offering Tab
 * 7. Check List of all endorsements which are included to Heritage bundle on Endorsement tab.
 * 8. Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * 9. Check List of all endorsements which are included to Heritage bundle on Quote tab.
 * <p>
 * <p>
 * // Legacy Bundle
 * <p>
 * 11.Verify presence of Heritage and Legacy bundle and absence of Prestige bundle.
 * 12.Verify "Select Variation" button on Legacy bundle is disabled
 * Verify "Remove Variation" button on Legacy bundle is disabled
 * Verify "Restore Defaults" button on Legacy bundle is enabled
 * Verify "Select Variation" button on Heritage bundle is enabled
 * Verify "Remove Variation" button on Heritage bundle is enabled
 * Verify "Restore Defaults" button on Heritage bundle is enabled
 * Verify "Add additional variation" is disabled
 * 13. Check List of all endorsements which is included to Legacy bundle on Offering tab.
 * 14. Calculate Premium on Offering Tab
 * 15. Verify Total Premium = Sub Total Coverage Premium + Endorsement Premium On Offering Tab
 * 16. Check List of all endorsements which is included to Legacy bundle on Endorsement tab.
 * 17. Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * 18. Check List of all endorsements which is included to Legacy bundle on Quote tab.
 * <p>
 * // Prestige Bundle
 * 19. Set conditions for Prestige bundle (Insurance Score = 870 and A Coverage = 280 000)
 * 20. Verify  Heritage,Legacy,Prestige bundles are present on Offering Tab
 * 21. Verify "Select Variation" button on Prestige bundle is disabled
 * Verify "Remove Variation" button on Prestige bundle is disabled
 * Verify "Restore Defaults" button on Prestige bundle is enabled
 * Verify "Select Variation" button on Heritage bundle is enabled
 * Verify "Remove Variation" button on Heritage bundle is enabled
 * Verify "Restore Defaults" button on Heritage bundle is enabled
 * Verify "Select Variation" button on Legacy bundle is enabled
 * Verify "Remove Variation" button on Legacy bundle is enabled
 * Verify "Restore Defaults" button on Legacy bundle is enabled
 * Verify "Add additional variation" is disabled
 * 22.Check List of all endorsements which is included to Prestige bundle on Offering tab.
 * 23.Calculate Premium on Offering Tab
 * 24.Verify Total Premium = Sub Total Coverage Premium + Endorsement Premium On Offering Tab
 * 25.Check List of all endorsements which is included to Prestige bundle on Endorsement tab.
 * 26.Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * 27.Check List of all endorsements which is included to Prestige bundle on Quote tab.
 * <p>
 * 28.Select Legacy bundle.
 * 29.Calculate Premium on Offering Tab
 * 30.Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * 31.Verify restore defaults on Legacy Bundle:  -> !!!Added in the block for Legacy Bundle tests
 * Set coverages:
 * CovB = "20%";
 * CovD = "40%";
 * CovE = "$200,000";
 * CovF = "$2,000";
 * Ded =  "$2,000";
 * Calculate premium.
 * Verify it's not equal to initial premium.
 * Press "Restore Defaults"
 * Calculate premium.
 * Verify calculated premium is equal to initial premium.
 * <p>
 * 32.Select Heritage bundle.   -> !!!Added in the block for Heritage Bundle tests
 * 33.Calculate Premium on Offering Tab -> !!!Added in the block for Heritage Bundle tests
 * 34.Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * 35.Verify restore defaults on Heritage Bundle
 * <p>
 * 36.Select Prestige bundle.
 * 37.Calculate Premium on Offering Tab
 * 38.Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * 39.Verify restore defaults on Prestige Bundle
 * <p>
 * 40.Remove Legacy Bundle
 * 41.Verify that only Heritage and Prestige bundles are present
 * 42.Add Legacy bundle back (press "Add additional variation" button)
 * 43.Verify Heritage,Legacy,Prestige bundles are present on Offering Tab
 * <p>
 * Req: 224895: 13928: US NB-MTC - Quote Comparison 18642: US CL NB-MTC
 * - Quote Comparison V2.0
 **/
public class TestQuoteComparison extends HomeSSHO3BaseTest {


	@Parameters({"state"})
	@StateList(states = { States.AZ, States.UT })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuoteComparison(@Optional("") String state) {

		TestData td = getPolicyTD();
		ProductOfferingTab productOfferingTab = new ProductOfferingTab();
		EndorsementTab endorsementTab = new EndorsementTab();
		List<String> includedEndorsements;
		Dollar defaultTotalPremium;
		Dollar modifiedTotalPremium;

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, ProductOfferingTab.class);

		/*
		 * Heritage Bundle verification
		 * Steps 1-9, 32-35
		 */

		//Steps 2-3
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE).isVariationSelected()).isTrue();
		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY).isVariationSelected()).isFalse();
		assertThat(productOfferingTab.getAssetList().getAsset(PRESTIGE).isVariationSelected()).isFalse();
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE).getAsset(SELECT_VARIATION)).isPresent(false);
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE).getAsset(REMOVE_VARIATION)).isPresent(false);
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE).getAsset(RESTORE_DEFAULTS)).isPresent();
		assertThat(productOfferingTab.btnAddAdditionalVariation).isEnabled(false);
		//5. Calculate Premium on Offering Tab
		productOfferingTab.calculatePremium();
		//6. Verify Total Premium = Sub Total Coverage Premium + Endorsement Premium On Offering Tab
		assertThat(ProductOfferingTab.isTotalPremiumCalculatedProperly(HERITAGE)).isTrue();
		//Saving of default premium for bundle with default values
		defaultTotalPremium = ProductOfferingTab.getTotalPremium(HERITAGE);

		productOfferingTab.getAssetList().getAsset(HERITAGE).setValue(getTestSpecificTD("TestData"));
		productOfferingTab.calculatePremium();

		//Saving of modified premium for bundle with modified values
		modifiedTotalPremium = ProductOfferingTab.getTotalPremium(HERITAGE);
		assertThat(modifiedTotalPremium).isNotEqualTo(defaultTotalPremium);
		//Saving the list of Included in bundle endorsements (except State-specific endorsements)
		includedEndorsements = ProductOfferingTab.getIncludedEndorsementList(HERITAGE);

		productOfferingTab.submitTab();
		endorsementTab.fillTab(td);
		//Step 7. Verification of the List of all endorsements which are included to Heritage bundle on Endorsement tab.
		assertThat(endorsementTab.tblIncludedEndorsements.getColumn("Form ID").getValue().containsAll(includedEndorsements)).isTrue();
		endorsementTab.submitTab();
		//34.Verify that modified Total Premium of selected bundle  = Total premium on Quote tab.
		assertThat(modifiedTotalPremium).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());
		//35.Verify restore defaults on Legacy Bundle
		productOfferingTab.getAssetList().getAsset(HERITAGE).restoreDefaults();
		productOfferingTab.calculatePremium();
		//Verify calculated premium is equal to initial premium.
		assertThat(defaultTotalPremium).isEqualTo(ProductOfferingTab.getTotalPremium(HERITAGE));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		assertThat(defaultTotalPremium).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
		//Step 9. Verification of the List of all endorsements which are included to Heritage bundle on PremiumsAndCoveragesQuoteTab.
		verifyIncludedEndorsementsAddedOnPremiumAndCoveragesQuoteTab(includedEndorsements);
		log.info("Heritage bundle is verified successfully");

		/*
		 * Legacy Bundle verification
		 * Steps 11-18, 28-31
		 */
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());
		productOfferingTab.getAssetList().getAsset(LEGACY).selectVariation();

		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY).isVariationSelected()).isTrue();
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE).isVariationSelected()).isFalse();
		assertThat(productOfferingTab.getAssetList().getAsset(PRESTIGE).isVariationSelected()).isFalse();
		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY).getAsset(SELECT_VARIATION)).isPresent(false);
		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY).getAsset(REMOVE_VARIATION)).isPresent(false);
		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY).getAsset(RESTORE_DEFAULTS)).isPresent();
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE).getAsset(SELECT_VARIATION)).isPresent();
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE).getAsset(REMOVE_VARIATION)).isPresent();
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE).getAsset(RESTORE_DEFAULTS)).isPresent();

		assertThat(productOfferingTab.btnAddAdditionalVariation).isEnabled(false);


		//15. Calculate Premium on Offering Tab
		productOfferingTab.calculatePremium();
		//16. Verify Total Premium = Sub Total Coverage Premium + Endorsement Premium On Offering Tab
		assertThat(ProductOfferingTab.isTotalPremiumCalculatedProperly(LEGACY)).isTrue();

		//Saving of defalut premium for bundle with default values
		defaultTotalPremium = ProductOfferingTab.getTotalPremium(LEGACY);

		productOfferingTab.getAssetList().getAsset(LEGACY).setValue(getTestSpecificTD("TestData"));
		productOfferingTab.calculatePremium();

		//Saving of modified premium for bundle with modified values
		modifiedTotalPremium = ProductOfferingTab.getTotalPremium(LEGACY);
		assertThat(modifiedTotalPremium).isNotEqualTo(defaultTotalPremium);
		//Saving the list of Included in bundle endorsements (except State-specific endorsements)
		includedEndorsements = ProductOfferingTab.getIncludedEndorsementList(LEGACY);

		productOfferingTab.submitTab();
		endorsementTab.fillTab(td);
		//Step 16. Verification of the List of all endorsements which are included to Legacy bundle on Endorsement tab.
		assertThat(endorsementTab.tblIncludedEndorsements.getColumn("Form ID").getValue().containsAll(includedEndorsements)).isTrue();
		endorsementTab.submitTab();
		//30. Verify that Total Premium of selected bundle  = Total premium on Quote tab.
		assertThat(modifiedTotalPremium).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());
		//31.Verify restore defaults on Legacy Bundle
		productOfferingTab.getAssetList().getAsset(LEGACY).restoreDefaults();
		productOfferingTab.calculatePremium();
		//Verify calculated premium is equal to initial premium.
		assertThat(defaultTotalPremium).isEqualTo(ProductOfferingTab.getTotalPremium(LEGACY));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		assertThat(defaultTotalPremium).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
		//Step 18. Verification of the List of all endorsements which are included to Legacy bundle on PremiumsAndCoveragesQuoteTab.
		verifyIncludedEndorsementsAddedOnPremiumAndCoveragesQuoteTab(includedEndorsements);
		log.info("Legacy bundle is verified successfully");

		/*
		 * Prestige Bundle verification
		 * Steps 19-27, 36-39
		 */
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());
		productOfferingTab.getAssetList().getAsset(PRESTIGE).selectVariation();

		assertThat(productOfferingTab.getAssetList().getAsset(PRESTIGE).isVariationSelected()).isTrue();
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE).isVariationSelected()).isFalse();
		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY).isVariationSelected()).isFalse();
		assertThat(productOfferingTab.getAssetList().getAsset(PRESTIGE).getAsset(SELECT_VARIATION)).isPresent(false);
		assertThat(productOfferingTab.getAssetList().getAsset(PRESTIGE).getAsset(REMOVE_VARIATION)).isPresent(false);
		assertThat(productOfferingTab.getAssetList().getAsset(PRESTIGE).getAsset(RESTORE_DEFAULTS)).isPresent();
		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY).getAsset(SELECT_VARIATION)).isPresent();
		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY).getAsset(REMOVE_VARIATION)).isPresent();
		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY).getAsset(RESTORE_DEFAULTS)).isPresent();

		assertThat(productOfferingTab.btnAddAdditionalVariation).isEnabled(false);

		//24. Calculate Premium on Offering Tab
		productOfferingTab.calculatePremium();
		//25. Verify Total Premium = Sub Total Coverage Premium + Endorsement Premium On Offering Tab
		assertThat(ProductOfferingTab.isTotalPremiumCalculatedProperly(PRESTIGE)).isTrue();

		defaultTotalPremium = ProductOfferingTab.getTotalPremium(PRESTIGE);

		productOfferingTab.getAssetList().getAsset(PRESTIGE).setValue(getTestSpecificTD("TestData"));
		productOfferingTab.calculatePremium();

		//Saving of modified premium for bundle with modified values
		modifiedTotalPremium = ProductOfferingTab.getTotalPremium(PRESTIGE);
		assertThat(modifiedTotalPremium).isNotEqualTo(defaultTotalPremium);
		//Saving the list of Included in bundle endorsements (except State-specific endorsements)
		includedEndorsements = ProductOfferingTab.getIncludedEndorsementList(PRESTIGE);

		productOfferingTab.submitTab();
		endorsementTab.fillTab(td);
		//Step 25. Verification of the List of all endorsements which are included to Prestige bundle on Endorsement tab.
		assertThat(new EndorsementTab().tblIncludedEndorsements.getColumn("Form ID").getValue().containsAll(includedEndorsements)).isTrue();
		endorsementTab.submitTab();
		//38.Verify that Total Premium of selected bundle  = Total premium on Quote tab.
		assertThat(modifiedTotalPremium).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());
		//39.Verify restore defaults on Prestige Bundle
		productOfferingTab.getAssetList().getAsset(PRESTIGE).restoreDefaults();
		productOfferingTab.calculatePremium();
		//Verify calculated premium is equal to initial premium.
		assertThat(defaultTotalPremium).isEqualTo(ProductOfferingTab.getTotalPremium(PRESTIGE));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		assertThat(defaultTotalPremium).isEqualTo(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
		//Step 27. Verification of the List of all endorsements which are included to Prestige bundle on PremiumsAndCoveragesQuoteTab.
		verifyIncludedEndorsementsAddedOnPremiumAndCoveragesQuoteTab(includedEndorsements);
		log.info("Prestige bundle is verified successfully");

		/*
		 * 40.Remove Legacy Bundle
		 * 41.Verify that only Heritage and Prestige bundles are present
		 * 42.Add Legacy bundle back (press "Add additional variation" button)
		 * 43.Verify Heritage,Legacy,Prestige bundles are present on Offering Tab
		 */
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());
		productOfferingTab.getAssetList().getAsset(LEGACY).removeVariation();

		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY)).isPresent(false);
		assertThat(productOfferingTab.getAssetList().getAsset(PRESTIGE)).isPresent();
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE)).isPresent();

		productOfferingTab.getAssetList().getAsset(LEGACY).addVariation();

		assertThat(productOfferingTab.getAssetList().getAsset(LEGACY)).isPresent();
		assertThat(productOfferingTab.getAssetList().getAsset(PRESTIGE)).isPresent();
		assertThat(productOfferingTab.getAssetList().getAsset(HERITAGE)).isPresent();

		policy.getDefaultView().fillFromTo(td, ProductOfferingTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TestQuoteComparison is passed with policy#" + PolicySummaryPage.labelPolicyNumber.getValue());
	}

	private void verifyIncludedEndorsementsAddedOnPremiumAndCoveragesQuoteTab(List<String> includedEndorsementList) {
		List<String> temp = PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getColumn("Description").getValue();
		List<String> addedOnTabEndorsementsList = temp.stream().map(e -> e.substring(0, 8)).collect(Collectors.toList());
		assertThat(addedOnTabEndorsementsList.containsAll(includedEndorsementList)).isTrue();
	}
}