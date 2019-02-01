package aaa.modules.regression.sales.auto_ss;

import java.util.HashMap;
import java.util.Map;

import aaa.common.Workspace;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.views.DefaultView;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestPolicyWaivers extends AutoSSBaseTest {
    protected TestData tdPolicy;
    private String origQuoteNum;
    private String policyNum;
    private DriverTab driverTab = new DriverTab();
    private RatingDetailReportsTab reportsTab = new RatingDetailReportsTab();
    private ActivityInformationMultiAssetList aiAssetList = driverTab.getActivityInformationAssetList();
    private Workspace defaultView = new DefaultView();

    private Workspace getDefaultView() {
        return defaultView;
    }

    /**
     * @author Rob Boles
     * @name Test Same Day Waiver in Endorsement
     * @scenario 1. Add minor violation - Disregard Traffic Device or Sign - set the occurrence date to policy effective date
     * 2. Add second minor violation - Failure to Yield Right-of-Way - set the occurrence date to policy effective date
     * 3. Create 2nd driver and repeat steps 1 and 2
     * 4. Calculate premium - validate 2nd same day minor violation is waved for both drivers
     * 5. Bind policy
     * 6. Initiate mid-term endorsement
     * 7. Navigate to Driver tab and note the details of the violations (1st minor should have 3 points and 2nd minor should be waived, both same day)
     * 8. Navigate to PnC tab and calculate premium
     * 9. Navigate back to Driver tab
     * 10. Validate: The points will be retained for the 1st minor and the 2nd minor will remain waived with 0 points and no premium change noted.
     * @details
     */
    @Parameters({"state"})
    @StateList(statesExcept = {Constants.States.CA})
    @Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testPolicyWaiversAutoSS(@Optional("") String state) {
        //TestData td_quote = getPolicyTD();

        mainApp().open();
        createCustomerIndividual();
        origQuoteNum = createQuote();
        policyNum = createPolicyAndVerifyWaiverStatus(origQuoteNum);
        //verifyWaiverStatusOnEndorsement(policyNum);

    }

    private String createPolicyAndVerifyWaiverStatus(String origQuoteNum) {
        SearchPage.openQuote(origQuoteNum);


        //policy.policyCopy().perform(getPolicyTD());

        policy.dataGather().start();


        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        new GeneralTab().fillTab(getPolicyTD());
        new GeneralTab().submitTab();

        new DriverTab().fillTab(getPolicyTD());

        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Minor Violation");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Disregard Traffic Device or Sign");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("01/10/2019");

        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Minor Violation");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Failure to Yield Right-of-Way");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("01/10/2019");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER).setValue("No");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.NOT_INCLUDED_IN_POINTS_AND_OR_TIER_REASON_CODES).setValue("Waived - Same Day");

        new DriverTab().submitTab();

        reportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.ORDER_REPORT).click();
        reportsTab.submitTab();

        //getDefaultView().fillUpTo(getPolicyTD(), BindTab.class, true);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().calculatePremium();

        new DocumentsAndBindTab().btnPurchase.click();
        //policy.getDefaultView().fillFromTo(getPolicyTD(), VehicleTab.class, BindTab.class);
        //policy.purchase(getPolicyTD());
        //TODO: Fix return type to return something of value or add assertions to check driver tab
        return PolicySummaryPage.labelPolicyNumber.getValue();
    }

    private void verifyWaiverStatusOnEndorsement(String createPolicyAndVerifyWaiverStatus) {
        SearchPage.openPolicy(createPolicyAndVerifyWaiverStatus);
        policy.endorse();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        //TODO: On driver tab in endo - scrape table values for 2 minor violations
        //new DriverTab().fillTab(td).submitTab();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().calculatePremium();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        //TODO: on driver tab validate the waiver did not drop

    }
}

