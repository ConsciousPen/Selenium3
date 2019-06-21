package aaa.modules.regression.sales.home_ss.ho3;

import static aaa.main.metadata.policy.HomeSSMetaData.ProductOfferingTab.*;
import static aaa.main.metadata.policy.HomeSSMetaData.ProductOfferingTab.VariationControls.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
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
 * <p> Created by lkazarnovskiy on 8/16/2017.
 * <p> <p>
 * <p> Objectives: Validate premium bundle functionality.
 * <p> <p>
 * <p> TC Steps:
 * <p> /*                         // Heritage Bundle
 * <p> 1. Create a quote
 * <p> 2. Test that only Heritage bundle is selected on Offering tab
 * <p> 3. Verify "Select Variation" button on Heritage bundle is disabled
 * <p> Verify "Remove Variation" button on Heritage bundle is disabled
 * <p> Verify "Restore Defaults" button on Heritage bundle is enabled
 * <p> Verify "Add additional variation" is disabled
 * <p> 4. Check List of all endorsements which are included to Heritage bundle on Offering tab.
 * <p> 5. Calculate Premium on Offering Tab
 * <p> 6. Verify Total Premium = Sub Total Coverage Premium + Endorsement Premium On Offering Tab
 * <p> 7. Check List of all endorsements which are included to Heritage bundle on Endorsement tab.
 * <p> 8. Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * <p> 9. Check List of all endorsements which are included to Heritage bundle on Quote tab.
 * <p> <p>
 * <p> <p>
 * <p> // Legacy Bundle
 * <p> <p>
 * <p> 11.Verify presence of Heritage and Legacy bundle and absence of Prestige bundle.
 * <p> 12.Verify "Select Variation" button on Legacy bundle is disabled
 * <p> Verify "Remove Variation" button on Legacy bundle is disabled
 * <p> Verify "Restore Defaults" button on Legacy bundle is enabled
 * <p> Verify "Select Variation" button on Heritage bundle is enabled
 * <p> Verify "Remove Variation" button on Heritage bundle is enabled
 * <p> Verify "Restore Defaults" button on Heritage bundle is enabled
 * <p> Verify "Add additional variation" is disabled
 * <p> 13. Check List of all endorsements which is included to Legacy bundle on Offering tab.
 * <p> 14. Calculate Premium on Offering Tab
 * <p> 15. Verify Total Premium = Sub Total Coverage Premium + Endorsement Premium On Offering Tab
 * <p> 16. Check List of all endorsements which is included to Legacy bundle on Endorsement tab.
 * <p> 17. Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * <p> 18. Check List of all endorsements which is included to Legacy bundle on Quote tab.
 * <p> <p>
 * <p> // Prestige Bundle
 * <p> 19. Set conditions for Prestige bundle (Insurance Score = 870 and A Coverage = 280 000)
 * <p> 20. Verify  Heritage,Legacy,Prestige bundles are present on Offering Tab
 * <p> 21. Verify "Select Variation" button on Prestige bundle is disabled
 * <p> Verify "Remove Variation" button on Prestige bundle is disabled
 * <p> Verify "Restore Defaults" button on Prestige bundle is enabled
 * <p> Verify "Select Variation" button on Heritage bundle is enabled
 * <p> Verify "Remove Variation" button on Heritage bundle is enabled
 * <p> Verify "Restore Defaults" button on Heritage bundle is enabled
 * <p> Verify "Select Variation" button on Legacy bundle is enabled
 * <p> Verify "Remove Variation" button on Legacy bundle is enabled
 * <p> Verify "Restore Defaults" button on Legacy bundle is enabled
 * <p> Verify "Add additional variation" is disabled
 * <p> 22.Check List of all endorsements which is included to Prestige bundle on Offering tab.
 * <p> 23.Calculate Premium on Offering Tab
 * <p> 24.Verify Total Premium = Sub Total Coverage Premium + Endorsement Premium On Offering Tab
 * <p> 25.Check List of all endorsements which is included to Prestige bundle on Endorsement tab.
 * <p> 26.Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * <p> 27.Check List of all endorsements which is included to Prestige bundle on Quote tab.
 * <p> <p>
 * <p> 28.Select Legacy bundle.
 * <p> 29.Calculate Premium on Offering Tab
 * <p> 30.Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * <p> 31.Verify restore defaults on Legacy Bundle:  -> !!!Added in the block for Legacy Bundle tests
 * <p> Set coverages:
 * <p> CovB = "20%";
 * <p> CovD = "40%";
 * <p> CovE = "$200,000";
 * <p> CovF = "$2,000";
 * <p> Ded =  "$2,000";
 * <p> Calculate premium.
 * <p> Verify it's not equal to initial premium.
 * <p> Press "Restore Defaults"
 * <p> Calculate premium.
 * <p> Verify calculated premium is equal to initial premium.
 * <p> <p>
 * <p> 32.Select Heritage bundle.   -> !!!Added in the block for Heritage Bundle tests
 * <p> 33.Calculate Premium on Offering Tab -> !!!Added in the block for Heritage Bundle tests
 * <p> 34.Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * <p> 35.Verify restore defaults on Heritage Bundle
 * <p> <p>
 * <p> 36.Select Prestige bundle.
 * <p> 37.Calculate Premium on Offering Tab
 * <p> 38.Verify that Total Premium of selected bundle  = Total premium on Quote tab.
 * <p> 39.Verify restore defaults on Prestige Bundle
 * <p> <p>
 * <p> 40.Remove Legacy Bundle
 * <p> 41.Verify that only Heritage and Prestige bundles are present
 * <p> 42.Add Legacy bundle back (press "Add additional variation" button)
 * <p> 43.Verify Heritage,Legacy,Prestige bundles are present on Offering Tab
 * <p> <p>
 * <p> Req: 224895: 13928: US NB-MTC - Quote Comparison 18642: US CL NB-MTC
 * <p> - Quote Comparison V2.0
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
		 * <p> Heritage Bundle verification
		 * <p> Steps 1-9, 32-35
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
		 * <p> Legacy Bundle verification
		 * <p> Steps 11-18, 28-31
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
		 * <p> Prestige Bundle verification
		 * <p> Steps 19-27, 36-39
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
		 * <p> 40.Remove Legacy Bundle
		 * <p> 41.Verify that only Heritage and Prestige bundles are present
		 * <p> 42.Add Legacy bundle back (press "Add additional variation" button)
		 * <p> 43.Verify Heritage,Legacy,Prestige bundles are present on Offering Tab
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