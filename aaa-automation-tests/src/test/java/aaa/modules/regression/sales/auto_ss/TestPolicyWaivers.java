package aaa.modules.regression.sales.auto_ss;

import aaa.main.metadata.policy.AutoSSMetaData;
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
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestPolicyWaivers extends AutoSSBaseTest {
    protected TestData tdPolicy;
    private String origQuoteNum;
    private String policyNum;
    private DriverTab driverTab = new DriverTab();
    private ActivityInformationMultiAssetList aiAssetList = driverTab.getActivityInformationAssetList();
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
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testPolicyWaiversAutoSS(@Optional("") String state) {
        mainApp().open();
        createCustomerIndividual();
        origQuoteNum = createQuote();
        policyNum = createPolicyAndVerifyWaiverStatus(origQuoteNum);
        verifyWaiverStatusOnEndorsement(policyNum);
    }

    /**
     * Open Data Gathering mode, Navigate to Driver tab, add 2 minor violations, Calculate Premium, Navigate to Bind tab and purchase quote
     * @param origQuoteNum - string of the quote number for lookup
     **/
    private String createPolicyAndVerifyWaiverStatus(String origQuoteNum) {
        SearchPage.openQuote(origQuoteNum);
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        //Adds first minor violation same date of occurrence
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Minor Violation");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Disregard Traffic Device or Sign");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("01/10/2019");
        //Adds second minor violation same date of occurrence
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Minor Violation");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Failure to Yield Right-of-Way");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("01/10/2019");

        new DriverTab().submitTab();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
        new PurchaseTab().fillTab(getPolicyTD()).submitTab();
        return PolicySummaryPage.labelPolicyNumber.getValue();
    }

    /**
     * Open Data Gathering mode, Navigate to Driver tab, add 2 minor violations, Calculate Premium, Navigate to Bind tab and purchase quote
     * @param createPolicyAndVerifyWaiverStatus - string of the policy number for endorsement action
     **/
    private void verifyWaiverStatusOnEndorsement(String createPolicyAndVerifyWaiverStatus) {
        SearchPage.openPolicy(createPolicyAndVerifyWaiverStatus);
        TestData initiateEndorsement = getPolicyTD("Endorsement", "TestData");
        policy.endorse().perform(initiateEndorsement);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        //Asserts that the waiver is present prior to calculating premium in the endorsement
        assertThat(AutoSSMetaData.DriverTab.ActivityInformation.NOT_INCLUDED_IN_POINTS_AND_OR_TIER_REASON_CODES.getLabel().equalsIgnoreCase("Waived - Same Day"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().calculatePremium();

        //Navigate back to Driver tab to assert the waiver did not fall off (validation of fix)
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        assertThat(AutoSSMetaData.DriverTab.ActivityInformation.NOT_INCLUDED_IN_POINTS_AND_OR_TIER_REASON_CODES.getLabel().equalsIgnoreCase("Waived - Same Day"));
        System.out.println("Policy Nunber: " + DriverTab.labelPolicyNumber.getValue());
    }
}

