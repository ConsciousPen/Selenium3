package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.enums.PrivilegeEnum;
import aaa.common.enums.RestRequestMethodTypes;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.ClaimCASResponseTags;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.claim.datamodel.claim.Claim;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.logs.PasAdminLogGrabber;
import aaa.helpers.rest.JsonClient;
import aaa.helpers.rest.RestRequestInfo;
import aaa.helpers.rest.dtoClaim.ClaimsAssignmentResponse;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static aaa.common.pages.SearchPage.tableSearchResults;
import static aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab.*;
import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;
import static aaa.main.pages.summary.PolicySummaryPage.labelPolicyNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.contentOf;

/**
 * This template is used to test Batch Claim Logic.
 *
 * @author Andrii Syniagin
 */
public class TestOfflineClaimsCATemplate extends CommonTemplateMethods {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CAS_REQUEST_PATH = System.getProperty("user.dir")
            + PropertyProvider.getProperty("test.downloadfiles.location") + "cas_claim_requests";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String CAS_RESPONSE_PATH = System.getProperty("user.dir")
            + PropertyProvider.getProperty("test.downloadfiles.location") + "cas_claim_responses";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String CAS_RESPONSE_FILE_NAME_TEMPLATE = "%s_PAS_B_PASHUB_EXGPAS_4071_D.xml";
    @SuppressWarnings("SpellCheckingInspection")
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss");
    @SuppressWarnings("SpellCheckingInspection")
    private static final String PAS_ADMIN_LOG_PATH = System.getProperty("user.dir")
            + PropertyProvider.getProperty("test.downloadfiles.location") + "pas_admin_log";
    public static final String SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = 'TRUE' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'PermissiveUse'";
    public static final String SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS = "UPDATE LOOKUPVALUE SET DATEOFLOSS = '%s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'PermissiveUse'";
    private static final String CLAIMS_URL = "https://claims-assignment-master.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1"; //Post-Permissive Use
    public static final String SQL_REMOVE_RENEWALCLAIMRECEIVEASYNCJOB_BATCH_JOB_CONTROL_ENTRY = "DELETE FROM BATCH_JOB_CONTROL_ENTRY WHERE jobname='renewalClaimReceiveAsyncJob'";
    public static final String CLAIMS_MICROSERICE_ENDPOINT = "select * from PROPERTYCONFIGURERENTITY where propertyname = 'aaaClaimsMicroService.microServiceUrl'";

    protected TestData adjusted;
    protected LocalDateTime policyExpirationDate;
    protected LocalDateTime policyEffectiveDate;
    protected String policyNumber;

    protected static DriverTab driverTab = new DriverTab();
    protected static PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    protected static DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    protected static ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();

    private static final String CLAIM_NUMBER_1 = "1002-10-8702";
    private static final String CLAIM_NUMBER_2 = "1002-10-8703";
    private static final String CLAIM_NUMBER_3 = "1002-10-8704";
    private static final String CAS_CLUE_CLAIM = "1002-10-8704";
    private static final String CLUE_CLAIM = "1002-10-8799";
    private static final String COMP_DL_PU_CLAIMS_DATA_MODEL_CHOICE = "comp_dl_pu_claims_data_model_choice.yaml";
    private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE_CHOICE = ImmutableMap.of(CLAIM_NUMBER_1, "D1278111", CLAIM_NUMBER_2, "D1278111");
    private static final String COMP_DL_PU_CLAIMS_DATA_MODEL_SELECT = "comp_dl_pu_claims_data_model_select.yaml";
    private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE_SELECT = ImmutableMap.of(CLAIM_NUMBER_1, "D5435433", CLAIM_NUMBER_2, "D5435433");
    private static final String DL_NAME_RECONCILEFNICLAIMS_DATA_MODEL_CHOICE= "dl_name_reconcileFNIclaims_data_model_choice.yaml";
    protected boolean updatePUFlag = false;
    protected boolean secondDriverFlag = false;


    @BeforeTest
    public void prepare() {
        try {
            FileUtils.forceDeleteOnExit(Paths.get(CAS_REQUEST_PATH).toFile());
            FileUtils.forceDeleteOnExit(Paths.get(CAS_RESPONSE_PATH).toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot delete directories " + CAS_RESPONSE_PATH + " "
                    + CAS_REQUEST_PATH, e);
        }
        try {
            Files.createDirectories(Paths.get(CAS_REQUEST_PATH));
            Files.createDirectories(Paths.get(CAS_RESPONSE_PATH));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create directories " + CAS_RESPONSE_PATH + " "
                    + CAS_REQUEST_PATH, e);
        }
    }

    public String createPolicyMultiDrivers() {
        TestData testData = getPolicyTD();
        List<TestData> testDataDriverData = new ArrayList<>();// Merged driver tab with 4 drivers
        testDataDriverData.add(testData.getTestData("DriverTab"));
        testDataDriverData.addAll(getTestSpecificTD("TestData_DriverTab_OfflineClaim").resolveLinks().getTestDataList("DriverTab"));
        adjusted = testData.adjust("DriverTab", testDataDriverData);

        // Create Customer and Policy with 4 drivers
        openAppAndCreatePolicy(adjusted);
        policyNumber = labelPolicyNumber.getValue();
        mainApp().close();
        return policyNumber;
    }

    // Retrieve policy, generate a manual renewal image, save and exit the app
    public void createManualRenewal() {
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        policy.renew().start();
        GeneralTab.buttonSaveAndExit.click();
        mainApp().close();
    }

    protected void pas14679_CompDLPUMatchMore() {
        // Toggle ON PermissiveUse Logic
        // Set DATEOFLOSS Parameter in DB: Equal to Claim3 dateOfLoss
        // Set RISKSTATECD in DB to get policy DATEOFLOSS working
        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-NOV-18"));

        createPolicyMultiDrivers();    // Create Customer and Policy with 4 drivers
        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
            createCasClaimResponseAndUploadWithUpdatedDL(policyNumber, COMP_DL_PU_CLAIMS_DATA_MODEL_SELECT, CLAIM_TO_DRIVER_LICENSE_SELECT);

        } else if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            createCasClaimResponseAndUploadWithUpdatedDL(policyNumber, COMP_DL_PU_CLAIMS_DATA_MODEL_CHOICE, CLAIM_TO_DRIVER_LICENSE_CHOICE);
        }
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        // Enter renewal image and verify claim presence
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());

        // Check 1st driver: FNI, has the COMP match claim & PU Match Claim. Also Making sure that Claim4: 1002-10-8704-INVALID-dateOfLoss from data model is not displayed
        // Check 2nd driver: Has DL match claim
        compDLPuAssertions(CLAIM_NUMBER_1, CLAIM_NUMBER_2, CLAIM_NUMBER_3);
        mainApp().close();

        //Run the renewal job and pay the bill
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));

        //Accept Payment and renew the policy
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);

        //Scenario to check the user does not have privilege to edit the PU indicator in endorsement
        //Login with different user. Check the PU indicator is not editable for internal claims other than E34/L41
        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.F35);
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(!activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isEnabled());
        });
    }

    /**
     * Initiates an endorsement, calculates premium, orders CLUE report for newly added driver
     *
     * @param policyNumber given policy number
     * @param addDriverTd  specific details for the driver being added to the policy
     */
    public void initiateAddDriverEndorsement(String policyNumber, TestData addDriverTd) {
        mainApp().open();
        //SearchPage.openPolicy(policyNumber);
        SearchPage.openPolicy("CAAC952918550");
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        if(secondDriverFlag) {
            policy.getDefaultView().fillUpTo(getTestSpecificTD("Add_Driver2_Endorsement"), DriverTab.class, true);
        } else {
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        }
        if(updatePUFlag) {
            log.info("Updating first driver with PU as yes");
            tableDriverList.selectRow(1);
            tableActivityInformationList.selectRow(1);
            log.info("Current PU value"+ activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).getValue());
            activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).setValue("Yes");
        }

        policy.getDefaultView().fill(addDriverTd);

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();
        premiumAndCoveragesTab.submitTab();

        //Modify default test data to mask unnecessary steps
        TestData td = getPolicyTD()
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV.getLabel()));
        new DriverActivityReportsTab().fillTab(td);
    }

    /**
     * Binds current endorsement: calculates premium, navigates to bind page, and binds endorsement
     */
    public void bindEndorsement() {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();
    }

    // Move to R-63, run batch job part 1 and offline claims batch job
    public void runRenewalClaimOrderJob() {
        policyExpirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(63));
        LocalDateTime updatedTime = TimeSetterUtil.getInstance().getCurrentTime();
        assertThat(updatedTime).isEqualToIgnoringHours(policyExpirationDate.minusDays(63));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalClaimOrderAsyncJob);
    }

    protected void pas18317_verifyPermissiveUseIndicator() {
        TestData testData = getPolicyTD();
        List<TestData> testDataDriverData = new ArrayList<>();// Merged driver tab with 4 drivers
        testDataDriverData.add(testData.getTestData("DriverTab"));
        testDataDriverData.addAll(getTestSpecificTD("TestData_DriverTab_OfflineClaim_PU").resolveLinks().getTestDataList("DriverTab"));
        adjusted = testData.adjust("DriverTab", testDataDriverData).resolveLinks();

        //Create a policy with 4 drivers
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(adjusted, DriverTab.class, true);
        //Assert to check the PU indicator for company input in quote level
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Company Input");
            //PAS-18317: PU indicator will NOT show for NON FNI drivers
            softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent()).isFalse();
        });
        driverTab.submitTab();

        if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
            policy.getDefaultView().fillFromTo(adjusted, MembershipTab.class, DocumentsAndBindTab.class, true);

        } else if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            policy.getDefaultView().fillFromTo(adjusted, MembershipTab.class, DriverActivityReportsTab.class, true);
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
            driverTab.submitTab();

            //Modify default test data to mask unnecessary steps
            adjusted = getPolicyTD()
                    .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                    .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT.getLabel()))
                    .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV.getLabel()));

            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
            policy.getDefaultView().fillFromTo(adjusted, PremiumAndCoveragesTab.class, DocumentsAndBindTab.class, true);
        }

        documentsAndBindTab.submitTab();

        new PurchaseTab().fillTab(adjusted).submitTab();
        policyNumber = PolicySummaryPage.getPolicyNumber();
        log.info("Policy created successfully. Policy number is " + policyNumber);
        mainApp().close();

        //Initiate endorsement
        TestData addDriverTd = getTestSpecificTD("Add_PU_Claim_Driver_Endorsement");
        initiateAddDriverEndorsement(policyNumber, addDriverTd);

        //Navigate to Driver page and verify the clue claim is added to driver5
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        puIndicatorAssertions();       // Assert to check PU indicator check for clue claims in endorsment
        bindEndorsement();
    }

    public void compDLPuAssertions(String COMP_MATCH, String DL_MATCH, String PU_MATCH) {
        // Check 1st driver: Contains only Two Matched Claims (Verifying that PermissiveUse Claim with wrong dateOfLoss is not displayed)
        // Check 1st driver: FNI, has COMP and Permissive Use matched claims (2nd PermissiveUse Claim is not displayed, because of dateOfLoss Param > Claim dateOfLoss)
        //PAS-23269 - PU indicator check
        activityAssertions(4, 1, 2, 1, "Internal Claims", COMP_MATCH, true);
        activityAssertions(4, 1, 2, 2, "Internal Claims", PU_MATCH, true);
        //PAS-23269 - PU indicator check and Check 2nd driver: Has DL match claim
        activityAssertions(4, 2, 1, 1, "Internal Claims", DL_MATCH, false);
        buttonSaveAndExit.click();
    }

    // Assertions for clue claims  Tests
    //PAS-18317: PU indicator will NOT show for NON FNI drivers
    public void puIndicatorAssertions() {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(tableDriverList).hasRows(5);
            if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
                // Check 5th driver: Check the clue claims with permissive use indicator
                tableDriverList.selectRow(5);
            } else if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
                // Check 3rd driver: Check the clue claims with permissive use indicator
                tableDriverList.selectRow(3);
            }
            softly.assertThat(tableActivityInformationList).hasRows(1);
            softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("CLUE");
            softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent()).isFalse();
        });
    }

    // Assertions for COMP and DL Tests: PAS-22172
    public void puDropAssertions(String COMP_MATCH, String PU_MATCH) {
        // Check 1st driver: Contains only one Matched Claim (Verifying that comp claim has not moved)
        activityAssertions(5, 1, 1, 1, "Internal Claims", COMP_MATCH, false);

        // Check 5th driver: Original Permissive Use claim should be on the newly added driver
        activityAssertions(5, 5, 1, 1, "CLUE", PU_MATCH, false);
    }

    // Assertions for Name/DOB Tests
    public void nameDobYobAssertions(String LASTNAME_FIRSTNAME_DOB, String LASTNAME_FIRSTNAME, String LASTNAME_FIRSTINITAL_DOB, String LASTNAME_FIRSTNAME_YOB) {
        // Check 3rd driver
        // PAS-8310 - LASTNAME_FIRSTNAME_DOB Match
        activityAssertions(4, 3, 4, 2, "Internal Claims", LASTNAME_FIRSTNAME_DOB, false);
        // PAS-17894 - LASTNAME_FIRSTNAME & LASTNAME_FIRSTINITAL_DOB //PAS-21435 - Removed LASTNAME_YOB match logic. Claim 8FAZ88888OHS is now unmatched
        activityAssertions(4, 3, 4, 3, "Internal Claims", LASTNAME_FIRSTNAME, false);
        activityAssertions(4, 3, 4, 4, "Internal Claims", LASTNAME_FIRSTINITAL_DOB, false);
        // Check 4th driver.
        // PAS-8310 - LASTNAME_FIRSTNAME_YOB Match
        activityAssertions(4, 4, 1, 1, "Internal Claims", LASTNAME_FIRSTNAME_YOB, false);
    }

    private void activityAssertions(int totalDrivers, int driverRowNo, int totalActivities, int activityRowNo, String activitySource, String claimNumber, boolean checkPU) {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(tableDriverList).hasRows(totalDrivers);
            tableDriverList.selectRow(driverRowNo);
            softly.assertThat(tableActivityInformationList).hasRows(totalActivities);
            tableActivityInformationList.selectRow(activityRowNo);
            softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue(activitySource);
            softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(claimNumber);
            if (checkPU) {
                softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isEnabled());
            }
            else {
                softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent()).isFalse();
            }
        });
    }

    public void generateClaimRequest() {
        // Download the claim request
        String content = downloadClaimRequest();

        //PAS-2467 -  Check if request contains DL and PolicyNumber. Should NOT contain DL
        List<String> driverLicenseList = getDriverLicences(adjusted);
        assertThat(content)
                .contains("ClaimBatchRequest")
                .contains(policyNumber)
                .endsWith("ClaimBatchRequest>");
        driverLicenseList.forEach(l -> assertThat(content).doesNotContain(l));
    }

    // Move to R-46 and run batch job part 2 and offline claims receive batch job
    public void runRenewalClaimReceiveJob() {
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(46));
        DBService.get().executeUpdate(SQL_REMOVE_RENEWALCLAIMRECEIVEASYNCJOB_BATCH_JOB_CONTROL_ENTRY);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        JobUtils.executeJob(Jobs.renewalClaimReceiveAsyncJob);
    }

    /**
     * Method changes current date to policy expiration date and issues generated renewal image
     *
     * @param policyNumber given policy number
     */
    protected void issueGeneratedRenewalImage(String policyNumber) {
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        if (tableSearchResults.isPresent()) {
            tableSearchResults.getRow("Eff. Date",
                    TimeSetterUtil.getInstance().getCurrentTime().minusYears(1).format(DateTimeUtils.MM_DD_YYYY).toString())
                    .getCell(1).controls.links.getFirst().click();
        }

        buttonRenewals.click();
        policy.dataGather().start();
        premiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();
        payTotalAmtDue(policyNumber);
    }

    /**
     * Method updates CAS Response XML with given Driver Licence according to Claim Number
     *
     * @param claimToDriverLicenseMap given Driver Licence Number according to Claim Number
     * @param response                CAS Response
     */
    private void updateDriverLicence(Map<String, String> claimToDriverLicenseMap, CASClaimResponse response) {
        List<Claim> claims = response.getClaimLineItemList().stream()
                .flatMap(claimLineItem -> claimLineItem.getClaimList().stream())
                .collect(Collectors.toList());
        claims.forEach(c -> {
            String driverLicense = claimToDriverLicenseMap.get(c.getClaimNumber());
            if (driverLicense != null) {
                c.setDrivingLicenseNumber(driverLicense);
            }
        });
    }

    /**
     * Method Updates CAS Response value by given XML Tag Name
     *
     * @param updatableDateFieldValueMap given value according to Claim Number
     * @param response                   CAS Response
     * @param updatableDateField         given XML Tag name
     */
    protected void updateDatesForClaim(Map<String, String> updatableDateFieldValueMap, CASClaimResponse response, String updatableDateField) {
        List<Claim> claims = response.getClaimLineItemList().stream()
                .flatMap(claimLineItem -> claimLineItem.getClaimList().stream())
                .collect(Collectors.toList());
        claims.forEach(c -> {
            String updatableDateFieldValue = updatableDateFieldValueMap.get(c.getClaimNumber());
            if (updatableDateFieldValue != null) {
                try {
                    Field field = Claim.class.getDeclaredField(updatableDateField);
                    field.setAccessible(true);
                    field.set(c, updatableDateFieldValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalStateException("Can't update field " + updatableDateField + " by " + updatableDateFieldValue, e);
                }
            }
        });
    }

    protected void setPolicyNumber(String policyNumber, CASClaimResponse response) {
        response.getClaimLineItemList().forEach(claimLineItem -> {
            claimLineItem.setAgreementNumber(policyNumber);
            claimLineItem.getClaimList().forEach(claim -> claim.setClaimPolicyReferenceNumber(policyNumber));
        });
    }

    protected String getCasResponseFileName() {
        String prefix = TimeSetterUtil.getInstance().getCurrentTime()
                .truncatedTo(ChronoUnit.SECONDS).format(DATE_TIME_FORMATTER);
        return String.format(CAS_RESPONSE_FILE_NAME_TEMPLATE, CAS_RESPONSE_PATH + File.separator + prefix);
    }

    protected List<String> getDriverLicences(@Nonnull TestData testData) {
        List<String> dls = new ArrayList<>();
        testData.getTestDataList("DriverTab").forEach(t -> dls.add(t.getValue("License #")));
        return dls;
    }

    /**
     * Method returns content as String of CAS Request file
     *
     * @return CAS claim request content
     */
    protected String downloadClaimRequest() {
        String claimRequestFolder = Jobs.getClaimOrderJobFolder();
        List<String> requests = RemoteHelper.get().getListOfFiles(claimRequestFolder);
        assertThat(requests).hasSize(1);
        String claimRequest = requests.get(0);
        RemoteHelper.get().downloadFile(claimRequest, CAS_REQUEST_PATH);
        File claimRequestFile = new File(CAS_REQUEST_PATH + File.separator + claimRequest);
        assertThat(claimRequestFile).exists().isFile().canRead().isAbsolute();
        String content = contentOf(claimRequestFile, Charset.defaultCharset());
        log.info("Downloaded CAS claim request: {}" + content);
        return content;
    }

    /**
     * Method returns content as String of pas-admins wrapper.log file
     *
     * @return PAS Admin Log File content
     */
    protected String downloadPasAdminLog() {
        String pasAdminLogFolder = PasAdminLogGrabber.getPasAdminLogFolder();
        RemoteHelper.get().getListOfFiles(pasAdminLogFolder);
        RemoteHelper.get().downloadFile("wrapper.log", PAS_ADMIN_LOG_PATH);
        File pasAdminLogFile = new File(PAS_ADMIN_LOG_PATH + File.separator + "wrapper.log");
        assertThat(pasAdminLogFile).exists().isFile().canRead().isAbsolute();
        String content = contentOf(pasAdminLogFile, Charset.defaultCharset());
        log.info("Downloaded PAS Admin Log File: {}" + content);
        return content;
    }

    /**
     * Method goes though all Claim Analytics items and returns required value according to claimNumber and policyNumber
     *
     * @param listOfClaims list Of Claim JSONs as strings;
     * @param claimNumber  given claim number
     * @param policyNumber given policy number
     * @param key          key of value which you want to get
     */
    protected String retrieveClaimValueFromAnalytics(List<String> listOfClaims, String claimNumber, String policyNumber, String key) {
        String claimValue = null;

        for (int i = 0; i <= listOfClaims.size() - 1; i++) {
            JSONObject specificClaimData = new JSONObject(listOfClaims.get(i)).getJSONObject("claims-assignment");
            if (specificClaimData.getString("claimNumber").equals(claimNumber) && specificClaimData.getString("policyNumber").equals(policyNumber)) {
                claimValue = specificClaimData.getString(key);
            } else {
                log.info("Moving to the next Claim List Item.. Required Claim in this Claim Analytics JSON Item couldn't be found. Claim Number: "
                        + claimNumber);
            }
        }
        return claimValue;
    }

    /**
     * Method creates CAS Response file and updates required fields: Policy Number, Driver Licence, Claim Dates: Date Of Loss, Close Date, Open Date
     *
     * @param policyNumber         given Policy Number
     * @param dataModelFileName    given CAS Response data model
     * @param claimToDriverLicence if != null, given Driver Licence according to Claim Number
     * @param claimDatesToUpdate   if != null, given Claim Dates according to Claim Number
     */
    private void createCasClaimResponseAndUpload(String policyNumber, String dataModelFileName,
                                                 Map<String, String> claimToDriverLicence, Map<String, String> claimDatesToUpdate) {
        // Create Cas response file
        String casResponseFileName = getCasResponseFileName();
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper(dataModelFileName, casResponseFileName);
        File claimResponseFile = batchClaimHelper.processClaimTemplate((response) -> {
            setPolicyNumber(policyNumber, response);
            if (claimToDriverLicence != null)
                updateDriverLicence(claimToDriverLicence, response);
            if (claimDatesToUpdate != null) {
                updateDatesForClaim(claimDatesToUpdate, response, ClaimCASResponseTags.TagNames.CLAIM_DATE_OF_LOSS);
                updateDatesForClaim(claimDatesToUpdate, response, ClaimCASResponseTags.TagNames.CLAIM_CLOSE_DATE);
                updateDatesForClaim(claimDatesToUpdate, response, ClaimCASResponseTags.TagNames.CLAIM_OPEN_DATE);
            }
        });
        String content = contentOf(claimResponseFile, Charset.defaultCharset());
        log.info("Generated CAS claim response filename {} content {}", casResponseFileName, content);

        // Upload claim response
        RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(),
                Jobs.getClaimReceiveJobFolder() + File.separator + claimResponseFile.getName());
    }

    /**
     * Method creates CAS Response file and Uploads to required folder: With Updated Policy Number only
     *
     * @param policyNumber      given Policy Number
     * @param dataModelFileName given CAS Response data model
     */
    public void createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(String policyNumber, String dataModelFileName) {
        createCasClaimResponseAndUpload(policyNumber, dataModelFileName, null, null);
    }

    /**
     * Method creates CAS Response file and Uploads to required folder: With Updated Policy Number & Driver License
     *
     * @param policyNumber         given policy number
     * @param dataModelFileName    given CAS Response data model
     * @param claimToDriverLicence given Driver License according to Claim Number
     */
    public void createCasClaimResponseAndUploadWithUpdatedDL(String policyNumber, String dataModelFileName,
                                                             Map<String, String> claimToDriverLicence) {
        createCasClaimResponseAndUpload(policyNumber, dataModelFileName, claimToDriverLicence, null);
    }

    /**
     * Method creates CAS Response file and Uploads to required folder: With Updated Policy Number & Claim Dates: Date Of Loss, Close Date, Open Date
     *
     * @param policyNumber       given Policy Number
     * @param dataModelFileName  given CAS Response data model
     * @param claimDatesToUpdate given Claim Dates according to Claim Number
     */
    public void createCasClaimResponseAndUploadWithUpdatedDates(String policyNumber, String dataModelFileName,
                                                                Map<String, String> claimDatesToUpdate) {
        createCasClaimResponseAndUpload(policyNumber, dataModelFileName, null, claimDatesToUpdate);
    }

    //Method to send JSON Request to Claims Matching Micro Service
    public static ClaimsAssignmentResponse runJsonRequestPostClaims(String claimsRequest) {
        RestRequestInfo<ClaimsAssignmentResponse> restRequestInfo = new RestRequestInfo<>();
        restRequestInfo.url = CLAIMS_URL;
        restRequestInfo.bodyRequest = claimsRequest;
        restRequestInfo.responseType = ClaimsAssignmentResponse.class;
        return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.POST);
    }

    protected void testClaimsAssigmentAssertion(ClaimsAssignmentResponse microServiceResponse) {
        //Throw the microServiceResponse to log - assists with debugging
        log.info(microServiceResponse.toString());
        //Create a list of all the expected UNMATCHED claim numbers
        String[] expectedClaimNumbers = {"1TAZ1111OHS", "17894-2222OHS", "17894-3333OHS", "17894-55555OHS", "17894-66666OHS", "17894-77777OHS", "17894-88888OHS", "17894-99999OHS", "18431-44444OHS", "18431-55555OHS"};
        ArrayList<String> expectedUnmatchedClaims = new ArrayList<>();
        expectedUnmatchedClaims.addAll(Arrays.asList(expectedClaimNumbers));

        //Create a list of all the actual UNMATCHED claim numbers
        ArrayList<String> actualUnmatchedClaims = new ArrayList<>();
        int x = 0;
        while (x < microServiceResponse.getUnmatchedClaims().size()) {
            String claimNumber = microServiceResponse.getUnmatchedClaims().get(x).getClaimNumber();
            actualUnmatchedClaims.add(claimNumber);
            x++;
        }

        //Verify the actual UNMATCHED claims equal the expected UNMATCHED claims
        //PAS-21435 - Removed LASTNAME_YOB match logic. These claims will now be unmatched
        log.info("expected: " + expectedUnmatchedClaims);
        log.info("actual: " + actualUnmatchedClaims);
        assertThat(actualUnmatchedClaims).isEqualTo(expectedUnmatchedClaims);

        //Create a list of all the expected MATCH CODES (Last 3: PERMISSIVE_USE to cover all possible cases of PU)
        String[] expectedCodes = {"EXISTING_MATCH", "COMP", "DL", "LASTNAME_FIRSTNAME_DOB", "LASTNAME_FIRSTNAME_YOB", "LASTNAME_FIRSTNAME", "LASTNAME_FIRSTINITAL_DOB", "PERMISSIVE_USE", "PERMISSIVE_USE", "PERMISSIVE_USE"};
        ArrayList<String> expectedMatchCodes = new ArrayList<>();
        expectedMatchCodes.addAll(Arrays.asList(expectedCodes));

        //Create a list of all the actual MATCH CODES
        ArrayList<String> actualMatchCodes = new ArrayList<>();
        int y = 0;
        while (y < microServiceResponse.getMatchedClaims().size()) {
            String matchcode = microServiceResponse.getMatchedClaims().get(y).getMatchCode();
            actualMatchCodes.add(matchcode);
            y++;
        }

        //Verify the actual MATCH CODES equal the expected MATCH CODES
        //PAS-14679 - Match Logic: DL Number
        //PAS-14058 - Match Logic: COMP
        //PAS-8310  - Match Logic: LASTNAME_FIRSTNAME_DOB, LASTNAME_FIRSTNAME_YOB
        //PAS-17894 - Match Logic: LASTNAME_FIRSTNAME, LASTNAME_FIRSTINITAL_DOB, & LASTNAME_YOB
        //PAS-18300 - Match Logic: PERMISSIVE_USE
        log.info("expected match codes: " + expectedMatchCodes);
        log.info("actual match codes: " + actualMatchCodes);
        assertThat(actualMatchCodes).isEqualTo(expectedMatchCodes);
    }

    /**
     * @author Chris Johns
     * PAS-22172 - END - CAS: reconcile permissive use claims when driver/named insured is added (avail for rating)
     * @name Test Offline STUB/Mock: reconcile permissive use claims when driver/named insured is added
     * @scenario Test Steps:
     * 1. Create a Policy with 3 drivers; FNI will get X PU Claims
     * 2. Move time to R-63 and run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 3. Create CAS Response File Thru Automation Framework - This is the 'Offline Batch Job' step
     * 4. Move Time to R-46 and run Renewal Part2 + "claimsRenewBatchReceiveJob" - X PU claims are assigned
     * 5. Retrieve policy and enter renewal image
     * 6. Verify all PU claims are assigned to the FNI
     * 7. Accept a payment and renew the policy
     * 8. Initiate an endorsement
     * 9. Add an AFR driver who's CLUE report will return a claim that matches one of the PU claims on the FNI
     * 10. Calculate premium and order CLUE report
     * 11. Navigate to the drive tab, and verify the PU claim was moved from the FNI to the newly added driver
     * @details Clean Path. Expected Result is that PU claim will be move from the FNI to the newly added driver
     */
    public void reconcilePUEndorsementAFRBody() {
        String COMP_DL_PU_CLAIMS_DATA_MODEL;
        Map<String, String> CLAIM_TO_DRIVER_LICENSE;

        if (getPolicyType().getShortName().equalsIgnoreCase(PolicyType.AUTO_CA_CHOICE.getShortName())) {
            COMP_DL_PU_CLAIMS_DATA_MODEL = "comp_dl_pu_claims_data_model_choice.yaml";
            CLAIM_TO_DRIVER_LICENSE = ImmutableMap.of(CLAIM_NUMBER_1, "D1278111", CLAIM_NUMBER_2, "D1278111");
        } else {
            COMP_DL_PU_CLAIMS_DATA_MODEL = "comp_dl_pu_claims_data_model_select.yaml";
            CLAIM_TO_DRIVER_LICENSE = ImmutableMap.of(CLAIM_NUMBER_1, "D5435433", CLAIM_NUMBER_2, "D5435433");
        }

        // Toggle ON PermissiveUse Logic
        // Set DATEOFLOSS Parameter in DB: Equal to Claim3 dateOfLoss
        // Set RISKSTATECD in DB to get policy DATEOFLOSS working
        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-NOV-18"));

        createPolicyMultiDrivers();    // Create Customer and Policy with 4 drivers
        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedDL(policyNumber, COMP_DL_PU_CLAIMS_DATA_MODEL, CLAIM_TO_DRIVER_LICENSE);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        // Enter renewal image and verify claim presence
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        // Check 1st driver: FNI, has the COMP match claim & PU Match Claim. Also Making sure that Claim4: 1002-10-8704-INVALID-dateOfLoss from data model is not displayed
        // Check 2nd driver: Has DL match claim
        compDLPuAssertions(CLAIM_NUMBER_1, CLAIM_NUMBER_2, CLAIM_NUMBER_3);
        mainApp().close();

        //Move time to R-35 and run batch jobs:
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));

        //Accept Payment and renew the policy
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);

        //Set test date for endorsement
        TestData addDriverTd = getTestSpecificTD("Add_PU_Claim_Driver_Endorsement_CA");
        //Initiate an endorsement: Add AFR Driver, calculate premium and order clue
        initiateAddDriverEndorsement(policyNumber, addDriverTd);

        //Navigate to Driver page and verify PU claim moved from FNI to newly added driver
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        puDropAssertions(CLAIM_NUMBER_1, CLAIM_NUMBER_3);

        //Bind Endorsement
        bindEndorsement();
    }

    public void pas24587_CASClueReconcilePUAFRUserFlagged(){
        String DL_NAME_RECONCILEFNICLAIMS_DATA_MODEL;
        Map<String, String> CLAIM_TO_DRIVER_LICENSE;

        if (getPolicyType().getShortName().equalsIgnoreCase(PolicyType.AUTO_CA_CHOICE.getShortName())) {
            DL_NAME_RECONCILEFNICLAIMS_DATA_MODEL = "dl_name_reconcileFNIclaims_data_model_choice.yaml";
            CLAIM_TO_DRIVER_LICENSE = ImmutableMap.of(CLAIM_NUMBER_1, "D1278222", CLAIM_NUMBER_2, "D1278999");
        } else {
            DL_NAME_RECONCILEFNICLAIMS_DATA_MODEL = "dl_name_reconcileFNIclaims_data_model_select.yaml";
            CLAIM_TO_DRIVER_LICENSE = ImmutableMap.of(CLAIM_NUMBER_1, "D1278222", CLAIM_NUMBER_2, "D1278999");
        }

        // Toggle ON PermissiveUse Logic
        // Set DATEOFLOSS Parameter in DB: Equal to Claim3 dateOfLoss
        // Set RISKSTATECD in DB to get policy DATEOFLOSS working
        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-NOV-18"));

        // Create Customer and Policy with one driver
        TestData testDataForFNI = getTestSpecificTD("TestData_DriverTab_ReconcileFNIclaims_PU").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForFNI);
        policyNumber = openAppAndCreatePolicy(adjusted);
        log.info("Policy created successfully. Policy number is " + policyNumber);

        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedDL(policyNumber, DL_NAME_RECONCILEFNICLAIMS_DATA_MODEL, CLAIM_TO_DRIVER_LICENSE);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        // Enter renewal image and verify claim presence
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        // Check Driver1 has CAS claims with PU as NO
        assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).getValue().equals("Internal Claims"));
        assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).getValue().equals("No"));
        mainApp().close();

        //Move time to R-35 and run batch jobs:
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));
        //Accept Payment and renew the policy
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);

        //Set test date for endorsement
        TestData addDriverTd = getTestSpecificTD("Add_PU_Claim_Driver_Endorsement_CA");
        //Initiate an endorsement: Add AFR Driver, calculate premium and order clue
        updatePUFlag = true;
        initiateAddDriverEndorsement(policyNumber, addDriverTd);
        //Navigate to Driver page and verify PU claim moved from FNI to newly added driver
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        //Check the Driver 2 has CLUE claim
        activityAssertions(2, 2, 1, 1, "CLUE", CAS_CLUE_CLAIM,false);
        //Bind Endorsement
        bindEndorsement();
    }

    public void pas24587_ClueReconcilePUAFRUserFlagged(){
        //Create a policy with 2 drivers
        TestData testDataForFNI = getTestSpecificTD("TestData_DriverTab_ClueReconcileFNIclaims_PU").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForFNI);
        policyNumber = openAppAndCreatePolicy(adjusted);
        log.info("Policy created successfully. Policy number is " + policyNumber);
        mainApp().close();

        //Initiate 1st endorsement
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
//        SearchPage.openPolicy("CAAS952918580");
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        //Driver2 clue claim is reassigned to driver1
        tableDriverList.selectRow(2);
        tableActivityInformationList.selectRow(1);
        tableActivityInformationList.getRow(1).getCell(tableActivityInformationList.getColumnsCount()).controls.links.get("Reassign").click();
        activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.SELECT_DRIVER_DIALOG).getAsset(AutoCaMetaData.DriverTab.SelectDriverDialog.ASSIGN_TO).setValueByIndex(1);
        activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.SELECT_DRIVER_DIALOG).getAsset(AutoCaMetaData.DriverTab.SelectDriverDialog.BTN_OK).click();
        //Change Driver1 PU flag to yes
        tableDriverList.selectRow(1);
        tableActivityInformationList.selectRow(1);
        assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).getValue().equals("CLUE"));
        assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).getValue().equals("No"));
        //Remove driver2
        tableDriverList.getRow(2).getCell(tableDriverList.getColumnsCount()).controls.links.get("Remove").click();
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REMOVE_DRIVER_DIALOG).getAsset(AutoCaMetaData.DriverTab.SelectDriverDialog.BTN_OK).click();

        bindEndorsement();

        policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(2));


        //Initiate 2nd endorsement
        secondDriverFlag = true;
        TestData addSecondDriverTd = getPolicyTD("Endorsement", "TestData");
        //Add Driver2 again and Order Clue report
        initiateAddDriverEndorsement(policyNumber, addSecondDriverTd);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        //Check driver2 is assigned back with CLUE claim from driver1
        activityAssertions(2, 2, 1, 1, "CLUE", CLUE_CLAIM,false);
    }
}
