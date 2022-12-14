package aaa.modules.regression.sales.template.functional;

import static aaa.common.pages.SearchPage.tableSearchResults;
import static aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab.*;
import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;
import static aaa.main.pages.summary.PolicySummaryPage.labelPolicyNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.contentOf;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.testng.annotations.BeforeTest;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.PrivilegeEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.ClaimCASResponseTags;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.claim.datamodel.claim.Claim;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

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
    public static final String SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = 'TRUE' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'PermissiveUse' and PRODUCTCD = 'AAA_CSA'";
    public static final String SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS = "UPDATE LOOKUPVALUE SET DATEOFLOSS = '%s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'PermissiveUse' and PRODUCTCD = 'AAA_CSA'";
    private static final String CLAIMS_URL = "https://claims-assignment-master.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1"; //Post-Permissive Use
    public static final String SQL_REMOVE_RENEWALCLAIMRECEIVEASYNCJOB_BATCH_JOB_CONTROL_ENTRY = "DELETE FROM BATCH_JOB_CONTROL_ENTRY WHERE jobname='renewalClaimReceiveAsyncJob'";
    public static final String CLAIMS_MICROSERVICE_ENDPOINT = "select * from PROPERTYCONFIGURERENTITY where propertyname = 'aaaClaimsMicroService.microServiceUrl'";

    protected TestData adjusted;
    protected LocalDateTime policyExpirationDate;
    protected LocalDateTime policyEffectiveDate;
    protected String policyNumber;

    protected static DriverTab driverTab = new DriverTab();
    protected static GeneralTab generalTab = new GeneralTab();
    protected static PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    protected static DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    protected static AssignmentTab assignmentTab = new AssignmentTab();
    protected static PurchaseTab purchaseTab = new PurchaseTab();
    protected static ErrorTab errorTab = new ErrorTab();
    protected static DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
    protected static ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();

    private static final String CLAIM_NUMBER_1 = "1002-10-8702";
    private static final String CLAIM_NUMBER_2 = "1002-10-8703";
    private static final String CLAIM_NUMBER_3 = "1002.10>8704";
    private static final String CLAIM_NUMBER_1_GDD = "Claim-GDD-111";
    private static final String CLAIM_NUMBER_2_GDD = "Claim-GDD-222";

    private static final String[] CLAIM_NUMBERS_PU_DEFAULTING = {"PU_DEFAULTING_CMP", "PU_DEFAULTING_1", "PU_DEFAULTING_2", "PU_DEFAULTING_3",
            "PU_DEFAULTING_4", "PU_DEFAULTING_5", "PU_DEFAULTING_6"};

    private static final String CAS_CLUE_CLAIM = "1002-10-8704";
    private static final String CLUE_CLAIM = "1002-10-8799";

    private static final String COMP_DL_PU_CLAIMS_DATA_MODEL_CHOICE = "comp_dl_pu_claims_data_model_choice.yaml";
    private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE_CHOICE = ImmutableMap.of(CLAIM_NUMBER_1, "D1278111", CLAIM_NUMBER_2, "D1278111");
    private static final String COMP_DL_PU_CLAIMS_DATA_MODEL_SELECT = "comp_dl_pu_claims_data_model_select.yaml";
    private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE_SELECT = ImmutableMap.of(CLAIM_NUMBER_1, "D5435433", CLAIM_NUMBER_2, "D5435433");
    private static final String GDD_PU_CLAIMS_DATA_MODEL = "gdd_PUClaims_data_model.yaml";
    private static final String PU_CLAIMS_DEFAULTING_DATA_MODEL = "pu_claims_defaulting_data_model.yaml";
    private static final String PU_CLAIMS_DEFAULTING_2ND_DATA_MODEL = "pu_claims_defaulting_2nd_data_model.yaml"; //TODO: will be used after PAS-26322
    protected boolean updatePUFlag = false;
    protected boolean secondDriverFlag = false;
    protected boolean fillContactInfo = false;
    protected boolean MDD = false;
    private static final String RESTRICT_FNI_MASSAGE = "The select named insured has not been established as a \"named insured driver\" on the driver tab";

    @BeforeTest
    public void prepare() {
        // Toggle ON PermissiveUse Logic & Set DATEOFLOSS Parameter in DB
        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-NOV-16"));
        log.info("Updated PU flag in DB");
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

        // Retrieve policy and enter renewal image
        retrieveRenewal(policyNumber);

        //PAS-29098: Verify Claim Order and Receipt dates on the Driver and Activity Reports Tab are NOT blank
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
        int x = 1;
        while (x < 4 ){
            assertThat(DriverActivityReportsTab.tableInternalClaim.getRow(x)
                    .getCell(AutoCaMetaData.DriverActivityReportsTab.OrderInternalClaimsRow.ORDER_DATE.getLabel()).getValue()).isNotEmpty();
            assertThat(DriverActivityReportsTab.tableInternalClaim.getRow(x)
                    .getCell(AutoCaMetaData.DriverActivityReportsTab.OrderInternalClaimsRow.RECEIPT_DATE.getLabel()).getValue()).isNotEmpty();
            x++;
        }

        // Check 1st driver: FNI, has the COMP match claim & PU Match Claim. Also Making sure that Claim4: 1002-10-8704-INVALID-dateOfLoss from data model is not displayed
        // Check 2nd driver: Has DL match claim
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        compDLPuAssertions(CLAIM_NUMBER_1, CLAIM_NUMBER_2, CLAIM_NUMBER_3);
        mainApp().close();

        //Run the renewal job and pay the bill
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));

        //Accept Payment and renew the policy
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(BatchJob.policyStatusUpdateJob);

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

        TestData td_driver_endorse = getTestSpecificTD("TestData_MDD_Endorse");
        if (MDD){
            mainApp().open();
            SearchPage.openPolicy(policyNumber);
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus30Days"));
            //Add a Driver who is Eligible for MDD in Mid Term Endorsement and Verify MDD is applied
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
            driverTab.fillTab(td_driver_endorse).submitTab();
            //Select the newly added Driver as "Manually Rated Driver" in the Assignment Tab
            return;
        }
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        if (secondDriverFlag) {
            policy.getDefaultView().fillUpTo(getTestSpecificTD("Add_Driver2_Endorsement"), DriverTab.class, true);
        } else {
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        }
        if (updatePUFlag) {
            log.info("Updating first driver with PU as yes");
            tableDriverList.selectRow(1);
            tableActivityInformationList.selectRow(2);
            log.info("Current PU value" + activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).getValue());
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
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
        JobUtils.executeJob(BatchJob.renewalClaimOrderAsyncJob);
    }

    /**
     * Method changes'First Named Insured' to the desired Insured. First Named Insured index starts at zero
     *
     * @param namedInsuredNumber - Insured who will become the First Named Insured
     */
    public void changeFNIGeneralTab(int namedInsuredNumber) {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.GENERAL.get());
        generalTab.getAssetList().getAsset(AutoCaMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel(), ComboBox.class).setValueByIndex(namedInsuredNumber);
        //PAS-28399: Check for Restrict FNI message
        if (Page.dialogConfirmation.labelMessage.getValue().contains(RESTRICT_FNI_MASSAGE)) {
            assertThat(Page.dialogConfirmation.labelMessage.getValue()).contains(RESTRICT_FNI_MASSAGE);
            Page.dialogConfirmation.buttonCancel.click();
        } else {
            assertThat(Page.dialogConfirmation.labelMessage.getValue()).doesNotContain(RESTRICT_FNI_MASSAGE);
            Page.dialogConfirmation.confirm();
        }
        //Reset Contact Info - blanks out after FNI change at New Business
        if (fillContactInfo) {
            generalTab.getContactInfoAssetList().getAsset(AutoCaMetaData.GeneralTab.ContactInformation.HOME_PHONE_NUMBER).setValue("6025557777");
            generalTab.getContactInfoAssetList().getAsset(AutoCaMetaData.GeneralTab.ContactInformation.PREFERED_PHONE_NUMBER).setValue("Home Phone");
        }
        generalTab.submitTab();
    }

    /**
     * Method opens app, retrieves policy, and enters data gathering in renewal image
     *
     * @param policyNumber
     */
    public void retrieveRenewal(String policyNumber) {
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        buttonRenewals.click();
        policy.dataGather().start();
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

        purchaseTab.fillTab(adjusted).submitTab();
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
        activityAssertions(5, 1, 1, 1, "Internal Claims", COMP_MATCH, true);

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
            } else {
                softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent()).isFalse();
            }
        });
    }

    private void overrideErrorTab() {
        ErrorTab errorTab = new ErrorTab();
        if (errorTab.isVisible()) {
            errorTab.overrideAllErrors();
            errorTab.buttonOverride.click();
            premiumAndCoveragesTab.submitTab();
        }
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
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
        JobUtils.executeJob(BatchJob.renewalClaimReceiveAsyncJob);
    }

    /**
     * Method changes current date to policy expiration date and issues generated renewal image
     *
     * @param policyNumber given policy number
     */
    protected void issueGeneratedRenewalImage(String policyNumber, Boolean validateGDD) {
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        if (tableSearchResults.isPresent()) {
            tableSearchResults.getRow("Eff. Date",
                    TimeSetterUtil.getInstance().getCurrentTime().minusYears(1).format(DateTimeUtils.MM_DD_YYYY))
                    .getCell(1).controls.links.getFirst().click();
        }

        buttonRenewals.click();
        policy.dataGather().start();
        premiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());

        if (BooleanUtils.isTrue(validateGDD)) {
            validateGDD();
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());

            // Assert that permissive rule is thrown for CLUE and can be overridden for LIFE: PAS-22609
            validateOverridableCLUEPPURule(ErrorEnum.Duration.LIFE);
        }

        payTotalAmtDue(policyNumber);
    }

    /**
     * Method changes current date to policy expiration date, validates Good Driver Discount and issues generated renewal image
     *
     * @param policyNumber given policy number
     */
    protected void issueGeneratedRenewalImageWithGDDValidation(String policyNumber) {
        issueGeneratedRenewalImage(policyNumber, true);
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
        String claimRequestFolder = BatchJob.getRenewalClaimOrderAsyncJobParameters().get(BatchJob.ParametersName.PROCESSED_FOLDER);
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

    /*
    Method verifies that PU indicator has correct defaulted values:
    used for pas25162_permissiveUseIndicatorDefaulting
     */
    protected void verifyPUvalues() {
        CustomSoftAssertions.assertSoftly(softly -> {
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();

            // Check 1st driver: Contains 7 Matched Claims (Verifying PU default value)
            softly.assertThat(tableActivityInformationList).hasRows(7);

            // Verifying PU default value for all Claims
            for (int i = 0; i <= 6; i++) {
                tableActivityInformationList.selectRow(i + 1);
                if (i == 6) { //PERMISSIVE_USE match = Yes
                    softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBERS_PU_DEFAULTING[6]);
                    softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS)).hasValue("Yes");
                } else {
                    softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBERS_PU_DEFAULTING[i]);
                    softly.assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS)).hasValue("No");
                }

            }
        });
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
     * 9. Add an AFR driver who's CLUE report will return a claim that matches one of the PU claims on the FNI. Claim numbers are compared and matched ignoring the format differences.
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

        createPolicyMultiDrivers();    // Create Customer and Policy with 4 drivers
        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedDL(policyNumber, COMP_DL_PU_CLAIMS_DATA_MODEL, CLAIM_TO_DRIVER_LICENSE);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy and enter renewal image
        retrieveRenewal(policyNumber);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());

        // Check 1st driver: FNI, has the COMP match claim & PU Match Claim. Also Making sure that Claim4: 1002-10-8704-INVALID-dateOfLoss from data model is not displayed
        // Check 2nd driver: Has DL match claim
        compDLPuAssertions(CLAIM_NUMBER_1, CLAIM_NUMBER_2, CLAIM_NUMBER_3);
        mainApp().close();

        //Move time to R-35 and run batch jobs:
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));

        //Accept Payment and renew the policy
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(BatchJob.policyStatusUpdateJob);

        //Set test date for endorsement
        TestData addDriverTd = getTestSpecificTD("Add_PU_Claim_Driver_Endorsement_CA");
        //Initiate an endorsement: Add AFR Driver, calculate premium and order clue
        initiateAddDriverEndorsement(policyNumber, addDriverTd);

        //Navigate to Driver page and verify PU claim moved from FNI to newly added driver
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        puDropAssertions(CLAIM_NUMBER_1, CAS_CLUE_CLAIM);

        //Bind Endorsement
        bindEndorsement();
    }

    /*
    Method/Test for CA Choice & Select: TestClaimsImpactOnDiscounts.pas18303_goodDriverDiscountForPUClaims
     */
    public void pas18303_goodDriverDiscountForPUClaims() {

        String claim1_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(93).toLocalDate().toString();
        String claim2_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(80).toLocalDate().toString();

        Map<String, String> UPDATE_CAS_RESPONSE_DATE_FIELDS = ImmutableMap.of(CLAIM_NUMBER_1_GDD, claim1_dates, CLAIM_NUMBER_2_GDD, claim2_dates);

        //Adjusted Test Data for: CCInput/CLUE/Internal Claims
        TestData testDataForCLUE = getTestSpecificTD("TestData_DriverTab_DiscountsGDD").resolveLinks();
        TestData td = getPolicyTD().adjust(testDataForCLUE);

        //Adjusted Test Data after assertions
        TestData tdAfterValidation = getTestSpecificTD("TestData_DriverActivityReportsTab").resolveLinks();
        TestData td2 = getPolicyTD().adjust(tdAfterValidation);

        createQuoteAndFillUpTo(td, PremiumAndCoveragesTab.class, true);

        // Overriding Errors caused by created ActivityInformation entries (Auto Select specific Rules)
        premiumAndCoveragesTab.submitTab();
        if (errorTab.isVisible()) {
            errorTab.overrideAllErrors();
            errorTab.buttonOverride.click();
            premiumAndCoveragesTab.submitTab();
        }

        // Verify GDD during NB quote, Also Verify that PU ind is not shown for NON First Named Insured
        validateGDDAndPUIndicatorOnNB(td, td2);

        // Retrieve Internal Claims
        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        createCasClaimResponseAndUploadWithUpdatedDates(policyNumber, GDD_PU_CLAIMS_DATA_MODEL, UPDATE_CAS_RESPONSE_DATE_FIELDS);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Verify GDD during: Renewal Quote, Endorsement Quote, Rewritten Quote
        validateGDDonRenewalEndorsementRewrittenQuote();

    }

    /*
    Method Validates P&C tab, and that Good Driver Discount is applied with Permissive Use Claims only:
    used for pas18303_goodDriverDiscountForPUClaims
     */
    protected void validateGDD() {
        String CLUE_Dates = TimeSetterUtil.getInstance().getCurrentTime().minusDays(90).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());

        //Making sure that PU = Yes, and its included in rating.
        ActivityInformationMultiAssetList activityInformationAssetList = new DriverTab().getActivityInformationAssetList();
        viewDriver(1);

        for (int i = 1; i <= tableActivityInformationList.getAllRowsCount(); i++) {
            tableActivityInformationList.selectRow(i);

            if (activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OVERRIDE_ACTIVITY_DETAILS.getLabel(), RadioGroup.class).isPresent()) {
                if (activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OVERRIDE_ACTIVITY_DETAILS.getLabel(), RadioGroup.class).getValue().equals("No")) {
                    activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OVERRIDE_ACTIVITY_DETAILS.getLabel(), RadioGroup.class).setValue("Yes");
                    activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), TextBox.class).setValue(CLUE_Dates);
                }
            }

            // With this condition CLUE default value in UI will be covered. Test will fail if PU will be null
            if (activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS.getLabel(), RadioGroup.class).getValue().equals("No")) {
                activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS.getLabel(), RadioGroup.class).setValue("Yes");
            }
            activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_YAF.getLabel(), RadioGroup.class).setValue("Yes");
        }

        //Verify That Discount is Applied with Permissive Use Claims
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();
        assertThat(PremiumAndCoveragesTab.tableDiscounts.getColumn(1).getCell(1).getValue()).contains("Good Driver");

        //Negative Case: Make One Claimas non PU
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        tableActivityInformationList.selectRow(1);
        activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS.getLabel(), RadioGroup.class).setValue("No");

        //Verify That Discount is NOT Applied when one Claim is not PU Claim
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();

        // If Discounts table is not visible, means that GDD discounts is not applied (Auto CA Select specific)
        if (PremiumAndCoveragesTab.tableDiscounts.isPresent()) {
            assertThat(PremiumAndCoveragesTab.tableDiscounts.getColumn(1).getCell(1).getValue()).doesNotContain("Good Driver");
        }
    }

    /*
    Method Validates P&C tab, and that Good Driver Discount is applied with Permissive Use Claims only: Renewal, Endorsement, Rewritten Quotes:
    used for pas18303_goodDriverDiscountForPUClaims
     */
    protected void validateGDDonRenewalEndorsementRewrittenQuote() {
        // Verify GDD during Renewal Quote Creation
        issueGeneratedRenewalImageWithGDDValidation(policyNumber);

        // Verify GDD during Endorsement Quote Creation
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        validateGDD();
        premiumAndCoveragesTab.cancel(false);
        Page.dialogConfirmation.buttonDeleteEndorsement.click();

        // Verify GDD during Rewritten Quote Creation
        buttonRenewals.click();
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
        policy.dataGather().start();
        validateGDD();
    }

    /*
    Method Validates Driver tab that PU Indicator is not visible for Non First Named Insured Driver:
    used for pas18303_goodDriverDiscountForPUClaims
     */
    protected void validateNonFNIPermissiveUse() {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        viewDriver(2);

        //Verify that 'Permissive Use Loss?' is not visible for Non First Named Insured
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(!activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent());
        });
    }

    /*
    Method Validates CLUE Permissive Use Overridable Rule: PAS-22609
    used for pas18303_goodDriverDiscountForPUClaims
     */
    protected void validateOverridableCLUEPPURule(ErrorEnum.Duration duration) {
        documentsAndBindTab.submitTab();

        // Overriding 1st Error screen caused by created ActivityInformation entries (Auto Select specific Rules)
        if (errorTab.isVisible() && errorTab.getErrorCodesList().contains(ErrorEnum.Errors.ERROR_AAA_10015021_CA_SELECT.getCode())) {
            errorTab.overrideErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER, ErrorEnum.Errors.ERROR_AAA_10015021_CA_SELECT);
            errorTab.override();
            documentsAndBindTab.submitTab();
        }

        // Assert CLUE PU Rule Code and Message
        assertThat(errorTab.getErrorCodesList().contains(ErrorEnum.Errors.ERROR_AAA_validate_pu_clue_claim_2.getCode()));
        assertThat(errorTab.getErrorMessagesList().contains(ErrorEnum.Errors.ERROR_AAA_validate_pu_clue_claim_2.getMessage()));

        // Overriding for TERM (NB) / LIFE (Renewal)
        errorTab.overrideErrors(duration, ErrorEnum.ReasonForOverride.OTHER, ErrorEnum.Errors.ERROR_AAA_validate_pu_clue_claim_2);
        errorTab.override();

        //Submit tab after override
        documentsAndBindTab.submitTab();
    }

    /*
   Method Validates NB quote: Good Driver Discount validation & PU indicator visibility on different Drivers:
   used for pas18303_goodDriverDiscountForPUClaims
	*/
    protected void validateGDDAndPUIndicatorOnNB(TestData td, TestData td2) {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
        driverActivityReportsTab.fillTab(td);

        //Making Sure that correct Policy Type is selected: Select OR Choice
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.fillTab(td);
        // Verify GDD during NB Quote Creation
        validateGDD();

        // Verify that Permissive Use Indicator is not displayed for Non First Named Insured
        validateNonFNIPermissiveUse();

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        policy.getDefaultView().fillFromTo(td2, PremiumAndCoveragesTab.class, DocumentsAndBindTab.class, true);

        // Assert that permissive rule is thrown for CLUE and can be overridden for TERM: PAS-22609
        validateOverridableCLUEPPURule(ErrorEnum.Duration.TERM);

        purchaseTab.fillTab(td2).submitTab();
        policyNumber = labelPolicyNumber.getValue();
        mainApp().close();
    }

    /**
     * Method to validate CAS/Clue Reconcile for AFR driver when PU flag is marked as Yes
     */
    public void pas24587_CASClueReconcilePUAFRUserFlagged() {

        String claim1_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(93).toLocalDate().toString();
        String DL_NAME_RECONCILEFNICLAIMS_DATA_MODEL;

        DL_NAME_RECONCILEFNICLAIMS_DATA_MODEL = "dl_name_reconcileFNIclaims_data_model.yaml";
        Map<String, String> CAS_RESPONSE_DATE_FIELDS = ImmutableMap.of(CLAIM_NUMBER_3, claim1_dates);

        // Create Customer and Policy with one driver
        TestData testDataForFNI;

        //Set correct 'Age First Licensed' to drivers age - ensures product is CA Choice (driving experience is less than 3)
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            String age = String.valueOf(ChronoUnit.YEARS.between(LocalDate.of(1997, Month.OCTOBER, 16), TimeSetterUtil.getInstance().getCurrentTime()));
            testDataForFNI = getTestSpecificTD("TestData_DriverTab_ReconcileFNIclaims_PU")
                    .adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel()), age).resolveLinks();
        } else {
            testDataForFNI = getTestSpecificTD("TestData_DriverTab_ReconcileFNIclaims_PU").resolveLinks();
        }

        adjusted = getPolicyTD().adjust(testDataForFNI);
        policyNumber = openAppAndCreatePolicy(adjusted);
        log.info("Policy created successfully. Policy number is " + policyNumber);

        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedDates(policyNumber, DL_NAME_RECONCILEFNICLAIMS_DATA_MODEL, CAS_RESPONSE_DATE_FIELDS);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy and enter renewal image
        retrieveRenewal(policyNumber);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        tableDriverList.selectRow(1);
        tableActivityInformationList.selectRow(2);
        // Check Driver1 has CAS claims with PU as NO
        assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).getValue().equals("Internal Claims"));
        assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).getValue().equals("No"));
        mainApp().close();

        //Move time to R-35 and run batch jobs:
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));
        //Accept Payment and renew the policy
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(BatchJob.policyStatusUpdateJob);

        //Set test date for endorsement
        TestData addDriverTd = getTestSpecificTD("Add_PU_Claim_Driver_Endorsement_CA");
        //Initiate an endorsement: Add AFR Driver2, calculate premium and order clue
        updatePUFlag = true;
        initiateAddDriverEndorsement(policyNumber, addDriverTd);
        //Navigate to Driver page and verify PU claim moved from FNI to newly added driver2
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        //Check the Driver 2 has CLUE claim
        //Claim numbers are compared and matched ignoring the format differences.
        activityAssertions(2, 2, 1, 1, "CLUE", CAS_CLUE_CLAIM, false);
        //Bind Endorsement
        bindEndorsement();
    }

    /**
     * Method to validate Clue Reconcile for AFR driver when PU flag is marked as Yes
     */
    public void pas24587_ClueReconcilePUAFRUserFlagged() {
        //Create a policy with 2 drivers
        TestData testDataForFNI;

        //Set correct 'Age First Licensed' to drivers age - ensures product is CA Choice (driving experience is less than 3)
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            String age = String.valueOf(ChronoUnit.YEARS.between(LocalDate.of(1997, Month.OCTOBER, 16), TimeSetterUtil.getInstance().getCurrentTime()));
            testDataForFNI = getTestSpecificTD("TestData_DriverTab_ClueReconcileFNIclaims_PU");
            TestData driver1Td = testDataForFNI.getTestDataList("DriverTab").get(0);
            driver1Td.adjust(AutoCaMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel(), age); //set Age First Licensed to the current age always
            TestData driver2Td = testDataForFNI.getTestDataList("DriverTab").get(1); //add adjustments needed for driver2 here in future
            List<TestData> adjustedDrivers = new ArrayList<>();
            adjustedDrivers.add(driver1Td);
            adjustedDrivers.add(driver2Td);
            testDataForFNI = testDataForFNI.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName()), adjustedDrivers).resolveLinks();
        } else {
            testDataForFNI = getTestSpecificTD("TestData_DriverTab_ClueReconcileFNIclaims_PU").resolveLinks();
        }

        adjusted = getPolicyTD().adjust(testDataForFNI).resolveLinks();
        createQuoteAndFillUpTo(adjusted, PremiumAndCoveragesTab.class);
        premiumAndCoveragesTab.submitTab();
        overrideErrorTab();
        policy.getDefaultView().fillFromTo(adjusted, DriverActivityReportsTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        policyNumber = labelPolicyNumber.getValue();
        log.info("Policy created successfully. Policy number is " + policyNumber);
        mainApp().close();

        //Initiate 1st endorsement
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
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
        tableActivityInformationList.selectRow(2);
        assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).getValue().equals("CLUE"));
        assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).getValue().equals("No"));
        //Remove driver2
        tableDriverList.getRow(2).getCell(tableDriverList.getColumnsCount()).controls.links.get("Remove").click();
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REMOVE_DRIVER_DIALOG).getAsset(AutoCaMetaData.DriverTab.SelectDriverDialog.BTN_OK).click();

        bindEndorsement();

        policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(2));

        //Initiate 2nd endorsement
        updatePUFlag = true;
        secondDriverFlag = true;
        TestData addSecondDriverTd = getPolicyTD("Endorsement", "TestData");
        //Add Driver2 again and Order Clue report
        initiateAddDriverEndorsement(policyNumber, addSecondDriverTd);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        //Check driver2 is assigned back with CLUE claim from driver1
        activityAssertions(2, 2, 1, 1, "CLUE", CLUE_CLAIM, false);
    }

    /**
     * Method to validate the violations do not show Permissive Use indicator
     */
    public void pas25463_ViolationsMVRPUIndicatorCheck() {
        //Create a policy with 2 drivers
        TestData testDataForFNI = getTestSpecificTD("TestData_DriverTab_ViolationsMVRFNIclaims_PU").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForFNI);
        createQuoteAndFillUpTo(adjusted, DriverTab.class);
        tableDriverList.selectRow(1);
        activityAssertions(2, 1, 4, 1, "Company Input", "", false); //assert the company input with Type Violations do not show up PU indicator
        activityAssertions(2, 1, 4, 2, "Company Input", "", true); //assert the company input with Type Accident show up PU indicator
        activityAssertions(2, 1, 4, 3, "Customer Input", "", true); //assert the company input with Type  Accident show up PU indicator
        activityAssertions(2, 1, 4, 4, "Customer Input", "", false); //assert the company input with Type Violations do not show up PU indicator
        driverTab.submitTab();
        policy.getDefaultView().fillFromTo(adjusted, MembershipTab.class, PremiumAndCoveragesTab.class, true);
        premiumAndCoveragesTab.submitTab();
        overrideErrorTab();
        new DriverActivityReportsTab().fillTab(adjusted);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        tableDriverList.selectRow(1);
        tableActivityInformationList.selectRow(5);
        //assert that the PU indicator do not show up for MVR claims
        assertThat(activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).getValue().equals("MVR"));
        assertThat(!activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent());

        driverTab.submitTab();
        adjusted = getPolicyTD()
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT.getLabel()))
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV.getLabel()));

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        policy.getDefaultView().fillFromTo(adjusted, PremiumAndCoveragesTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        policyNumber = labelPolicyNumber.getValue();
        log.info("Policy created successfully. Policy number is " + policyNumber);
        mainApp().close();

        //Initiate an endorsement
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        tableDriverList.selectRow(1);
        //asserting the Company/Customer inputs and MVR claims for check the PU indicator
        activityAssertions(2, 1, 5, 1, "Company Input", "", false);
        activityAssertions(2, 1, 5, 2, "Company Input", "", true);
        activityAssertions(2, 1, 5, 3, "Customer Input", "", true);
        activityAssertions(2, 1, 5, 4, "Customer Input", "", false);
        activityAssertions(2, 1, 5, 5, "MVR", "", false);
        driverTab.submitTab();

        bindEndorsement();
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
        File claimResponseFile = batchClaimHelper.processClaimTemplate(response -> {
            setPolicyNumber(policyNumber, response);
            if (claimToDriverLicence != null) {
                updateDriverLicence(claimToDriverLicence, response);
            }
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
                BatchJob.getRenewalClaimOrderAsyncJobParameters().get(BatchJob.ParametersName.IMPORT_FOLDER) + File.separator + claimResponseFile.getName());
    }

    /*
    Method/Test for CA Choice & Select: TestOfflineClaims.pas25162_permissiveUseIndicatorDefaulting
     */
    public void pas25162_permissiveUseIndicatorDefaulting() {
        //Adjusted Test Data for: Internal Claims
        TestData testDataForPUInd = getTestSpecificTD("TestData_PUDefaulting").resolveLinks();
        TestData td = getPolicyTD().adjust(testDataForPUInd);

        policyNumber = openAppAndCreatePolicy(td);
        log.info("Policy created successfully. Policy number is " + policyNumber);

        //Run Jobs to create and issue 1st Renewal: 1st CAS Response
        runRenewalClaimOrderJob();
        createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(policyNumber, PU_CLAIMS_DEFAULTING_DATA_MODEL);
        runRenewalClaimReceiveJob();

        // Retrieve policy and enter renewal image
        retrieveRenewal(policyNumber);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        //1st Renewal: Verify PU Values in Drivers tab
        verifyPUvalues();

        //TODO: Mantas Garsvinskas Uncomment after PAS-26322
        /*
        issueGeneratedRenewalImage(policyNumber, false);

        //Run Jobs to create 2nd required Renewal and validate the results: EXISTING_MATCH case: 2nd CAS Response
        runRenewalClaimOrderJob();
        createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(policyNumber, PU_CLAIMS_DEFAULTING_2ND_DATA_MODEL);
        runRenewalClaimReceiveJob();

        // Retrieve policy and verify claim presence on renewal image
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        if (tableSearchResults.isPresent()) {
            tableSearchResults.getRow("Eff. Date",
                    TimeSetterUtil.getInstance().getCurrentTime().plusDays(46).minusYears(1).format(DateTimeUtils.MM_DD_YYYY))
                    .getCell(1).controls.links.getFirst().click();
        }
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        //2nd Renewal: Verify PU Values in Drivers tab
        verifyPUvalues(); */
    }

    /*
	Method for CA Choice & Select: TestOfflineClaims.PAS-20828 Product Determination Cannot by Influenced by Permissive Use Claims
	 */
    public void pas20828_productDetermineWithPUClaims() {
        TestData testDataForFNI = getTestSpecificTD("TestData_DriverTab_ReconcileFNIclaims_PU").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForFNI);
        String noAgeChange = "";
        String age = String.valueOf(ChronoUnit.YEARS.between(LocalDate.of(1997, Month.OCTOBER, 16), TimeSetterUtil.getInstance().getCurrentTime()));
        String ageMinusFour = Integer.toString(Integer.parseInt(age) - 4);

        createQuoteAndFillUpTo(adjusted, FormsTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        String productDetermined = premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        log.info("product value : " + productDetermined);
        assertThat(productDetermined).isEqualToIgnoringCase("CA Select"); //System determines as Select with no activity

        productDeterminationAssertions(true, false, noAgeChange, "CA Choice"); //System determines as Choice with two At fault accident and PU as No
        productDeterminationAssertions(false, true, noAgeChange, "CA Select"); //Product determination is not impacted with this PU loss (PU is Yes) and keeps as Select
        productDeterminationAssertions(false, true, age, "CA Choice"); //System determines as Choice when driving experience is less than 3 years
        productDeterminationAssertions(false, true, ageMinusFour, "CA Select"); //System determines as Select when driving experience is greater than 3 years
        productDeterminationAssertions(false, false, ageMinusFour, "CA Choice"); //System determines as Choice when activity is not a PU loss (PU is No)
        PremiumAndCoveragesTab.buttonSaveAndExit.click();
    }

    /*
	 Method verifies the Product Determination assertion based on various scenarios
	 */
    private void productDeterminationAssertions(boolean addActivity, boolean permissiveUse, String age, String product) {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        if (addActivity) {
            TestData td_activity = getTestSpecificTD("TestData_Activity");
            new DriverTab().fillTab(td_activity);
        }
        if (permissiveUse) {
            tableActivityInformationList.selectRow(1);
            activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).setValue("Yes");
            tableActivityInformationList.selectRow(2);
            activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).setValue("Yes");
        } else {
            tableActivityInformationList.selectRow(1);
            activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).setValue("No");
            tableActivityInformationList.selectRow(2);
            activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).setValue("No");
        }
        if (!age.isEmpty()) {
            driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.AGE_FIRST_LICENSED).setValue(age);
        }
        driverTab.submitTab();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        String productDetermined = premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        log.info("product value: " + productDetermined);
        assertThat(productDetermined).isEqualToIgnoringCase(product);
    }

    /**
     * @author Chris Johns
     * @author Kiruthika Rajendran
     * PAS-22172 - END - CAS: reconcile permissive use claims when driver/named insured is added (avail for rating)
     * PAS-25271 - DRIVER TAB: make "rel. to first named insured" NOT editable for existing driver
     * @name Test Offline STUB/Mock: reconcile permissive use claims when driver/named insured is added
     * @scenario Test Steps:
     * 1. Create a Policy with 2 names Insured and drivers
     * 2. Add 4 company and customer input claims to first insured: 2 will be Permissive Use = Yes & 2 will be Permissive Use = No
     * 3. Navigate to General Tab and change the FNI to the second Insured
     * 4. Navigate to the Driver Tab and verify the new FNI has acquired the PU claims from the previous FNI
     * 5. Bind the policy and then Initiate an endorsement
     * 6. Change the FNI back to the original insured
     * 7. Navigate to the Driver Tab and verify the new FNI has acquired the PU claims from the previous FNI
     * 8. Complete and bind the endorsement
     * @details Clean Path. Expected Result is that PU claim will be move from the FNI to the newly added driver
     */
    public void pas24652_ChangeFNIGeneralTabNBEndorsement() {
        //Create a policy with 2 drivers
        TestData testDataForFNI = getTestSpecificTD("TestData_Change_FNI_NB_Endorsement_PU_CA").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForFNI);
        createQuoteAndFillUpTo(adjusted, DriverTab.class);

        tableDriverList.selectRow(1);
        //PAS-25271 DRIVER TAB: make "rel. to first named insured" NOT editable for existing driver
        //Verify the Rel to FNI field is not editable for FNI driver
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isEnabled()).isFalse();

        tableDriverList.selectRow(2);
        //Verify the non FNI driver does not show the option of "First Named Insured" in drop down
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isOptionPresent("First Named Insured")).isFalse();

        //Navigate to the General Tab and change the FNI to the second insured (Steve)
        fillContactInfo = true;
        changeFNIGeneralTab(1);  //Index starts at 0

        tableDriverList.selectRow(1);
        //PAS-25271 DRIVER TAB: make "rel. to first named insured" NOT editable for existing driver
        //Verify the Rel to FNI field is not editable for new FNI driver
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isEnabled()).isFalse();

        //Assert that the PU claims have moved to the new FNI (Steve) and has a total of 3 claims now (one existing)
        activityAssertions(2, 1, 3, 2, "Company Input", "", true); //assert the company input with Type Accident show up PU indicator
        activityAssertions(2, 1, 3, 3, "Customer Input", "", true); //assert the company input with Type  Accident show up PU indicator

        tableDriverList.selectRow(2);
        //PAS-25271 Verify the non FNI driver does not show the option of "First Named Insured" in drop down
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isOptionPresent("First Named Insured")).isFalse();

        //Assert that old FNI only has 2 Violation claims
        activityAssertions(2, 2, 2, 1, "Company Input", "", false); //assert the company input with Type Violations do not show up PU indicator
        activityAssertions(2, 2, 2, 2, "Customer Input", "", false); //assert the company input with Type Violations do not show up PU indicator

        //Set 'Rel. to First Named Insured': Other
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Other");
        driverTab.submitTab();

        //Continue policy until Driver Activity Reports tab
        policy.getDefaultView().fillFromTo(adjusted, MembershipTab.class, PremiumAndCoveragesTab.class, true);
        premiumAndCoveragesTab.submitTab();
        overrideErrorTab();

        //Continue to bind the policy and save the policy number
        policy.getDefaultView().fillFromTo(adjusted, DriverActivityReportsTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        policyNumber = labelPolicyNumber.getValue();
        log.info("Policy created successfully. Policy number is " + policyNumber);

        //Initiate an endorsement
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        //Change FNI back to First Insured (Nicolas)
        fillContactInfo = false;
        changeFNIGeneralTab(1);

        //On Driver tab, assert the PU claims all move back to original FNI, Nicolas: 3 Violations, 2 PU claims
        tableDriverList.selectRow(1);
        //PAS-25271 DRIVER TAB: make "rel. to first named insured" NOT editable for existing driver
        //Verify the Rel to FNI field is not editable for new FNI driver
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isEnabled()).isFalse();

        activityAssertions(2, 1, 5, 1, "Company Input", "", false);
        activityAssertions(2, 1, 5, 2, "Customer Input", "", false);
        activityAssertions(2, 1, 5, 3, "MVR", "", false);
        activityAssertions(2, 1, 5, 4, "Company Input", "", true);
        activityAssertions(2, 1, 5, 5, "Customer Input", "", true);

        tableDriverList.selectRow(2);
        //PAS-25271 Verify the non FNI driver does not show the option of "First Named Insured" in drop down
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isOptionPresent("First Named Insured")).isFalse();

        //Verify the other insured only has one claim now
        activityAssertions(2, 2, 1, 1, "Customer Input", "", false);

        //Set 'Rel. to First Named Insured': Other
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Other");

        bindEndorsement();
    }

    /**
     * @author Chris Johns
     * @author Kiruthika Rajendran
     * PAS-22172 - END - CAS: reconcile permissive use claims when driver/named insured is added (avail for rating)
     * PAS-25271 - DRIVER TAB: make "rel. to first named insured" NOT editable for existing driver
     * @name Test Offline STUB/Mock: reconcile permissive use claims when driver/named insured is added
     * @scenario Test Steps:
     * 1. Create a Policy with 2 names Insured and drivers
     * 2. Move time to R-63 and Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 3. Move Time to R-46 and Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 4. Retrieve policy and enter renewal image
     * 5. Verify Claim Data is applied to the FNI
     * 6. Navigate to General Tab and change the FNI to the second Insured
     * 7. Navigate to the Driver Tab and verify the new FNI has acquired the PU claims from the previous FNI
     */
    public void pas24652_ChangeFNIGeneralTabRenewal() {
        // Create Customer and Policy with two named insured' and drivers
        TestData testDataForFNI;
        testDataForFNI = getTestSpecificTD("TestData_Change_FNI_Renewal_PU_CA").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForFNI);
        policyNumber = openAppAndCreatePolicy(adjusted);
        log.info("Policy created successfully. Policy number is " + policyNumber);

        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response - product doesn't matter here, we only need comp and pu claims match
        createCasClaimResponseAndUploadWithUpdatedDL(policyNumber, COMP_DL_PU_CLAIMS_DATA_MODEL_SELECT, CLAIM_TO_DRIVER_LICENSE_SELECT);

        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy and enter renewal image
        retrieveRenewal(policyNumber);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());

        tableDriverList.selectRow(1);
        //PAS-25271 DRIVER TAB: make "rel. to first named insured" NOT editable for existing driver
        //Verify the Rel to FNI field is not editable for new FNI driver
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isEnabled()).isFalse();

        // Check 1st driver: FNI, has the COMP match claim & PU Match Claim. Also Making sure that Claim4: 1002-10-8704-INVALID-dateOfLoss from data model is not displayed
        activityAssertions(2, 1, 2, 1, "Internal Claims", CLAIM_NUMBER_1, true);
        activityAssertions(2, 1, 2, 2, "Internal Claims", CLAIM_NUMBER_3, true);

        tableDriverList.selectRow(2);
        //PAS-25271 Verify the non FNI driver does not show the option of "First Named Insured" in drop down
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isOptionPresent("First Named Insured")).isFalse();

        //Navigate to the General Tab and change the FNI to the second insured (Steve)
        changeFNIGeneralTab(1);  //Index starts at 0

        tableDriverList.selectRow(1);
        //PAS-25271 DRIVER TAB: make "rel. to first named insured" NOT editable for existing driver
        //Verify the Rel to FNI field is not editable for new FNI driver
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isEnabled()).isFalse();

        //Assert that the PU claims have moved to the new FNI (Steve) for a total of 2 claims now (1 existing, 1 PU)
        activityAssertions(2, 1, 2, 1, "Customer Input", "", true);
        activityAssertions(2, 1, 2, 2, "Internal Claims", CLAIM_NUMBER_3, true);

        tableDriverList.selectRow(2);
        //PAS-25271 Verify the non FNI driver does not show the option of "First Named Insured" in drop down
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).isOptionPresent("First Named Insured")).isFalse();

        //Assert that old FNI only has 1 Internal Claims
        activityAssertions(2, 2, 1, 1, "Internal Claims", CLAIM_NUMBER_1, false);

        //Save and exit the Renewal
        DriverTab.buttonSaveAndExit.click();
    }

    public void pas28399_RestrictChangeFNIGeneralTab(String SCENARIO){
        // Create Customer and Policy with three named insured' and two drivers
        adjusted = getPolicyTD().adjust(getTestSpecificTD("TestData_Restrict_FNI_NB_PU_CA").resolveLinks());
        TestData addDriverTd = getTestSpecificTD("Add_NI_Driver_Endorsement_CA");
        //Initiate a quote and fill up to the driver tab
        createQuoteAndFillUpTo(adjusted, DriverTab.class);
        //Navigate back to General tab and change the FNI to Scott (Not a Driver) - Method checks for 28399 Restrict FNI message
        changeFNIGeneralTab(2);  //Index starts at 0
        //Continue to bind the policy
        driverTab.submitTab();
        policy.getDefaultView().fillFromTo(adjusted, MembershipTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        policyNumber = labelPolicyNumber.getValue();
        //ENDORSEMENT or RENEWAL:
        switch (SCENARIO){
            case "ENDORSEMENT":
                //Initiate an endorsement: Try to change FNI again - verify error pop-up
                policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
                //Navigate back to General tab and change the FNI to Scott (Not a Driver) - Method checks for 28399 Restrict FNI message
                changeFNIGeneralTab(2);  //Index starts at 0
                //Add third NI as a driver to resolve pop-up
                NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
                policy.getDefaultView().fill(addDriverTd);
                //change FNI again - verify error pop-up does NOT appear
                changeFNIGeneralTab(2);  //Index starts at 0
                //Continue to bind the endorsement
                tableDriverList.selectRow(1);
                driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Other");
                tableDriverList.selectRow(2);
                driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Other");
                driverTab.submitTab();
//                policy.getDefaultView().fillFromTo(adjusted, MembershipTab.class, PurchaseTab.class, true);
                NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
                premiumAndCoveragesTab.buttonSaveAndExit.click();
                break;
            case "RENEWAL":
                //Run the renewal job and pay the bill
                policyExpirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);
                moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(45));
                //Retrieve policy and enter renewal image: Try to change FNI again - verify error pop-up
                retrieveRenewal(policyNumber);
                //Navigate back to General tab and change the FNI to Scott (Not a Driver) - Method checks for 28399 Restrict FNI message
                changeFNIGeneralTab(2);  //Index starts at 0
                //Add third NI as a driver to resolve pop-up
                NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
                policy.getDefaultView().fill(addDriverTd);
                //Change FNI again - verify error pop-up does NOT appear
                changeFNIGeneralTab(2);  //Index starts at 0
                //Save and Exit the renewal
                GeneralTab.buttonSaveAndExit.click();
                break;
        }
    }

    /**
     * Method for CA Choice & Select: Assert the UW rules are triggered and set the PU flag as Yes in the Driver tab
     */
    private void updateUWPUFlag() {
        int i = 1, j = 4;

        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_CAC7161836_CA_CHOICE);
        } else {
            j = 3;
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_10015021_CA_SELECT);
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_10015015_CA_SELECT);
        }
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_10015023_CA_SELECT_CHOICE);
        errorTab.cancel();

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        tableDriverList.selectRow(1);
        while (i <= j) {
            tableActivityInformationList.selectRow(i);
            activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).setValue("Yes");
            i++;
        }

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();
        premiumAndCoveragesTab.submitTab();
    }

    /**
     * Method/Test for CA Choice & Select: PROD ELIGIBILITY: update uw rule so PU YES claims not counted (10015015 - select) (common code, fix all 4)
     */
    public void pas27908_UpdateUWRulesWithPUFlag() {
        //Create a quote with 2 named insured and one driver and order the reports in DAR page
        TestData testDataForUWrules = getTestSpecificTD("TestData_DriverTab_UpdateUWRules_PU").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForUWrules);
        createQuoteAndFillUpTo(adjusted, DriverActivityReportsTab.class);

        //Add activities to the driver
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        TestData tdActivityUWRules = getTestSpecificTD("TestData_Activity_UWRules");
        driverTab.fillTab(tdActivityUWRules);

        TestData tdActivityUWRulesAdjusted = tdActivityUWRules
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT.getLabel()))
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV.getLabel()));

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        policy.getDefaultView().fillFromTo(tdActivityUWRulesAdjusted, PremiumAndCoveragesTab.class, DocumentsAndBindTab.class, true);
        documentsAndBindTab.submitTab();
        //Assertion to verify the rules are triggered and go back to driver tab to set the PU flag as YES for claims
        updateUWPUFlag();

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab(); // Verified the rules are not triggered and proceed to create a policy
        purchaseTab.fillTab(adjusted).submitTab();
        policyNumber = labelPolicyNumber.getValue();

        //Initiate Endorsement - Add the second driver for the named insured
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        generalTab.viewInsured(2);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Add_Driver2_EndorsementUWRules"), DriverTab.class, true);

        //Change the FNI to second named insured
        fillContactInfo = true;
        changeFNIGeneralTab(1);
        tableDriverList.selectRow(2);
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).setValue("Other");
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();
        premiumAndCoveragesTab.submitTab();
        driverActivityReportsTab.fillTab(getTestSpecificTD("Add_Driver2_EndorsementUWRules"));

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        tableDriverList.selectRow(1);
        driverTab.fillTab(getTestSpecificTD("DriverTab_EndorsementActivity_UWRules"));

        //Bind policy and do assertion to verify the rules are triggered and go back to driver tab to set the PU flag as YES for claims
        bindEndorsement();
        updateUWPUFlag();

        TestData maskedDriverActivityTd = getPolicyTD()
                .adjust(getTestSpecificTD("Add_Driver2_EndorsementUWRules"))
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV.getLabel()));
        driverActivityReportsTab.fillTab(maskedDriverActivityTd);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab(); // Verified the rules are not triggered and proceed to bind the endorsement
    }

    /**
     * @author Saranya Hariharan
     * PAS-27226- CA Mature Driver Discount doesn't work according to rules
     * @name Test Offline STUB/Mock: reconcile permissive use claims when driver/named insured is added
     * @scenario Test Steps:
     * 1. Create Customer.
     * 2. Create CA_Select /Choice Quote.
     * 3. Add 1 Driver who is eligible for MDD.
     * 1. Driver is a rated driver AND
     * 2. Driver is at least 50 years old AND
     * 3. Driver had completed a Mature Driver Improvement Course approved by the California Department of Motor Vehicles within the past 3 years from the effective date of policy AND
     * 4. Driver's License is not revoked or suspended after Mature Driver Course completion
     * OR
     * 5.Driver has no at-fault accidents , Major Violation or Alcohol-Related Violation that occurred after after MD Course completion date
     * 4. Verify MDD gets applied in the Premium and Coverages Discount Section for the Driver who has met the above Criteria.
     * 5. Navigate to Driver Tab and add all possible activities (Customer/Company Input) according with occurrence Date AFTER Mature Driver Discount Course Completion date
     * 6. Navigate to P&C Tab and Calculate Premium and assert that Mature Driver Discount is not applied in the Discount section for the Driver who has Convictions.
     * 7.Continue the steps and Create Policy
     * *********Endorsement Scneario***********
     * 1.Retrieve the policy created and initiate Mid Term Endorsement.
     * 2.Add a New Driver Eligible for MDD
     * 3.Navigate to P&C tab and verify that MDD is applied to the newly added driver.
     * 4.Add activities to the Newly added Driver
     * 5.Verify in P&C Tab Discount section that MDD goes away for the Newly added Driver
     * 6.Continue the steps and Bind the Endorsement.
     */
    public void pas27226_MatureDriverDiscount() {
        TestData testDataForMDD = getTestSpecificTD("TestData_Discounts").resolveLinks();
        TestData td_activity = getTestSpecificTD("TestData_Activity_MDD");
        TestData td_driver_endorse = getTestSpecificTD("TestData_MDD_Endorse");

        //Create policy with Driver who will qualify for the MDD. Fill to P&C Page and verify MDD
        createQuoteAndFillUpTo(testDataForMDD, PremiumAndCoveragesTab.class, false);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Mature Driver Discount (Tom Johns)");

            //Order Reports in the DAR page and then Navigate to Driver and Add Activities to the Second Driver who is Eligible for Mature Driver Discount
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
            driverActivityReportsTab.fillTab(td_activity).submitTab();
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
            tableDriverList.selectRow(2);
            driverTab.fillTab(td_activity);

            //Navigate to the P&C page amd assert that Mature Driver Discount does not exist - Driver is not a Assigned Rated Driver and would not get the discount
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
            softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Mature Driver Discount (Tom Johns)");

            //Calculate Premium and bind the policy
            premiumAndCoveragesTab.fillTab(td_activity).submitTab();
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
            documentsAndBindTab.fillTab(td_activity).submitTab();
            if (errorTab.isVisible()) {
                errorTab.overrideAllErrors();
                errorTab.buttonOverride.click();
                documentsAndBindTab.submitTab();
            }
            purchaseTab.fillTab(td_activity).submitTab();
            String policyNum = labelPolicyNumber.getValue();

            //Begin Endorsement Scenario
            TestData td_activity1 = getTestSpecificTD("TestData_Activity_MDD_Endorse");
            //Add a New Driver in Endorsement
            MDD = true;
            initiateAddDriverEndorsement(policyNum, td_activity1);

            //Add a new Vehicle and assign it to the newly added driver (3rd driver)
            policy.getDefaultView().fillFromTo(td_driver_endorse, MembershipTab.class, AssignmentTab.class, true);
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());
            assignmentTab.fillTab(td_driver_endorse).submitTab();

            //Verify added Driver gets MDD discount and that the existing driver does NOT magically get the MDD during endorsement
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
            softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).contains("Mature Driver Discount (Nike Johns)");
            softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Mature Driver Discount (Tom Johns)");
            premiumAndCoveragesTab.fillTab(td_activity1).submitTab();
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());

            //Navigate to Driver and add Activities/claims to the added driver
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
            tableDriverList.selectRow(3);
            driverTab.fillTab(td_activity1);

            //Assert that MDD does not exist for the newly added driver - activity removed eligibility
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
            softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString()).doesNotContain("Mature Driver Discount (Nike Johns)");
        });
    }
}

