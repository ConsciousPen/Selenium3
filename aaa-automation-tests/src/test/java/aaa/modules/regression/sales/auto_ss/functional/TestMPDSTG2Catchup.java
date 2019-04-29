package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMultiPolicyDiscountQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.assertj.core.api.Assertions.assertThat;

public class TestMPDSTG2Catchup extends AutoSSBaseTest {

    String populateData_NotFound = "NOT_FOUND@yeah.com";
    String populatefound = "REFRESH_P@yeah.com";
    String populateerror = "MPD_ERROR@yeah.com";
    String cancelledMS = "3111111111111121";
    String activeMS = "4290074030137505";

    /**
     * @author Robert Boles
     * @name PAS-28452 MPD/Membership Validation to occur simultaneously (AC#1)
     * @scenario Precondition: Wiremock for MPD required
     * 1. Create Customer.
     * 2. Create AUTO_SS Policy with Membership Cancelled and Quotes/Membership Cancelled no quotes/Membership Active and Quotes and lastly Membership Active and No quotes.
     * 3. Run NB +30 Validation Batch Jobs
     * 4. Verify DB has one endorsement created
     * 5. Verify only one decpage has been generated and it contains AHDRXX string for discount removal
     * @details
     */
    //Membership: Cancelled
    //MPD: QUOTED at bind
    //BMS: NOTFOUND_STG2 --Endorse image (cancelledMS)
    //MPDVS: FOUND_STG2 --Endorse image (populatefound)
    //Not picked up due to outside of time point window
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: MPD/Membership Validation to occur simultaneously")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-1")
    public void pas28452_CatchUp_Boundary_MinusOne(@Optional("UT") String state) {
        String policyNumber = mpdSTG2(populatefound,cancelledMS,-1, "Yes", true);
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusDB(policyNumber).orElse("Null"));
        assertThat(responseMPD).isEqualTo("Null");
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVEndoAndDocgenFromDB(policyNumber, "ENDORSEMENT_ISSUE","AHDRXX", "AAA Multi-Policy Discount" ).orElse("Null")).isEqualTo("0");
    }

    //Membership: Cancelled
    //MPD: QUOTED at bind
    //BMS: NOTFOUND_STG2 --Endorse image (cancelledMS)
    //MPDVS: FOUND_STG2 --Endorse image (populatefound)
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: MPD/Membership Validation to occur simultaneously")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-2")
    public void pas28452_MS_Cancelled_MPD_FOUND_STG2(@Optional("UT") String state) {
        String policyNumber = mpdSTG2(populatefound,cancelledMS,0, "Yes", true);
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusDB(policyNumber).orElse("Null"));
        assertThat(responseMPD).isEqualTo("FOUND_STG2");
         assertThat(AAAMultiPolicyDiscountQueries.getMPDVEndoAndDocgenFromDB(policyNumber, "ENDORSEMENT_ISSUE","AHDRXX", "AAA Membership Discount" ).orElse("Null")).isEqualTo("1");
    }

    //Membership:Cancelled
    //MPD: QUOTED at bind
    //BMS:NOTFOUND_STG2 Endorse image (cancelledMS)
    //MPDVS:NOTFOUND_STG2 Endorse image (populateDataForBatch_NotFound)
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: MPD/Membership Validation to occur simultaneously")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-3")
    public void pas28452_MS_Cancelled_MPD_NOTFOUND_STG2(@Optional("UT") String state) {
        String policyNumber = mpdSTG2(populateData_NotFound,cancelledMS,0, "Yes", true);
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusDB(policyNumber).orElse("Null"));
        assertThat(responseMPD).isEqualTo("NOTFOUND_STG2");
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVEndoAndDocgenFromDB(policyNumber, "ENDORSEMENT_ISSUE","AHDRXX", "AAA Multi-Policy Discount" ).orElse("Null")).isEqualTo("1");
    }

    //Membership: Active
    //MPD: QUOTED at bind
    //BMS: NOTFOUND_STG2 --Endorse image (cancelledMS)
    //MPDVS: FOUND_STG2 --Endorse image (populateData)
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: MPD/Membership Validation to occur simultaneously")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-4")
    public void pas28452_MS_Active_MPD_FOUND_STG2(@Optional("KY") String state) {
        String policyNumber = mpdSTG2(populatefound,activeMS,2, "Yes", true);
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusDB(policyNumber).orElse("Null"));
        assertThat(responseMPD).isEqualTo("FOUND_STG2");
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVEndoAndDocgenFromDB(policyNumber, "ENDORSEMENT_ISSUE","AHDRXX", "AAA Multi-Policy Discount" ).orElse("Null")).isEqualTo("0");
    }

    //Membership: Active
    //MPD: QUOTED at bind
    //BMS: FOUND_STG2 Endorse image (cancelledMS)
    //MPDVS: FOUND_STG2 Endorse image (populateDataForBatch_NotFound)
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: MPD/Membership Validation to occur simultaneously")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-5")
    public void pas28452_MS_Active_MPD_NOTFOUND_STG2(@Optional("UT") String state) {
        String policyNumber = mpdSTG2(populateData_NotFound,activeMS,0, "Yes", true);
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusDB(policyNumber).orElse("Null"));
        assertThat(responseMPD).isEqualTo("NOTFOUND_STG2");
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVEndoAndDocgenFromDB(policyNumber, "ENDORSEMENT_ISSUE","AHDRXX", "AAA Multi-Policy Discount" ).orElse("Null")).isEqualTo("1");
    }

    //Membership:Cancelled
    //MPD: QUOTED
    //BMS:NOTFOUND_STG2 Endorse image
    //MPDVS:ERROR_STG2 Endorse image
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: MPD/Membership Validation to occur simultaneously")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-6")
    //TODO not working due to some optional error issue
    public void pas28452_MS_Cancelled_MPD_Error(@Optional("UT") String state) {
        String policyNumber = mpdSTG2(populateerror,cancelledMS,0, "Yes", true);
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusDB(policyNumber).orElse("Null"));
        assertThat(responseMPD).isEqualTo("ERROR_STG2");
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVEndoAndDocgenFromDB(policyNumber, "ENDORSEMENT_ISSUE","AHDRXX", "AAA Multi-Policy Discount" ).orElse("Null")).isEqualTo("0");
    }

    //Membership:Active
    //MPD: QUOTED
    //BMS:Not eligible for Validation
    //MPDVS:ERROR_STG2 Endorse Original image
    //TODO ERROR with MS = active
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: MPD/Membership Validation to occur simultaneously")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-7")
    public void pas28452_MS_Active_MPD_Error(@Optional("UT") String state) {
        String policyNumber = mpdSTG2(populateerror, activeMS, 0, "Yes", true);
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusDB(policyNumber).orElse("Null"));
        assertThat(responseMPD).isEqualTo("ERROR_STG2");
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVEndoAndDocgenFromDB(policyNumber, "ENDORSEMENT_ISSUE","AHDRXX", "AAA Multi-Policy Discount" ).orElse("Null")).isEqualTo("0");
    }

    //Membership:Active
    //MPD: QUOTED
    //BMS:Not eligible for Validation
    //MPDVS:ERROR_STG2 Endorse Original image
    //TODO ERROR with MS = active
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: MPD/Membership Validation to occur simultaneously")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-8")
    public void pas28452_MS_Active_MPD_None(@Optional("UT") String state) {
        String policyNumber = mpdSTG2("REFRESH_Qx@gmail.com", activeMS, 0, "Yes", false);
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusDB(policyNumber).orElse("Null"));
        assertThat(responseMPD).isEqualTo("Null");
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVEndoAndDocgenFromDB(policyNumber, "ENDORSEMENT_ISSUE","AHDRXX", "AAA Multi-Policy Discount" ).orElse("Null")).isEqualTo("0");
    }

    //Membership:Active
    //MPD: QUOTED
    //BMS:Not eligible for Validation
    //MPDVS:ERROR_STG2 Endorse Original image
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: MPD/Membership Validation to occur simultaneously")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-9")
    public void pas28452_MS_Cancelled_MPD_None(@Optional("UT") String state) {
        String policyNumber = mpdSTG2("REFRESH_Q@gmail.com", cancelledMS, 0, "Yes", false);
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusDB(policyNumber).orElse("Null"));
        assertThat(responseMPD).isEqualTo("Null");
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVEndoAndDocgenFromDB(policyNumber, "ENDORSEMENT_ISSUE","AHDRXX", "AAA Multi-Policy Discount" ).orElse("Null")).isEqualTo("0");
    }
    /**
     * This is will validate the status of membership and multi-policy discount
     * @param wiremockValue the specific value used to return data from wiremock (FOUND_STG2, NOTFOUND_STG2, ERROR_STG2)
     * @param membershipNumber the specific membership number used to set membership (active gets bypassed by job while cancelled gets picked up as NOTFOUND_STG2)
     * @param catchup the specific number of days you want to use to run past the renewal date up to 4 days
     * @param insurercd used to set the flag for membership Yes or No
     * @param none used to bypass the AAA Other Products section
     */
    private String mpdSTG2(String wiremockValue, String membershipNumber, int catchup, String insurercd, boolean none) {
        GeneralTab generalTab = new GeneralTab();
        ErrorTab errorTab = new ErrorTab();

        TestData testData = getTdAuto();
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, GeneralTab.class,true);
        //Set Membership to Yes or No
        generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue(insurercd);
        //Set the membership number to either cancelled or active
        generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue(membershipNumber);
        if (none) {
            //puts quoted products into the MPD table with REFRESH_Q@yeah.com
            generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue("REFRESH_Q@yeah.com");

            generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                    .click(Waiters.AJAX);
            //Sets the value to wiremock value for STG2 status check
            generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue(wiremockValue);

        }
        generalTab.submitTab();
        policy.getDefaultView().fillFromTo(getTdAuto(), DriverTab.class, PurchaseTab.class, true);
        if (errorTab.tableErrors.isPresent()) {
            errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_MVR_order_validation_SS);
            errorTab.override();
            PurchaseTab purchaseTab = new PurchaseTab();
            purchaseTab.submitTab();
        } else {
            PurchaseTab purchaseTab = new PurchaseTab();
            purchaseTab.submitTab();
        }
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Policy Number " + PolicySummaryPage.getPolicyNumber());
        //This job group needs to execute on a weekday
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        if(policyEffectiveDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            policyEffectiveDate = policyEffectiveDate.plusDays(2);
        } else if (policyEffectiveDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            policyEffectiveDate = policyEffectiveDate.plusDays(1);
        }

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(30).plusDays(catchup));
        log.info("Time Setter Move " + policyEffectiveDate.plusDays(30).plusDays(catchup));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
        JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
        JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
        JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
        JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
        JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
        setTimeToToday();
        return policyNumber;
    }
    /**
     * @return Test Data for an AZ SS policy with no other active policies
     */
    private TestData getTdAuto() {
        return getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()))
                .mask(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel()));
    }

    /**
     * @info sets the time back to system time for reduction in discount
     */
    public void setTimeToToday() {
        log.info("Current application date: " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        TimeSetterUtil.getInstance().adjustTime();
    }
}