package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    @Test(groups = { Groups.FUNCTIONAL, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
    public void testPolicyRateFairPlanEndorsement(@Optional("CA") String state) {
        testSetup();
        validateAC1();
    }

    /**
     * @author Robert Boles
     * @name Test CA FAIR Plan Rating - PAS-13215 (AC#2_HO3)
     * @scenario
     * @precondition - use a zipcode that will return a PPC and Fireline other than 1 and 0
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
    @Test(groups = { Groups.FUNCTIONAL, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
    public void testPolicyRateFairPlanEndorsementAC2(@Optional("CA") String state) {
        testSetup();
        validateAC2();
    }

    public void testSetup() {
        mainApp().open();
        createCustomerIndividual();
        policy.createQuote(getPolicyTD());
    }

    public void validateAC1() {
        endorsement_FPCECA.put("Form ID", "FPCECA");
        endorsement_FPCECA.put("Name", "Fair Plan Companion Endorsement - California");

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());

        //Verify the ENDo is not selected already
        aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab endorsementTab = new aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab();
        endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_FPCECA).verify.present(true);

        //Scrape the total premium from the Prem summary asset list on PnC tab prior to Fair Plan Endo
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        Dollar preEndoPremium = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();

        //Add the ENDO and verify presence
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();
        //AC3 - This confirms an Informational note will display notifying the user that this endorsement has been added
        Page.dialogConfirmation.confirm();

        endorsementTab.btnSaveForm.click();
        endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_FPCECA).verify.present(true);

        //Verify premium is reduced after
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().btnCalculatePremium().click();
        Dollar postEndoPremium = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();

        CustomAssert.assertTrue(postEndoPremium.lessThan(preEndoPremium));
        mainApp().close();
    }

    public void validateAC2() {
        endorsement_FPCECA.put("Form ID", "FPCECA");
        endorsement_FPCECA.put("Name", "Fair Plan Companion Endorsement - California");

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        //Verify the ENDo is not selected already
        aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab endorsementTab = new aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab();

        //Add the ENDO and verify presence
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();
        //AC3 - This confirms an Informational note will display notifying the user that this endorsement has been added
        Page.dialogConfirmation.confirm();

        endorsementTab.btnSaveForm.click();
        endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_FPCECA).verify.present(true);

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().btnCalculatePremium().click();
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Protection class").contains("1"));
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Fireline score").contains("0"));
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }
}
