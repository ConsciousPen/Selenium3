package aaa.modules.regression.sales.home_ca.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestCAFairPlanRating extends HomeCaHO3BaseTest {
    Map<String, String> endorsement_FPCECA = new HashMap<>();

    /**
     * @author Robert Boles
     * @name Test CA FAIR Plan Rating - PAS-13215 (AC#1_HO3)
     * @scenario
     * 1. Create new Customer;
     * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO3;
     * 3. Fill all mandatory fields;
     * 4. Calculate premium without FAIR Plan Endorsement;
     * 5. Add FAIR Plan Companion endorsement;
     * 6. Calculate premium again;
     * 7. Validate that the premium calculated after the FAIR Plan Companion endorsements is added is reduced;
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PAS-13215 18.5: CA FAIR Plan: Rating HO3")
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO3, testCaseId = "PAS-13215")
    public void testPolicyRateFairPlanEndorsement(@Optional("CA") String state) {
        testSetup();
        validateAC1();
    }

    /**
     * @author Robert Boles
     * @name Test CA FAIR Plan Rating - PAS-13215 (AC#2_HO3)
     * @scenario
     * 1. Create new Customer;
     * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO3;
     * 3. Fill all mandatory fields;
     * 4. Navigate to Premium & Coverages tab;
     * 5. Add FAIR Plan Companion endorsement;
     * 6. Click on 'View rating Details' link;
     * 7. Validate that the the PPC value of '1' and Fireline score of '0' should be displayed regardless of the values returned by the PPC and Fireline services;
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PAS-13215 18.5: CA FAIR Plan: Rating HO3")
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO3, testCaseId = "PAS-13215")
    public void testPolicyRateFairPlanEndorsementAC2(@Optional("CA") String state) {
        testSetup();
        validateAC2();
    }

    /**
     * @author Robert Boles
     * @name Test CA FAIR Plan Rating - PAS-13215 (AC#4_HO3)
     * @scenario
     * 1. Create new Customer;
     * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO3;
     * 3. Fill all mandatory fields and ensure Theft protective device and  Fire protective device are checked;
     * 4. Add FAIR Plan Companion endorsement;
     * 5. Click on 'View rating Details' link;
     * 6. Navigate to Premium & Coverages tab and confirm Discount = Local on VRD;
     * 7. Validate that the Smoke and burglar alarm discount factor = 0.0 on VRD;
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
    public void testPolicyRateFairPlanEndorsementAC4(@Optional("CA") String state) {
        testSetup();
        validateAC4();
    }


    public void testSetup() {
        mainApp().open();
        createCustomerIndividual();
        policy.createQuote(getPolicyTD());
    }

    public void validateAC1() {
        endorsement_FPCECA.put("Form ID", "FPCECA");
        endorsement_FPCECA.put("Name", "FAIR Plan Companion Endorsement - California");

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());

        //Verify the ENDo is not selected already
        EndorsementTab endorsementTab = new EndorsementTab();
        assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_FPCECA).isPresent()).isTrue();

        //Scrape the total premium from the Prem summary asset list on PnC tab prior to Fair Plan Endo
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        Dollar preEndoPremium = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();

        //Add the ENDO and verify presence
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();
        endorsementTab.btnSaveEndo.click();
        //AC3 - This confirms an Informational note will display notifying the user that this endorsement has been added
	    assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_FPCECA)).isPresent();

        //Verify premium is reduced after
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().btnCalculatePremium().click();
        Dollar postEndoPremium = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
        assertThat(postEndoPremium.lessThan(preEndoPremium)).isTrue();
        mainApp().close();
    }

    public void validateAC2() {
        endorsement_FPCECA.put("Form ID", "FPCECA");
        endorsement_FPCECA.put("Name", "FAIR Plan Companion Endorsement - California");

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        //Verify the ENDo is not selected already
        EndorsementTab endorsementTab = new EndorsementTab();

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().btnCalculatePremium().click();
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        //Verify the Fireline and PPC prior to endo
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Protection class")).contains("5");
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Fireline score")).contains("0");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

        //Add the ENDO and verify presence
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();

        endorsementTab.btnSaveEndo.click();
	    assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_FPCECA)).isPresent();

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().btnCalculatePremium().click();
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Protection class")).contains("1");
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Fireline score")).contains("0");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }

    public void validateAC4() {
        endorsement_FPCECA.put("Form ID", "FPCECA");
        endorsement_FPCECA.put("Name", "FAIR Plan Companion Endorsement - California");

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());

        //Add Theft / Fire Alarm
        new PropertyInfoTab().getTheftProtectiveTPDDAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.LOCAL_THEFT_ALARM).setValue(Boolean.TRUE);
        new PropertyInfoTab().getFireProtectiveDDAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.FireProtectiveDD.LOCAL_FIRE_ALARM).setValue(Boolean.TRUE);

        //Verify the Endo is not selected already prior to test
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        EndorsementTab endorsementTab = new EndorsementTab();
        assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_FPCECA)).isPresent();

        // Verify Discount for alarm is retained
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().btnCalculatePremium().click();
        assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell("Discounts applied")).hasValue("New home, AAA Membership, Smoke and Burglar alarm");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Smoke and burglar alarm (Central, Local, None)")).isEqualTo("Local");
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Smoke and burglar alarm discount factor")).isEqualTo("0.95");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

        //Add the ENDO and verify presence
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();
        endorsementTab.btnSaveEndo.click();
        assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_FPCECA)).isPresent();

        //Verify Discount for alarm is removed with FAIR PLAN
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().btnCalculatePremium().click();

        assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Smoke and Burgler alarm");
        //VRD is accurate
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Smoke and burglar alarm (Central, Local, None)")).isEqualTo("Local");
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Smoke and burglar alarm discount factor")).isEqualTo("0.0");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }

}