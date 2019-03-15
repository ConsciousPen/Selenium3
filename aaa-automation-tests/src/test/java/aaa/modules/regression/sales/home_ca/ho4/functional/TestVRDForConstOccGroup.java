package aaa.modules.regression.sales.home_ca.ho4.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaHO4BaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import static aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab.RatingDetailsView.propertyInformation;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@StateList(states = Constants.States.CA)
public class TestVRDForConstOccGroup extends HomeCaHO4BaseTest {

    private ApplicantTab applicantTab = new ApplicantTab();
    private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    /**
     * @author Sreekanth Kopparapu
     * @name Test Construction occupancy group on VRD for CA HO4
     * @scenario
     * 1. Create a Quote for CA HO4 and on Property Info tab - Dwelling Address - Num of Family Units - Single..
     * 2. Navigate to P*C Quote tab, calculate Premium and assert value on VRD - Property Information - Construction Occupancy Group to be "CO1"
     * 3. Bind the policy.
     * 2. Initiate an endorsement and on property Info tab, update Dwelling address - Number of Family units to "61+"
     * 4. calculate Premium and assert value on VRD - Property Information - Construction Occupancy Group to be "CO2"
     * 5. Bind the endorsement
     * * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4, testCaseId = "26291")
    public void pas26291_TestVRDForConstOccGroupEndTx(@Optional("CA") String state) {

       createQuoteAndPolicy();
        //Endorse the policy to update Number of Family Units to 61+ and validate VRD for Construction Occupancy Group
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus2Month"));
       //CCG - Construction Occupancy Group on VRD
        validateVRDForCOG();
        policy.getDefaultView().fillFromTo(getPolicyTD(), MortgageesTab.class, BindTab.class, true);
        new BindTab().submitTab();
        policy.renew().perform();
        calculatePremiumAndOpenVRD();
        new MortgageesTab().saveAndExit();
    }

    private void createQuoteAndPolicy(){

        TestData td = getPolicyTD().adjust(TestData.makeKeyPath(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(), HomeCaMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(),
                HomeCaMetaData.PropertyInfoTab.PropertyValue.PERSONAL_PROPERTY_VALUE.getLabel()), "50000");

        createQuoteAndFillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        PropertyQuoteTab.RatingDetailsView.open();
        assertThat(propertyInformation.getValueByKey("Construction occupancy group")).contains("CO1");
        PropertyQuoteTab.RatingDetailsView.close();
        premiumsAndCoveragesQuoteTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), MortgageesTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }

    private void validateVRDForCOG() {
        TestData td = getPolicyTD().adjust(applicantTab.getClass().getSimpleName(), getTestSpecificTD("ApplicantTab"))
                .adjust(TestData.makeKeyPath(HomeCaMetaData.ReportsTab.class.getSimpleName(), HomeCaMetaData.ReportsTab.PUBLIC_PROTECTION_CLASS.getLabel() + "[0]",
                        HomeCaMetaData.ReportsTab.PublicProtectionClassRow.REPORT.getLabel()), "Re-order report")
                .mask(TestData.makeKeyPath(HomeCaMetaData.ReportsTab.class.getSimpleName(), HomeCaMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel()))
                .mask(TestData.makeKeyPath(HomeCaMetaData.ReportsTab.class.getSimpleName(), HomeCaMetaData.ReportsTab.CLUE_REPORT.getLabel()));

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        policy.getDefaultView().fillFromTo(td, ApplicantTab.class, PropertyInfoTab.class);
        propertyInfoTab.getDwellingAddressAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS).setValue("61+");
        calculatePremiumAndOpenVRD();
    }

    private void calculatePremiumAndOpenVRD(){
        premiumsAndCoveragesQuoteTab.calculatePremium();
        PropertyQuoteTab.RatingDetailsView.open();
        assertThat(propertyInformation.getValueByKey("Construction occupancy group")).isEqualTo("CO2");
        PropertyQuoteTab.RatingDetailsView.close();
        premiumsAndCoveragesQuoteTab.submitTab();
    }

}
