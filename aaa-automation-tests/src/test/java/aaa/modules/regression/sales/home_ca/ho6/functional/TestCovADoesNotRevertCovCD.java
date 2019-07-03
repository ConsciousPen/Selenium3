package aaa.modules.regression.sales.home_ca.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.HomeCaHO6BaseTest;
import toolkit.utils.TestInfo;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestCovADoesNotRevertCovCD extends HomeCaHO6BaseTest {

    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    /**
     * @author Josh Carpenter
     * @name Test Coverage A does not revert Cov C or D to default values for CA HO6 new business
     * @scenario
     * 1. Create CA HO6 quote
     * 2. Fill up to Premium & Coverages tab
     * 3. Capture Cov A, C, D
     * 4. Increase/decrease Cov A
     * 5. Calculate premium and validate
     * @details
     **/
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-31629")
    public void pas31629_testCovCDoesNotRevertCovCD_NB(@Optional("CA") String state) {

        createQuoteAndFillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class);
        validateCovCDoesNotRevertCovCD();

    }

    /**
     * @author Josh Carpenter
     * @name Test Coverage A does not revert Cov C or D to default values for CA HO6 endorsements
     * @scenario
     * 1. Create CA HO6 policy
     * 2. Initiate endorsement
     * 3. Navigate to Property Info Tab
     * 4. Increase/decrease Cov A
     * 5. Calculate premium and validate
     * @details
     **/
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-31629")
    public void pas31629_testCovCDoesNotRevertCovCD_Endorsement(@Optional("CA") String state) {

        openAppAndCreatePolicy();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        validateCovCDoesNotRevertCovCD();

    }

    /**
     * @author Josh Carpenter
     * @name Test Coverage A does not revert Cov C or D to default values for CA HO6
     * @scenario
     * 1. Create CA HO6 policy
     * 2. Create renewal image
     * 3. Navigate to Property Info Tab
     * 4. Increase/decrease Cov A
     * 5. Calculate premium and validate
     * @details
     **/
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-31629")
    public void pas31629_testCovCDoesNotRevertCovCD_Renewal(@Optional("CA") String state) {

        openAppAndCreatePolicy();
        policy.renew().perform();
        validateCovCDoesNotRevertCovCD();

    }

    private void validateCovCDoesNotRevertCovCD() {
        // Capture current coverage values
        if (!premiumsAndCoveragesQuoteTab.btnCalculatePremium().isPresent()) {
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        }
        Dollar covA = new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_A).getValue());
        Dollar covC = new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C).getValue());
        Dollar covD = new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D).getValue());

        // Increase Cov A from original value
        changeCovA(covA, new Dollar (10000));

        // Validate Cov C and D have not changed
        assertThat(new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C).getValue())).isEqualTo(covC);
        assertThat(new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D).getValue())).isEqualTo(covD);

        // Decrease Cov A from original value
        changeCovA(covA, new Dollar (-10000));

        // Validate Cov C and D have not changed
        assertThat(new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C).getValue())).isEqualTo(covC);
        assertThat(new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D).getValue())).isEqualTo(covD);
    }

    private void changeCovA(Dollar covA, Dollar amount) {
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
        new PropertyInfoTab().getPropertyValueAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT).setValue(covA.add(amount).toPlaingString());
        new PropertyInfoTab().getPropertyValueAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST).setValue(covA.add(amount).toPlaingString());
        premiumsAndCoveragesQuoteTab.calculatePremium();
    }

}
