package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.util.Files.contentOf;
import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.auto_ss.TestPolicyCreationBig;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestAccidentSurchargeWaiver extends TestOfflineClaimsTemplate {

    private File claimResponseFile;

    private static final String TEST_DRIVER = "TestDriver";

    // Activity Types
    private static final String NAF_ACCIDENT = "Non-Fault Accident";
    private static final String AF_ACCIDENT = "At-Fault Accident";
    private static final String MAJOR_VIOLATION = "Major Violation";

    // Activity Descriptions
    private static final String PROPERTY_DAMAGE = "Accident (Property Damage Only)";
    private static final String BODILY_INJURY = "Accident (Resulting in Bodily Injury)";
    private static final String NOT_AT_FAULT = "Accident (Not-At-Fault)";
    private static final String DRAG_RACING = "Drag Racing or Speed Contest";
    private static final String HIT_AND_RUN = "Hit and Run";

    /**
     * @author Josh Carpenter
     * @name Test that a new at-fault accident is waived if the ASW conditions are met during conversion
     * @scenario
     * 1. Initiate Auto SS conversion policy with base date > 4 years ago
     * 2. Fill policy up to P & C tab with AF accident in the past 33 months
     * 3. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 4. Add a second AF accident in the past 33 months
     * 5. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 6. Remove the second claim
     * 7. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 8. Change the prior carrier to non-AAA (Progressive)
     * 9. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiverConversion(@Optional("") String state) {

        TestData td = adjustTdBaseDate(getConversionPolicyDefaultTD());
        createConversionQuoteAndFillUpTo(td, DocumentsAndBindTab.class);
        validateAFW(td);
        assertThat(PolicySummaryPage.tableRenewals).isPresent();

    }

    /**
     * @author Josh Carpenter
     * @name Test that a new at-fault accident is waived if the ASW conditions are met during NB
     * @scenario
     * 1. Initiate Auto SS policy with base date = 2 years ago and time with previous carrier = 2 years
     * 2. Fill policy up to P & C tab with AF accident in the past 33 months
     * 3. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 4. Add a second AF accident in the past 33 months
     * 5. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 6. Remove the second claim
     * 7. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 8. Change the prior carrier to non-AAA (Progressive)
     * 9. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiverNB(@Optional("") String state) {

        TestData td = adjustTdBaseDate(getPolicyTD());
        createQuoteAndFillUpTo(td, DocumentsAndBindTab.class);
        validateAFW(td);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    }

    /**
     * @author Josh Carpenter
     * @name Test that a new at-fault accident is waived if the ASW conditions are met during endorsement
     * @scenario
     * 1. Initiate Auto SS policy with base date = 2 years ago and time with previous carrier = 2 years and AF accident in the past 33 months
     * 2. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 3. Bind policy
     * 4. Initiate endorsement with trans. eff. date 5 days from policy eff. date
     * 5. Add another AF claim in the past 33 months
     * 6. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiverEndorsement(@Optional("") String state) {

        TestData td = adjustTdBaseDate(getPolicyTD());
        createQuoteAndFillUpTo(td, RatingDetailReportsTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        fillActivityDriverTab(getActivityInfoTd());
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        policy.getDefaultView().fillFromTo(td, VehicleTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        fillActivityDriverTab(getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY)
                .adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-8M>")
                .adjust(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY.getLabel(), "click"));
        calculatePremiumAndNavigateToDriverTab();

        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);
        validateIncludeInPoints(BODILY_INJURY, "Yes");

    }

    /**
     * @author Josh Carpenter, Sreekanth Kopparapu
     * @name Test that a new at-fault accident is waived if the ASW conditions are met during renewal
     * @scenario
     * 1. Create Auto SS policy with base date = 2 years ago and time with previous carrier = 2 years
     * 2. Create renewal image and add AF accident in the past 33 months
     * 3. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 4. Add a second AF accident in the past 33 months
     * 5. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 6. Remove the second claim
     * 7. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 8. Change the prior carrier to non-AAA (Progressive)
     * 9. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiverRenewal(@Optional("") String state) {

        TestData td = adjustTdBaseDate(getPolicyTD());
        openAppAndCreatePolicy(td);
        policy.renew().perform();
        validateAFW(td);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    }

    /**
     * @author Josh Carpenter
     * @name Test that ASW is given properly during an aged renewal (ASW not eligible in NB policy)
     * @scenario
     * 1.  Create Auto SS policy with the following attributes:
     *       a. Base date = 1 year ago
     *       b. Time with previous carrier = 2 years
     *       c. Previous carrier = 1 year, AAA provider
     * 2.  Advance time to renewal reports order date (R-63)
     * 3.  Run renewalOfferGenerationPart1 and renewalClaimOrderAsyncJob
     * 4.  Create CAS response file and upload to VDM with micro service automation
     * 5.  Run renewalOfferGenerationPart2 and renewalClaimReceiveAsyncJob
     * 6.  Open policy and validate CAS claim is on the driver tab and 'Included in Rating' = 'Yes'
     * 7.  Bind policy
     * 8.  Advance time to renewal effective date and run renewal jobs again
     * 9.  Pay amount due for the renewal
     * 10. Advance time to next renewal reports order date (2R-63)
     * 11. Run renewalOfferGenerationPart1
     * 12. Update claim loss amount, create CAS response, and upload to VDM
     * 13. Run renewalOfferGenerationPart2 and renewalClaimReceiveAsyncJob
     * 14. Open policy, navigate to Driver tab, validate.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-23995")
    public void pas23995_testAccidentSurchargeWaiverAgedRenewalCAS(@Optional("") String state) {

        TestData tdCustomer = getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), "ASW")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), "CASTest");

        TestData tdPolicy = adjustTdBaseDate(getPolicyTD())
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel()), "$<today>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "D99155622");

        // Create policy
        mainApp().open();
        createCustomerIndividual(tdCustomer);
        createPolicy(tdPolicy);
        policyNumber = PolicySummaryPage.getPolicyNumber();
        policyExpirationDate = PolicySummaryPage.getExpirationDate();

        // Advance time to renewal reports order date and run jobs
        runRenewalAndOrderJobs();

        // Generate CAS response file, upload to VDM, and run renewal receive jobs
        downloadClaimRequest();
        createCasResponseAndUpload();
        runRenewalClaimReceiveJob();

        // Open policy and validate the AF accident is included in rating
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        calculatePremiumAndNavigateToDriverTab();
        assertThat(DriverTab.tableActivityInformationList.getRow(1).getCell(PolicyConstants.ActivityInformationTable.INCLUDE_IN_POINTS_TIER).getValue()).isEqualTo("Yes");
        assertThat(driverTab.getActivityInformationAssetList().getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).getValue()).contains("5000");
        premiumAndCoveragesTab.calculatePremium();

        // Bind policy and make active
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);

        // Open policy and create second renewal image
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        assertThat(PolicySummaryPage.getEffectiveDate()).isEqualToIgnoringHours(policyExpirationDate);
        policyExpirationDate = PolicySummaryPage.getExpirationDate();
        runRenewalAndOrderJobs();

        // Upload file into inbound folder and run receive jobs
        createCasResponseUpdateLossAmtAndUpload();
        runRenewalClaimReceiveJob();

        // Open policy and validate claim is still included in rating and claim amount has been updated
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        calculatePremiumAndNavigateToDriverTab();
        assertThat(DriverTab.tableActivityInformationList.getRow(1).getCell(PolicyConstants.ActivityInformationTable.INCLUDE_IN_POINTS_TIER).getValue()).isEqualTo("Yes");
        assertThat(driverTab.getActivityInformationAssetList().getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).getValue()).contains("4000");

    }

    /**
     * @author Josh Carpenter
     * @name Test ASW with 1 AF and 1 NAF accident
     * @scenario
     * 1.  Initiate SS quote with one driver and 4 years with a prior AAA carrier on General tab
     * 2.  Add 1 AF accident and 1 NAF accident
     * 3.  Navigate to P & C tab, calculate premium
     * 4.  Navigate back to Driver tab
     * 5.  Validate AF accident receives ASW
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27346")
    public void pas24673_oneAFAndOneNAFAccidentNB(@Optional("") String state) {

        // Create test data for one AF and one NAF accident
        List<TestData> tdActivity = new ArrayList<>();
        tdActivity.add(getActivityInfoTd(AF_ACCIDENT, PROPERTY_DAMAGE));
        tdActivity.add(getActivityInfoTd(NAF_ACCIDENT, NOT_AT_FAULT)
                .mask(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel())
                .adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-23M>"));

        // Initiate quote, fill up to P & C tab, and navigate to driver tab
        TestData td = getDefaultASWTd().adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel()), tdActivity);
        createQuoteAndFillUpTo(td, PremiumAndCoveragesTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        // Validate AF accident receives ASW
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);

    }

    /**
     * @author Josh Carpenter
     * @name Test ASW is not given when 2 drivers both have AF accident at NB
     * @scenario
     * 1.  Initiate SS quote with two drivers and 4 years with a prior AAA carrier on General tab
     * 2.  Add 1 AF accident to each driver
     * 3.  Navigate to P & C tab, calculate premium
     * 4.  Navigate back to Driver tab
     * 5.  Validate ASW is not given to either driver
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27346")
    public void pas24673_testTwoDriversEachWithAFAccidentNB(@Optional("") String state) {

        // Create driver tab test data for 2 drivers with one AF accident each
        List<TestData> tdDriverTab = new ArrayList<>();
        tdDriverTab.add(getPolicyTD().getTestData(AutoSSMetaData.DriverTab.class.getSimpleName())
                .adjust(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), getActivityInfoTd(AF_ACCIDENT, PROPERTY_DAMAGE)));
        tdDriverTab.add(getSecondDriverTd().adjust(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY)
                .adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-6M>")));

        // Initiate quote, fill up to P & C tab, and navigate to driver tab
        createQuoteAndFillUpTo(getDefaultASWTd().adjust(DriverTab.class.getSimpleName(), tdDriverTab), PremiumAndCoveragesTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        // Validate accident for each driver is included in rating
        DriverTab.viewDriver(1);
        validateIncludeInPoints(PROPERTY_DAMAGE, "Yes");
        DriverTab.viewDriver(2);
        validateIncludeInPoints(BODILY_INJURY, "Yes");

    }

    /**
     * @author Josh Carpenter
     * @name Test ASW is retained during Endorsement
     * @scenario
     * 1.  Initiate SS quote with one driver and 4 years with a prior AAA carrier on General tab
     * 2.  Add 1 AF accident that receives ASW to driver and bind policy
     * 3.  Initiate endorsement, add 2nd driver with AF accident
     * 4.  Navigate to P & C tab, calculate premium
     * 5.  Navigate back to Driver tab
     * 6.  Validate ASW is retained for first driver, second driver accident is chargeable
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27346")
    public void pas24673_testASWRemainsDuringEndorsement(@Optional("") String state) {

        // Create test data for endorsement to add a second driver with AF accident during endorsement
        TestData tdEndorsementFill = DataProviderFactory.dataOf(
                GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData(),
                DriverTab.class.getSimpleName(), getSecondDriverTd().adjust(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(),
                        getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY).adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-6M>")));

        // Initiate quote, fill up to DAR tab, add AF accident for driver
        createQuoteAndFillUpTo(getDefaultASWTd(), DriverActivityReportsTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        fillActivityDriverTab(getActivityInfoTd());

        // Validate AF accident receives ASW
        calculatePremiumAndNavigateToDriverTab();
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);

        // Bind policy
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        policy.getDefaultView().fillFromTo(getPolicyTD(), DocumentsAndBindTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        // Initiate endorsement and add second driver with AF accident in past 33 months
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        policy.getDefaultView().fill(tdEndorsementFill);
        calculatePremiumAndNavigateToDriverTab();

        // Validate ASW is retained for first driver, accident for second driver is chargeable
        DriverTab.viewDriver(1);
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);
        DriverTab.viewDriver(2);
        validateIncludeInPoints(BODILY_INJURY, "Yes");
    }

    /**
     * @author Josh Carpenter
     * @name Test ASW is not given when 2 drivers both have AF accident at renewal
     * @scenario
     * 1.  Initiate SS quote with two drivers and 4 years with a prior AAA carrier on General tab
     * 2.  Add 1 AF accident to each driver
     * 3.  Navigate to P & C tab, calculate premium
     * 4.  Navigate back to Driver tab
     * 5.  Validate ASW is not given to either driver
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27346")
    public void pas24673_testNoASWForBothDriversDuringRenewal(@Optional("") String state) {

        // Create driver tab test data for 2 drivers with clean history
        List<TestData> tdDriverTab = new ArrayList<>();
        tdDriverTab.add(getPolicyTD().getTestData(AutoSSMetaData.DriverTab.class.getSimpleName()));
        tdDriverTab.add(getSecondDriverTd());

        // Create policy with 2 drivers
        openAppAndCreatePolicy(getDefaultASWTd().adjust(DriverTab.class.getSimpleName(), tdDriverTab));

        // Create renewal image and navigate to driver tab
        policy.renew().perform();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        // Add AF accident for first driver
        DriverTab.viewDriver(1);
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel()), getActivityInfoTd()));

        // Add AF accident for first driver
        DriverTab.viewDriver(2);
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel()),
                        getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY).adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-6M>")));

        // Validate accidents for both drivers are chargeable
        calculatePremiumAndNavigateToDriverTab();
        DriverTab.viewDriver(1);
        validateIncludeInPoints(PROPERTY_DAMAGE, "Yes");
        DriverTab.viewDriver(2);
        validateIncludeInPoints(BODILY_INJURY, "Yes");

    }

    /**
     * @author Josh Carpenter
     * @name Test that ASW is given to a second driver after the first driver's ASW accident is aged out of 33 month window.
     * @scenario
     * 1.  Initiate SS quote with two drivers with base date 2 years ago and 2 years with a prior AAA carrier on General tab
     * 2.  Add 1 AF accident to first driver that is 23 months old, second driver is clean
     * 3.  Navigate to P & C tab, calculate premium
     * 4.  Navigate back to Driver tab
     * 5.  Validate ASW is applied to AF accident of first driver
     * 6.  Bind Policy
     * 7.  Create renewal image
     * 8.  Add new AF accident in the last 12 months for second driver
     * 9.  Navigate to P & C tab, calculate premium
     * 10. Navigate back to Driver tab
     * 11. Validate ASW is now applied to the second driver's activity (both drivers have ASW now)
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27346")
    public void pas24673_testASWForTwoDriversWhenOneAccidentAgesOut(@Optional("") String state) {

        // Create test data for policy with base date 2 years ago and prior carrier time 3 total years
        TestData td =  adjustTdBaseDate(getPolicyTD()).adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel()), "$<today-4y>");

        // Create test data for 2 drivers with clean history
        List<TestData> tdDriverTab = new ArrayList<>();
        tdDriverTab.add(getPolicyTD().getTestData(AutoSSMetaData.DriverTab.class.getSimpleName()));
        tdDriverTab.add(getSecondDriverTd());

        // Initiate quote with 2 drivers and add AF accident to first driver
        createQuoteAndFillUpTo(td.adjust(DriverTab.class.getSimpleName(), tdDriverTab), DriverActivityReportsTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        DriverTab.viewDriver(1);
        fillActivityDriverTab(getActivityInfoTd().adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-23M>"));
        calculatePremiumAndNavigateToDriverTab();

        // Validate ASW is recieved for first driver's accident
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);

        // Bind quote
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        policy.getDefaultView().fillFromTo(getPolicyTD(), DocumentsAndBindTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        // Advance time to renewal reports order date and create renewal image
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(PolicySummaryPage.getExpirationDate()));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().perform();

        // Add AF accident for second driver
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        DriverTab.viewDriver(2);
        fillActivityDriverTab(getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY).adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-6M>"));
        calculatePremiumAndNavigateToDriverTab();

        // Validate both accidents receive ASW
        validateIncludeInPoints(BODILY_INJURY, "No");
        validateReasonCode(BODILY_INJURY, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);
        DriverTab.viewDriver(1);
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);

    }

    /**
     * @author Josh Carpenter
     * @name Test that ASW is given to a second AF accident after the driver's first accident is aged out of 33 month window.
     * @scenario
     * 1.  Initiate SS quote with with base date 2 years ago and 2 years with a prior AAA carrier on General tab
     * 2.  Add 1 AF accident to first driver that is 23 months old, second driver is clean
     * 3.  Navigate to P & C tab, calculate premium
     * 4.  Navigate back to Driver tab
     * 5.  Validate ASW is applied to AF accident of first driver
     * 6.  Bind Policy
     * 7.  Create renewal image
     * 8.  Add new AF accident in the last 12 months for second driver
     * 9.  Navigate to P & C tab, calculate premium
     * 10. Navigate back to Driver tab
     * 11. Validate ASW is now applied to the second driver's activity (both drivers have ASW now)
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27346")
    public void pas24673_testASWForSecondAccidentAfterFirstAgesOut(@Optional("") String state) {

        // Initiate quote and fill with one driver that has one AF accident
        createQuoteAndFillUpTo(adjustTdBaseDate(getPolicyTD()), DriverActivityReportsTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        fillActivityDriverTab(getActivityInfoTd().adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-23M>"));

        // Validate the accident receives ASW
        calculatePremiumAndNavigateToDriverTab();
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);

        // Bind quote
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        policy.getDefaultView().fillFromTo(getPolicyTD(), DocumentsAndBindTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        // Initiate renewal and add second AF accident for driver
        policy.renew().perform();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        fillActivityDriverTab(getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY)
                .adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-6M>")
                .adjust(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY.getLabel(), "click"));
        calculatePremiumAndNavigateToDriverTab();

        // Validate both accidents receive ASW
        validateIncludeInPoints(BODILY_INJURY, "No");
        validateReasonCode(BODILY_INJURY, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);

    }

    /**
     * @author Josh Carpenter
     * @name Test SDW and ASW hierarchy during NB
     * @scenario
     * 1.  Initiate SS quote with one driver and 4 years with a prior AAA carrier on General tab
     * 2.  Add 2 AF accidents and 2 violations on the driver tab
     * 3.  Navigate to P & C tab, calculate premium
     * 4.  Navigate back to Driver tab
     * 5.  Validate both violations and the accident with the least points receives SDW
     * 6.  Validate the last accident with the most points receives ASW
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27346")
    public void pas27346_testMultipleActivitiesOnSameDayNB(@Optional("") String state) {

        // Create test data for 2 AF accidents and 2 violations
        List<TestData> tdActivity = new ArrayList<>();
        tdActivity.add(getActivityInfoTd(AF_ACCIDENT, PROPERTY_DAMAGE));
        tdActivity.add(getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY));
        tdActivity.add(getActivityInfoTd(MAJOR_VIOLATION, HIT_AND_RUN).mask(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel()));
        tdActivity.add(getActivityInfoTd(MAJOR_VIOLATION, DRAG_RACING).mask(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel()));

        // Create quote with activity
        TestData td = getDefaultASWTd().adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel()), tdActivity);
        createQuoteAndFillUpTo(td, PremiumAndCoveragesTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        // Validate both violations and lowest point value accident receive SDW, other accident receives ASW
        validateMultipleActivitiesOnSameDay();

    }

    /**
     * @author Josh Carpenter
     * @name Test SDW and ASW hierarchy during endorsement
     * @scenario
     * 1.  Create SS policy with one driver and 4 years with a prior AAA carrier on General tab
     * 2.  Initiate endorsement and add new driver with 2 AF accidents and 2 violations on the driver tab
     * 3.  Navigate to P & C tab, calculate premium
     * 4.  Navigate back to Driver tab
     * 5.  Validate both violations and the accident with the least points receives SDW
     * 6.  Validate the last accident with the most points receives ASW
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27346")
    public void pas27346_testMultipleActivitiesOnSameDayEndorsement(@Optional("") String state) {

        // Create test data for 2 AF accidents and 2 violations
        List<TestData> tdActivity = new ArrayList<>();
        tdActivity.add(getActivityInfoTd(MAJOR_VIOLATION, DRAG_RACING).mask(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel()));
        tdActivity.add(getActivityInfoTd(MAJOR_VIOLATION, HIT_AND_RUN).mask(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel()));
        tdActivity.add(getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY));
        tdActivity.add(getActivityInfoTd(AF_ACCIDENT, PROPERTY_DAMAGE));

        // Create test data to fill endorsement with activity
        TestData tdEndorsementFill = DataProviderFactory.dataOf(
                GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData(),
                DriverTab.class.getSimpleName(), getSecondDriverTd().adjust(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), tdActivity));

        // Create policy and add endorsement with activity
        openAppAndCreatePolicy(getDefaultASWTd());
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        policy.getDefaultView().fill(tdEndorsementFill);
        calculatePremiumAndNavigateToDriverTab();

        // Validate both violations and lowest point value accident receive SDW, other accident receives ASW
        DriverTab.viewDriver(2);
        validateMultipleActivitiesOnSameDay();

    }

    /**
     * @author Josh Carpenter
     * @name Test SDW and ASW hierarchy during Renewal
     * @scenario
     * 1.  Create SS policy with one driver and 4 years with a prior AAA carrier on General tab
     * 2.  During renewal add second driver with 2 AF accidents and 2 violations all on same day
     * 3.  Navigate to P & C tab, calculate premium
     * 4.  Navigate back to Driver tab
     * 5.  Validate both violations and the accident with the least points receives SDW
     * 6.  Validate the last accident with the most points receives ASW
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27346")
    public void pas27346_testMultipleActivitiesOnSameDayRenewal(@Optional("") String state) {

        // Create test data for 2 AF accidents and 2 violations
        List<TestData> tdActivity = new ArrayList<>();
        tdActivity.add(getActivityInfoTd(MAJOR_VIOLATION, HIT_AND_RUN).mask(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel()));
        tdActivity.add(getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY));
        tdActivity.add(getActivityInfoTd(MAJOR_VIOLATION, DRAG_RACING).mask(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel()));
        tdActivity.add(getActivityInfoTd(AF_ACCIDENT, PROPERTY_DAMAGE));

        // Create test data to fill renewal with activity
        TestData tdRenewalFill = DataProviderFactory.dataOf(
                GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData(),
                DriverTab.class.getSimpleName(), getSecondDriverTd().adjust(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), tdActivity));

        // Create policy, create renewal image and fill with activity
        openAppAndCreatePolicy(getDefaultASWTd());
        policy.renew().performAndFill(tdRenewalFill);
        calculatePremiumAndNavigateToDriverTab();

        // Validate both violations and lowest point value accident receive SDW, other accident receives ASW
        DriverTab.viewDriver(2);
        validateMultipleActivitiesOnSameDay();

    }

    private TestData adjustTdBaseDate(TestData td) {
        return td.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel()), "$<today-2y>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel()), "$<today-3y>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel()), "$<today-1y>");
    }

    private TestData getDefaultASWTd() {
        return getPolicyTD()
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel()), "$<today-5y>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel()), "$<today-1y>");
    }

    private TestData getActivityInfoTd() {
        return getActivityInfoTd(AF_ACCIDENT, PROPERTY_DAMAGE);
    }

    private TestData getActivityInfoTd(String type, String description) {
        return DataProviderFactory.dataOf(
                AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), type,
                AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), description,
                AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-10M>",
                AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), "3000");
    }

    private TestData getSecondDriverTd() {
        return getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1)
                .mask(AutoSSMetaData.DriverTab.NAMED_INSURED.getLabel())
                .adjust(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), "SDW")
                .adjust(AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), TEST_DRIVER)
                .adjust(AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), "05/05/1981")
                .adjust(AutoSSMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click");
    }

    private void fillActivityDriverTab(TestData td) {
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel()), td));
    }

    private void validateIncludeInPoints(String description, String expectedValue) {
        assertThat(DriverTab.tableActivityInformationList.getRowContains(PolicyConstants.ActivityInformationTable.DESCRIPTION, description)
                .getCell(PolicyConstants.ActivityInformationTable.INCLUDE_IN_POINTS_TIER).getValue()).isEqualTo(expectedValue);
    }

    private void validateReasonCode(String description, String expectedValue) {
        assertThat(DriverTab.tableActivityInformationList.getRowContains(PolicyConstants.ActivityInformationTable.DESCRIPTION, description)
                .getCell(PolicyConstants.ActivityInformationTable.NOT_INCLUDED_REASON_CODES).getValue()).isEqualTo(expectedValue);
    }

    private void validateMultipleActivitiesOnSameDay() {
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_SDW);

        validateIncludeInPoints(BODILY_INJURY, "No");
        validateReasonCode(BODILY_INJURY, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);

        validateIncludeInPoints(HIT_AND_RUN, "No");
        validateReasonCode(HIT_AND_RUN, PolicyConstants.ActivityInformationTable.REASON_CODE_SDW);

        validateIncludeInPoints(DRAG_RACING, "No");
        validateReasonCode(DRAG_RACING, PolicyConstants.ActivityInformationTable.REASON_CODE_SDW);
    }

    private void validateAFW(TestData policyTd) {

        // Add AF accident
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        fillActivityDriverTab(getActivityInfoTd());

        // Validate AFW is given
        calculatePremiumAndNavigateToDriverTab();
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);
        DriverTab.tableActivityInformationList.resetAllFilters();

        // Add a second AF accident in past 33 months and validate
        fillActivityDriverTab(getActivityInfoTd(AF_ACCIDENT, BODILY_INJURY)
                .adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-8M>")
                .adjust(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY.getLabel(), "click"));
        calculatePremiumAndNavigateToDriverTab();
        assertThat(DriverTab.tableActivityInformationList.getRow(1).getCell(PolicyConstants.ActivityInformationTable.INCLUDE_IN_POINTS_TIER).getValue()).isEqualTo("Yes");
        assertThat(DriverTab.tableActivityInformationList.getRow(2).getCell(PolicyConstants.ActivityInformationTable.INCLUDE_IN_POINTS_TIER).getValue()).isEqualTo("Yes");

        // Remove second claim and validate AFW is given
        DriverTab.tableActivityInformationList.removeRow(2);
        calculatePremiumAndNavigateToDriverTab();
        validateIncludeInPoints(PROPERTY_DAMAGE, "No");
        validateReasonCode(PROPERTY_DAMAGE, PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);

        // Change Prior Carrier to non-AAA (Progressive) and validate no AFW for both
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        new GeneralTab().getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER).setValue("Progressive");
        calculatePremiumAndNavigateToDriverTab();
        validateIncludeInPoints(PROPERTY_DAMAGE, "Yes");
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

        if (PurchaseTab.remainingBalanceDueToday.isPresent()) {
            purchaseTab.fillTab(policyTd).submitTab();
        }

    }

    private void calculatePremiumAndNavigateToDriverTab() {
        premiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
    }

    private void runRenewalAndOrderJobs() {
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(policyExpirationDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalClaimOrderAsyncJob);
    }

    private void createCasResponseAndUpload() {
        String casResponseFileName = getCasResponseFileName();
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper("af_accident_claim_data_model.yaml", casResponseFileName);
        claimResponseFile = batchClaimHelper.processClaimTemplate(response -> setPolicyNumber(policyNumber, response));
        assertThat(claimResponseFile).isFile().isNotNull();
        String content = contentOf(claimResponseFile, Charset.defaultCharset());
        log.info("Generated CAS claim response filename {} content {}", casResponseFileName, content);
        RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(), Jobs.getClaimReceiveJobFolder() + File.separator + claimResponseFile.getName());
    }

    private void createCasResponseUpdateLossAmtAndUpload() {
        String casResponseFileName = getCasResponseFileName();
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper("af_accident_claim_data_model.yaml", casResponseFileName);
        claimResponseFile = batchClaimHelper.processClaimTemplate(response -> {
            setPolicyNumber(policyNumber, response);
            setClaimLossAmount("4000.000000000", response);
        });
        assertThat(claimResponseFile).isFile().isNotNull();
        String content = contentOf(claimResponseFile, Charset.defaultCharset());
        log.info("Updated CAS claim total loss amount in response filename {} content {}", casResponseFileName, content);
        RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(), Jobs.getClaimReceiveJobFolder() + File.separator + claimResponseFile.getName());
    }

    private void setClaimLossAmount(String totalAmountPaid, CASClaimResponse response) {
        response.getClaimLineItemList().forEach(claimLineItem -> {
            claimLineItem.getClaimList().forEach(claim -> claim.setTotalAmountPaid(totalAmountPaid));
        });
    }

}
