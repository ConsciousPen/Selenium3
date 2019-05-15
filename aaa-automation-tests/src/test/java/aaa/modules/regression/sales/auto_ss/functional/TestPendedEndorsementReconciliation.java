package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.CheckBox;

import java.time.LocalDateTime;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * This test class is intended for testing the removal of Pended Endorsements before a variety of discount removals. <br>
 *     Discount removal occurs at NB+15 (STG1) and NB+30 (STG2).
 */
public class TestPendedEndorsementReconciliation extends AutoSSBaseTest {

    // Static data used throughout test class.
    private enum eTimepoints {STG1, STG2, STG3, STG4}
    private enum eThresholdTest {BEFORE, ON, AFTER}
    static private int CATCHUP_TIMEFRAME_VALUE = 4;

    static private String _storedPolicyNumber = null;
    static private LocalDateTime _policyEffectiveDate = null;
    private GeneralTab _generalTab = new GeneralTab();

    @DataProvider(name = "reconcilePendedEndorsements_MembershipTestData")
    public static Object[][] reconcilePendedEndorsements_MembershipTestData() {
        return new Object[][]{
                {"AZ", eThresholdTest.BEFORE, 15, CATCHUP_TIMEFRAME_VALUE - 1, false, true},
                {"AZ", eThresholdTest.ON, 15, CATCHUP_TIMEFRAME_VALUE, false, true},
                {"AZ", eThresholdTest.AFTER, 15, CATCHUP_TIMEFRAME_VALUE + 1, false, false}};
    }

    @DataProvider(name = "reconcilePendedEndorsements_EValueTestData")
    public static Object[][] reconcilePendedEndorsements_EValueTestData() {
        return new Object[][]{
                {"AZ", eThresholdTest.BEFORE, 15, CATCHUP_TIMEFRAME_VALUE - 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true},
                {"AZ", eThresholdTest.ON, 15, CATCHUP_TIMEFRAME_VALUE, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true},
                {"AZ", eThresholdTest.AFTER, 15, CATCHUP_TIMEFRAME_VALUE + 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false}};
    }

    @DataProvider(name = "reconcilePendedEndorsements_MPDTestData")
    public static Object[][] reconcilePendedEndorsements_MPDTestData() {
        return new Object[][]{
                {"UT", eThresholdTest.BEFORE, 15, CATCHUP_TIMEFRAME_VALUE - 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true},
                {"UT", eThresholdTest.ON, 15, CATCHUP_TIMEFRAME_VALUE, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true},
                {"UT", eThresholdTest.AFTER, 15, CATCHUP_TIMEFRAME_VALUE + 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false}};
    }

    // TEST CASES:

    @Parameters({"state"})
    @Test(dataProvider = "reconcilePendedEndorsements_MembershipTestData", groups = { Groups.CIO, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-21672")
    public void pas21672_reconcilePendedEndorsements_Membership(@Optional String state, eThresholdTest typeOfBoundryTest, eTimepoints stg_x, Integer daysAfterNB, Boolean bExpectingPolicyToBeProcessed) {
        TestData testLevelTD = prepareTestData();
        prepareForPolicyCreation(testLevelTD);
        handlePolicyCreation(testLevelTD, true, false, false);
        addPendedEndorsement();
        validatePendedEndorsementPresent();
        advanceJVMToTimepoint(stg_x, daysAfterNB);
        runBatchJobs(stg_x);
        queryDBForNumberOfPendingEndorsements(bExpectingPolicyToBeProcessed);
    }

    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-21672")
    public void pas21672_reconcilePendedEndorsements_Evalue(@Optional("AZ") String state) {

    }

    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-21672")
    public void pas21672_reconcilePendedEndorsements_MPD(@Optional("UT") String state) {

    }

    // TEST METHODS:

    private TestData prepareTestData(){
        //Create TestData that has AAA Membership == NO initially.
        TestData createdTD = getPolicyDefaultTD();
        createdTD = TestDataHelper.adjustTD(createdTD, GeneralTab.class, AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), "No");
        createdTD = TestDataHelper.adjustTD(createdTD, GeneralTab.class, AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "");

        return createdTD;
    }

    /**
     * Initiate the test. Open the app, create a customer and initiate a quote.
     */
    private void prepareForPolicyCreation(TestData in_td){
        createQuoteAndFillUpTo(in_td, GeneralTab.class, true);
    }

    /**
     * This method should begin when the web-driver is viewing the GeneralTab.
     * @param bHasMembership Should the test add a AAA Membership?
     * @param bHasEValue Should the test add eValue?
     * @param bHasMPD Should the test add MPD?
     */
    private void handlePolicyCreation(TestData in_td, Boolean bHasMembership, Boolean bHasEValue, Boolean bHasMPD){
        if(bHasMembership){
            addMembershipToGeneralTab();
        }

        if(bHasMPD){
            addCompanionPolicyToGeneralTab();
        }

        // Continue to next page (Driver's Tab). Then complete policy bind.
        Tab.buttonNext.click();
        policy.getDefaultView().fillFromTo(in_td, DriverTab.class, PurchaseTab.class);
        _storedPolicyNumber = PolicySummaryPage.getPolicyNumber();
        _policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
    }

    private void addPendedEndorsement(){
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
        _generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED).setValue("Doug");
        _generalTab.saveAndExit();
    }

    private void validatePendedEndorsementPresent(){
        // If this is not true, there's no point in continuing the test.
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled();
    }

    private void advanceJVMToTimepoint(eTimepoints STG_N, Integer in_daysAfterNB){
        switch(STG_N){
            case STG1:
                TimeSetterUtil.getInstance().nextPhase(_policyEffectiveDate.plusDays(15 + in_daysAfterNB));
                break;
            case STG2:
                TimeSetterUtil.getInstance().nextPhase(_policyEffectiveDate.plusDays(30 + in_daysAfterNB));
                break;
            default:
                CustomAssertions.fail("STG_N has an unexpected/unhandled value: " + STG_N.toString());
                break;
        }
    }

    private void runBatchJobs(eTimepoints STG_N){
        switch(STG_N){
            case STG1:
                JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
                JobUtils.executeJob(Jobs.membershipValidationJob);
                break;
            case STG2:
                JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
                JobUtils.executeJob(Jobs.membershipValidationJob);
                break;
            default:
                CustomAssertions.fail("STG_N has an unexpected/unhandled value: " + STG_N.toString());
                break;
        }
    }

    private void queryDBForNumberOfPendingEndorsements(Boolean bShouldEndorsementsBeDeleted){
        Integer numberOfRowsReturnedFromQuery = AAAMembershipQueries.getNumberOfPendedEndorsements(_storedPolicyNumber);

        if(bShouldEndorsementsBeDeleted){
            CustomAssertions.assertThat(numberOfRowsReturnedFromQuery).isEqualTo(0);
        }
        else{
            CustomAssertions.assertThat(numberOfRowsReturnedFromQuery).isEqualTo(1);
        }
    }

    private void addMembershipToGeneralTab(){
        _generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
        _generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("9999992222222228");
    }

    private void addCompanionPolicyToGeneralTab(){
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Customer Details");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ZIP_CODE.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ZIP_CODE.getControlClass()).setValue("85395");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.FIRST_NAME.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.FIRST_NAME.getControlClass()).setValue("Mike");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.LAST_NAME.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.LAST_NAME.getControlClass()).setValue("Tyson");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.DATE_OF_BIRTH.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.DATE_OF_BIRTH.getControlClass()).setValue("01/01/2000");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADDRESS_LINE_1.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADDRESS_LINE_1.getControlClass()).setValue("123 Fake St.");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.CITY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.CITY.getControlClass()).setValue("Goodyear");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.STATE.getLabel(), (AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.STATE.getControlClass())).setValue("AZ");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();

        new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + String.valueOf(0) + ":customerSelected")).setValue(true);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_SELECTED_BTN.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_SELECTED_BTN.getControlClass()).click();
    }
}
