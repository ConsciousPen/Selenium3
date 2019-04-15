package aaa.modules.regression.sales.template.functional;
import static aaa.common.pages.SearchPage.tableSearchResults;
import aaa.common.pages.Page;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import static aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab.tableActivityInformationList;
import static aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab.tableDriverList;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import aaa.main.pages.summary.PolicySummaryPage;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.RestRequestMethodTypes;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.ClaimAnalyticsJSONTags;
import aaa.helpers.claim.ClaimCASResponseTags;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.claim.datamodel.claim.Claim;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.logs.PasLogGrabber;
import aaa.helpers.rest.JsonClient;
import aaa.helpers.rest.RestRequestInfo;
import aaa.helpers.rest.dtoClaim.ClaimsAssignmentResponse;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.ComboBox;

/**
 * This template is used to test Batch Claim Logic.
 *
 * @author Andrii Syniagin
 */
public class TestOfflineClaimsTemplate extends AutoSSBaseTest {

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
    private static final String PAS_LOG_DOWNLOAD_PATH = System.getProperty("user.dir")
            + PropertyProvider.getProperty("test.downloadfiles.location") + "downloaded_pas_log";
    public static final String SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = 'TRUE' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'PermissiveUse'";
    public static final String SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS = "UPDATE LOOKUPVALUE SET DATEOFLOSS = '%s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'PermissiveUse'";
    private static final String CLAIMS_URL = "https://claims-assignment-master.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1"; //Post-Permissive Use
    public static final String SQL_REMOVE_RENEWALCLAIMRECEIVEASYNCJOB_BATCH_JOB_CONTROL_ENTRY = "DELETE FROM BATCH_JOB_CONTROL_ENTRY WHERE jobname='renewalClaimReceiveAsyncJob'";
    public static final String CLAIMS_MICROSERVICE_ENDPOINT = "select * from PROPERTYCONFIGURERENTITY where propertyname = 'aaaClaimsMicroService.microServiceUrl'";

    protected TestData adjusted;
    protected LocalDateTime policyExpirationDate;
    protected String policyNumber;

    protected static DriverTab driverTab = new DriverTab();
    protected static PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    protected static DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    protected static PasLogGrabber pasLogGrabber = new PasLogGrabber();
    protected static PurchaseTab purchaseTab = new PurchaseTab();
    protected static ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
    protected boolean newBusinessFlag = false;
    protected static aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab();
    private static final String CLAIM_NUMBER_1 = "1002-10-8702";
    private static final String CLAIM_NUMBER_2 = "1002-10-8703";
    private static final String CLAIM_NUMBER_3 = "1002.10>8704";
    private static final String COMP_DL_PU_CLAIMS_DATA_MODEL = "comp_dl_pu_claims_data_model.yaml";
    private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE = ImmutableMap.of(CLAIM_NUMBER_1, "D07963714", CLAIM_NUMBER_2, "D07963714");
    @BeforeTest
    public void prepare() {
        // Toggle ON PermissiveUse Logic & Set DATEOFLOSS Parameter in DB
        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-NOV-16"));

        try {
            FileUtils.forceDeleteOnExit(Paths.get(CAS_REQUEST_PATH).toFile());
            FileUtils.forceDeleteOnExit(Paths.get(CAS_RESPONSE_PATH).toFile());
            FileUtils.forceDeleteOnExit(Paths.get(PAS_LOG_DOWNLOAD_PATH).toFile());
            Files.createDirectories(Paths.get(CAS_REQUEST_PATH));
            Files.createDirectories(Paths.get(CAS_RESPONSE_PATH));
            Files.createDirectories(Paths.get(PAS_LOG_DOWNLOAD_PATH));
        } catch (IOException e) {
            throw new IllegalStateException("Can't delete directories " + CAS_RESPONSE_PATH + " "
                    + CAS_REQUEST_PATH + " " + PAS_LOG_DOWNLOAD_PATH, e);
        }
    }

    public String createPolicyMultiDrivers() {
        TestData testData = getPolicyTD();
        List<TestData> testDataDriverData = new ArrayList<>();// Merged driver tab with 4 drivers
        testDataDriverData.add(testData.getTestData("DriverTab"));
        testDataDriverData.addAll(getTestSpecificTD("TestData_DriverTab_OfflineClaim").resolveLinks().getTestDataList("DriverTab"));
        adjusted = testData.adjust("DriverTab", testDataDriverData);

        // Create Customer and Policy with 4 drivers
        mainApp().open();
        createCustomerIndividual();
        policy.createPolicy(adjusted);

        policyNumber = labelPolicyNumber.getValue();

        mainApp().close();
        return policyNumber;
    }

    // Retrieve policy, generate a manual renwal image, save and exit the app
    public void createManualRenewal() {
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        policy.renew().start();
        GeneralTab.buttonSaveAndExit.click();
        mainApp().close();
    }

    /**
     * Initiates an endorsement, calculates premium, orders CLUE report for newly added driver
     * @param policyNumber given policy number
     * @param addDriverTd specific details for the driver being added to the policy
     */
    public void initiateAddDriverEndorsement(String policyNumber, TestData addDriverTd) {
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        policy.getDefaultView().fill(addDriverTd);

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();
        premiumAndCoveragesTab.submitTab();

        //Modify default test data to mask unnecessary steps
        TestData td = getPolicyTD()
                .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoSSMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE.getLabel()));
        new DriverActivityReportsTab().fillTab(td);
    }

    /**
     * Binds current endorsement: calculates premium, navigates to bind page, and binds endorsement
     */
    public void bindEndorsement() {
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
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

    // Assertions for COMP and DL Tests
    public void compDLPuAssertions(String COMP_MATCH, String DL_MATCH, String PU_MATCH) {
        CustomSoftAssertions.assertSoftly(softly -> {
            DriverTab driverTab = new DriverTab();
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
            softly.assertThat(tableDriverList).hasRows(4);

            // Check 1st driver: Contains only Two Matched Claims (Verifying that PermissiveUse Claim with wrong dateOfLoss is not displayed)
            softly.assertThat(tableActivityInformationList).hasRows(2);

            // Check 1st driver: FNI, has COMP and Permissive Use matched claims (2nd PermissiveUse Claim is not displayed, because of dateOfLoss Param > Claim dateOfLoss)
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(COMP_MATCH);

            tableActivityInformationList.selectRow(2);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(PU_MATCH);

            // Check 2nd driver: Has DL match claim
            tableDriverList.selectRow(2);
            softly.assertThat(tableActivityInformationList).hasRows(1);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(DL_MATCH);
        });
    }

    // Assertions for COMP and DL Tests: PAS-22172
    public void puDropAssertions(String COMP_MATCH, String PU_MATCH) {
        CustomSoftAssertions.assertSoftly(softly -> {
            DriverTab driverTab = new DriverTab();
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
            softly.assertThat(tableDriverList).hasRows(5);

            // Check 1st driver: Contains only one Matched Claim (Verifying that comp claim has not moved)
            tableDriverList.selectRow(1);
            softly.assertThat(tableActivityInformationList).hasRows(1);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(COMP_MATCH);

            // Check 5th driver: Original Permissive Use claim should be on the newly added driver
            tableDriverList.selectRow(5);
            softly.assertThat(tableActivityInformationList).hasRows(1);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("CLUE");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(PU_MATCH);
        });
    }

    // Assertions for Name/DOB Tests
    public void nameDobYobAssertions(String LASTNAME_FIRSTNAME_DOB, String LASTNAME_FIRSTNAME, String LASTNAME_FIRSTINITAL_DOB, String LASTNAME_FIRSTNAME_YOB) {
        CustomSoftAssertions.assertSoftly(softly -> {
            DriverTab driverTab = new DriverTab();
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
            softly.assertThat(tableDriverList).hasRows(4);

            // Check 3rd driver
            // PAS-8310 - LASTNAME_FIRSTNAME_DOB Match
            tableDriverList.selectRow(3);
            tableActivityInformationList.selectRow(2);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(LASTNAME_FIRSTNAME_DOB);
            // PAS-17894 - LASTNAME_FIRSTNAME & LASTNAME_FIRSTINITAL_DOB //PAS-21435 - Removed LASTNAME_YOB match logic. Claim 8FAZ88888OHS is now unmatched
            tableActivityInformationList.selectRow(3);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(LASTNAME_FIRSTNAME);
            tableActivityInformationList.selectRow(4);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(LASTNAME_FIRSTINITAL_DOB);

            // Check 4th driver.
            // PAS-8310 - LASTNAME_FIRSTNAME_YOB Match
            tableDriverList.selectRow(4);
            tableActivityInformationList.selectRow(3);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(LASTNAME_FIRSTNAME_YOB);
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
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();
        payTotalAmtDue(policyNumber);
    }

    /**
     * Method updates CAS Response XML with given Driver Licence according to Claim Number
     * @param claimToDriverLicenseMap given Driver Licence Number according to Claim Number
     * @param response CAS Response
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
     * @param updatableDateFieldValueMap given value according to Claim Number
     * @param response CAS Response
     * @param updatableDateField given XML Tag name
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
        testData.getTestDataList("DriverTab").forEach(t -> dls.add(t.getValue("License Number")));
        return dls;
    }

    /**
     * Method returns content as String of CAS Request file
     * @return
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
     * Method returns content as String of pas-app wrapper.log file
     * @return
     */
    protected String downloadPasSelectedLog(String requiredLogFolder) {
        String pasSelectedLogFolder = requiredLogFolder;
        RemoteHelper.get().getListOfFiles(pasSelectedLogFolder);
        RemoteHelper.get().downloadFile("wrapper.log", PAS_LOG_DOWNLOAD_PATH);
        File downloadedPasLogFile = new File(PAS_LOG_DOWNLOAD_PATH + File.separator + "wrapper.log");
        assertThat(downloadedPasLogFile).exists().isFile().canRead().isAbsolute();
        String content = contentOf(downloadedPasLogFile, Charset.defaultCharset());
        log.info("Downloaded Selected PAS Log File: {}" + content);
        return content;
    }

    /**
     * Method returns content as String of pas-app wrapper.log file
     * @return
     */
    protected String downloadPasAppLog() {
        String pasAppLogFolder = PasLogGrabber.getPasAppLogFolder();
        return downloadPasSelectedLog(pasAppLogFolder);
    }

    /**
     * Method returns content as String of pas-admin wrapper.log file
     * @return
     */
    protected String downloadPasAdminLog() {
        String pasAdminLogFolder = PasLogGrabber.getPasAdminLogFolder();
        return downloadPasSelectedLog(pasAdminLogFolder);
    }

    /**
     * Method returns content as String of combined pas-app & pas-admin log
     * @return
     */
    protected String combinePasAppAndAdminLog() {
        return downloadPasAdminLog() + downloadPasAppLog();
    }

    /**
     Method goes though all Claim Analytics items and returns required value according to claimNumber and policyNumber
     *
     * @param listOfClaims list Of Claim JSONs as strings;
     * @param claimNumber given claim number
     * @param policyNumber given policy number
     * @param key key of value which you want to get
     */
    protected String retrieveClaimValueFromAnalytics(List<String> listOfClaims, String claimNumber, String policyNumber, String key) {
        String claimValue = null;

        for (int i = 0; i <= listOfClaims.size() - 1; i++) {
            JSONObject specificClaimData = new JSONObject(listOfClaims.get(i)).getJSONObject("claims-assignment");
            if (specificClaimData.getString(ClaimAnalyticsJSONTags.TagNames.CLAIM_NUMBER).equals(claimNumber) && specificClaimData
                    .getString(ClaimAnalyticsJSONTags.TagNames.POLICY_NUMBER).equals(policyNumber)) {
                claimValue = specificClaimData.get(key).toString();
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
     * @param policyNumber given Policy Number
     * @param dataModelFileName given CAS Response data model
     * @param claimToDriverLicence if != null, given Driver Licence according to Claim Number
     * @param claimDatesToUpdate if != null, given Claim Dates according to Claim Number
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
     * @param policyNumber - given Policy Number
     * @param dataModelFileName given CAS Response data model
     */
    public void createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(String policyNumber, String dataModelFileName) {
        createCasClaimResponseAndUpload(policyNumber, dataModelFileName, null, null);
    }

    /**
     * Method creates CAS Response file and Uploads to required folder: With Updated Policy Number & Driver License
     *
     * @param policyNumber given policy number
     * @param dataModelFileName given CAS Response data model
     * @param claimToDriverLicence given Driver License according to Claim Number
     */
    public void createCasClaimResponseAndUploadWithUpdatedDL(String policyNumber, String dataModelFileName,
            Map<String, String> claimToDriverLicence) {
        createCasClaimResponseAndUpload(policyNumber, dataModelFileName, claimToDriverLicence, null);
    }

    /**
     * Method creates CAS Response file and Uploads to required folder: With Updated Policy Number & Claim Dates: Date Of Loss, Close Date, Open Date
     *
     * @param policyNumber given Policy Number
     * @param dataModelFileName given CAS Response data model
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
        while (y < microServiceResponse.getMatchedClaims().size())
        {
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
        log.info("expected match codes: "+expectedMatchCodes);
        log.info("actual match codes: "+actualMatchCodes);
        assertThat(actualMatchCodes).isEqualTo(expectedMatchCodes);
    }
    protected void pas18317_verifyPermissiveUseIndicator() {
        TestData testData = getPolicyTD();
        List<TestData> testDataDriverData = new ArrayList<>();// Merged driver tab with 4 drivers
        testDataDriverData.add(testData.getTestData("DriverTab"));
        testDataDriverData.addAll(getTestSpecificTD("TestData_DriverTab_OfflineClaim_AZ").resolveLinks().getTestDataList("DriverTab"));
        adjusted = testData.adjust("DriverTab", testDataDriverData).resolveLinks();
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(adjusted,DriverTab.class, true);
        //Assert to check the PU indicator for company input in quote level
         CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Company Input");
            //PAS-18317: PU indicator will NOT show for NON FNI drivers
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent()).isFalse();
        });
        driverTab.submitTab();
        policy.getDefaultView().fillFromTo(adjusted, RatingDetailReportsTab.class,DriverActivityReportsTab.class,true);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().fillTab(adjusted);
        documentsAndBindTab.submitTab();
        new PurchaseTab().fillTab(adjusted).submitTab();
        policyNumber = PolicySummaryPage.getPolicyNumber();
        log.info("Policy created successfully. Policy number is " + policyNumber);
        mainApp().close();
       //Initiate endorsement
        TestData addDriverTd = getTestSpecificTD("Add_PU_Claim_Driver_Endorsement_AZ");
        initiateAddDriverEndorsement(policyNumber, addDriverTd);
       //Navigate to Driver page and verify the clue claim is added to driver5
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        puIndicatorAssertions();       // Assert to check PU indicator check for clue claims in endorsement
        bindEndorsement();
    }
    // Assertions for clue claims  Tests
    //PAS-18317: PU indicator will NOT show for NON FNI drivers
    public void puIndicatorAssertions() {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(tableDriverList).hasRows(5);
            softly.assertThat(tableActivityInformationList).hasRows(1);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("CLUE");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent()).isFalse();
        });
    }
    public void pas25463_ViolationsMVRPUIndicatorCheck(){
        TestData testDataForFNI = getTestSpecificTD("TestData_DriverTab_ViolationsMVRFNIclaims_PU").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForFNI);
        createQuoteAndFillUpTo(adjusted, DriverTab.class);
        tableDriverList.selectRow(1);
       //Assertions to verify PU Indicator does not show up for any type of Activity .
        activityAssertions(2,1,4, 1, "Company Input", "", false);
        activityAssertions(2,1,4, 2, "Company Input", "", false);
        activityAssertions(2,1,4, 3, "Customer Input", "", false);
        activityAssertions(2,1,4, 4, "Customer Input", "", false);
        driverTab.submitTab();
        policy.getDefaultView().fillFromTo(adjusted, RatingDetailReportsTab.class, DriverActivityReportsTab.class,true);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        tableDriverList.selectRow(1);
        tableActivityInformationList.selectRow(5);
        assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).getValue().equals("MVR"));
        assertThat(!activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent());
        driverTab.submitTab();
        adjusted = getPolicyTD().mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoSSMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE.getLabel()));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        policy.getDefaultView().fillFromTo(adjusted, PremiumAndCoveragesTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        policyNumber = PolicySummaryPage.getPolicyNumber();
        log.info("Policy created successfully. Policy number is " + policyNumber);
        //Initiate an endorsement
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
       tableDriverList.selectRow(1);
       //asserting the Company/Customer inputs and MVR claims for check the PU indicator
        activityAssertions(2,1,5, 1, "Company Input", "", false);
        activityAssertions(2,1,5, 2, "Company Input", "", false);
        activityAssertions(2,1,5, 3, "Customer Input", "", false);
        activityAssertions(2,1,5, 4, "Customer Input", "", false);
        activityAssertions(2,1,5, 5, "MVR", "", false);
        driverTab.submitTab();
        bindEndorsement();
    }
    private void activityAssertions(int totalDrivers, int driverRowNo, int totalActivities, int activityRowNo, String activitySource, String claimNumber, boolean checkPU) {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(tableDriverList).hasRows(totalDrivers);
            tableDriverList.selectRow(driverRowNo);
            softly.assertThat(tableActivityInformationList).hasRows(totalActivities);
            tableActivityInformationList.selectRow(activityRowNo);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue(activitySource);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(claimNumber);
            if (checkPU) {
                softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isEnabled());
            }
            else {
                //For SS Auto PU Indicator should not be Present
                softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent()).isFalse();
            }
        });
    }
    /**
     * @author Chris Johns
     * @author Saranya Hariharan
     * PAS-22172 - END - CAS: reconcile permissive use claims when driver/named insured is added (avail for rating)
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
    public void pas24652_ChangeFNIGeneralTabRenewal(){
        // Create Customer and Policy with two named insured' and drivers
        adjusted = getPolicyTD().adjust(getTestSpecificTD("TestData_Change_FNI_Renewal_PU_AZ").resolveLinks());
        policyNumber = openAppAndCreatePolicy(adjusted);
        log.info("Policy created successfully. Policy number is " + policyNumber);
        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it
        // Create the claim response - product doesn't matter here, we only need comp and pu claims match
        createCasClaimResponseAndUploadWithUpdatedDL(policyNumber, COMP_DL_PU_CLAIMS_DATA_MODEL, CLAIM_TO_DRIVER_LICENSE );
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job
        // Retrieve policy and enter renewal image
        retrieveRenewal(policyNumber);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        // Check 1st driver: FNI, has the MVR ,COMP match claim & PU Match Claim. Also Making sure that Claim4: 1002-10-8704-INVALID-dateOfLoss from data model is not displayed
        //TODO: PU Indicator Shows up in UI for Internal claims after PAS-22608 is merged to Master Branch. Assertions needs to be modified.
        tableDriverList.selectRow(1);
        activityAssertions(2, 1, 3, 1, "MVR", "", false);
        activityAssertions(2, 1, 3, 2, "Internal Claims", CLAIM_NUMBER_1, false);
        activityAssertions(2, 1, 3, 3, "Internal Claims", CLAIM_NUMBER_3, false);
        //Navigate to the General Tab and change the FNI to the second insured (Steve)
        changeFNIGeneralTab(1);  //Index starts at 0
        //Assert that the PU claims have moved to the new FNI (Steve) for a total of 2 claims now (1 existing, 1 PU)
        tableDriverList.selectRow(1);
        activityAssertions(2,1,2, 1, "Customer Input", "", false);
        activityAssertions(2,1, 2, 2, "Internal Claims", CLAIM_NUMBER_3, false);
        //Assert that old FNI  has 1 Internal Claims and 1 existing MVR claim
        tableDriverList.selectRow(2);
        activityAssertions(2, 2, 2, 2, "Internal Claims", CLAIM_NUMBER_1, false);
        //Save and exit the Renewal
        DriverTab.buttonSaveAndExit.click();
    }
    /**
     * Method changes'First Named Insured' to the desired Insured. First Named Insured index starts at zero
     * @param namedInsuredNumber - Insured who will become the First Named Insured
     */
    public void changeFNIGeneralTab(int namedInsuredNumber) {
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel(), ComboBox.class).setValueByIndex(namedInsuredNumber);
        Page.dialogConfirmation.confirm();
        //Reset Contact Info - blanks out after FNI change at New Business
        if (newBusinessFlag) {
            generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.HOME_PHONE_NUMBER).setValue("6025557777");
            generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.PREFERED_PHONE_NUMBER).setValue("Home Phone");
        }
        generalTab.submitTab();
    }

    /**
     * Method opens app, retrieves policy, and enters data gathering in renewal image
     * @param policyNumber
     */
    public void retrieveRenewal(String policyNumber) {
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        buttonRenewals.click();
        policy.dataGather().start();
    }
}
