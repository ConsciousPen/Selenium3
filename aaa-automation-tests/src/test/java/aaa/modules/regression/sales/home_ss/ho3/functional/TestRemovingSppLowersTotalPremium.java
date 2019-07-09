package aaa.modules.regression.sales.home_ss.ho3.functional;

import static aaa.common.enums.NavigationEnum.HomeSSTab.*;
import static aaa.main.metadata.policy.HomeSSMetaData.DocumentsTab.DocumentsToBind.APPRAISALS_SALES_RECEIPTS_FOR_SCHEDULED_PROPERTY;
import static aaa.main.metadata.policy.HomeSSMetaData.EndorsementTab.HS_04_61;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PersonalPropertyTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.toolkit.webdriver.customcontrols.PersonalPropertyMultiAssetList;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestRemovingSppLowersTotalPremium extends HomeSSHO3BaseTest {

    private PersonalPropertyTab personalPropertyTab = new PersonalPropertyTab();
    private EndorsementTab endorsementTab = new EndorsementTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    /**
     * @author Igor Garkusha
     * @name Test HO3 Removing SPP doesn't lower total premium
     * @scenario 1.  Create policy with Scheduled Personal Property
     * 2.  Create Endorse
     * 3.  Check Boundary conditions +/-1 day
     * 4.  Remove the Scheduled Personal Property from “Other Endorsement” tab by selecting remove on the endorsement
     * 5.  Recalculate premium and validate if the Actual premium is subtracting the SPP amount
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-5847")
    public void pas5847_removingSPPDoesNotLowerTotalPremium(@Optional("NJ") String state) {

        mainApp().open();
        createCustomerIndividual();

        // adjust default testdata with testdata from ho3(re-usable in ho6 test)
        TestData testTd = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
        testTd.adjust(TestData.makeKeyPath(HomeSSMetaData.DocumentsTab.class.getSimpleName(),
                APPRAISALS_SALES_RECEIPTS_FOR_SCHEDULED_PROPERTY.getLabel()), "true");
        testTd = testTd.resolveLinks();
        createPolicy(testTd);

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        Arrays.asList(PREMIUMS_AND_COVERAGES, ENDORSEMENT, PREMIUMS_AND_COVERAGES_ENDORSEMENT_SCHEDULED_PERSONAL_PROPERTY).
                forEach(tab -> NavigationPage.toViewTab(tab.get()));

        personalPropertyTab.getAssetList().getAsset("Cameras", PersonalPropertyMultiAssetList.class).removeAll();
        NavigationPage.toViewTab(ENDORSEMENT.get());
        endorsementTab.tblIncludedEndorsements.getRow("Form ID", HS_04_61.getLabel()).getCell(6).
                controls.links.get(1).click();
        Page.dialogConfirmation.confirm();

		NavigationPage.toViewTab(PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTab.calculatePremium();

        Dollar preEndorsement = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getColumn(2).getValue().get(0));
        Dollar actualPremium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getColumn(5).getValue().get(0));

        assertThat(actualPremium.lessThan(preEndorsement)).as(actualPremium + "should be less than " + preEndorsement).isTrue();

    }
}
