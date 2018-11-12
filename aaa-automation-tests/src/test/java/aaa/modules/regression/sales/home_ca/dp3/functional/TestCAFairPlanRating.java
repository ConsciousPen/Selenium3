package aaa.modules.regression.sales.home_ca.dp3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;


@StateList(states = Constants.States.CA)
public class TestCAFairPlanRating extends HomeCaDP3BaseTest {
    Map<String, String> endorsement_FPCECADP = new HashMap<>();

    /**
     * @author Robert Boles
     * @name Test CA FAIR Plan Rating - PAS-13215 (AC#1_DP3)
     * @scenario
     * 1. Create new Customer;
     * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=DP3;
     * 3. Fill all mandatory fields;
     * 4. Calculate premium without FAIR Plan Endorsement;
     * 5. Add FAIR Plan Companion endorsement;
     * 6. Calculate premium again;
     * 7. Validate that the premium calculated after the FAIR Plan Companion endorsements is added is reduced;
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "AC1 PAS-13215 18.5: CA FAIR Plan: Rating DP3")
    @TestInfo(component = ComponentConstant.Service.HOME_CA_DP3, testCaseId = "PAS-13215")
    public void testPolicyRateFairPlanEndorsement(@Optional("CA") String state) {
        testSetup();
        validateAC1();
    }

    /**
     * @author Robert Boles
     * @name Test CA FAIR Plan Rating - PAS-13215 (AC#2_DP3)
     * @scenario
     * @precondition - use a zipcode that will return a PPC and Fireline other than 1 and 0
     * 1. Create new Customer;
     * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=DP3;
     * 3. Fill all mandatory fields;
     * 4. Navigate to Premium & Coverages tab;
     * 5. Add FAIR Plan Companion endorsement;
     * 6. Click on 'View rating Details' link;
     * 7. Validate that the the PPC value of '1' and Fireline score of '0' should be displayed regardless of the values returned by the PPC and Fireline services;
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "AC2 PAS-13215 18.5: CA FAIR Plan: Rating DP3")
    @TestInfo(component = ComponentConstant.Service.HOME_CA_DP3, testCaseId = "13215")
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

        endorsement_FPCECADP.put("Form ID", "FPCECADP");
        endorsement_FPCECADP.put("Name", "FAIR Plan Companion Endorsement - California");

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());

        //Verify the ENDo is not selected already
        EndorsementTab endorsementTab = new EndorsementTab();
        assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_FPCECADP).isPresent()).isTrue();

        //Scrape the total premium from the Prem summary asset list on PnC tab prior to Fair Plan Endo
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        Dollar preEndoPremium = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();

        //Add the ENDO and verify presence
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECADP.getLabel()).click();
        endorsementTab.btnSaveEndo.click();
        //AC3 - This confirms an Informational note will display notifying the user that this endorsement has been added
	    assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_FPCECADP).isPresent()).isTrue();

        //Verify premium is reduced after
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().btnCalculatePremium().click();
        Dollar postEndoPremium = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
        assertThat(postEndoPremium.lessThan(preEndoPremium)).isTrue();

    }

    public void validateAC2() {
        endorsement_FPCECADP.put("Form ID", "FPCECADP");
        endorsement_FPCECADP.put("Name", "FAIR Plan Companion Endorsement - California");

        new HomeCaPolicyActions.DataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        //Verify the ENDo is not selected already
        EndorsementTab endorsementTab = new EndorsementTab();

        //Add the ENDO and verify presence
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECADP.getLabel()).click();

        endorsementTab.btnSaveEndo.click();
	    assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_FPCECADP)).isPresent();

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().btnCalculatePremium().click();
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Protection class")).contains("1");
        assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Fireline score")).contains("0");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

    }
}