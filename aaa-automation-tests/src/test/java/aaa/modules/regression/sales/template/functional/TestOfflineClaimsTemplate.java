package aaa.modules.regression.sales.template.functional;


import aaa.common.Tab;
import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;
import static aaa.main.pages.summary.PolicySummaryPage.labelPolicyNumber;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.PrivilegeEnum;
import aaa.common.enums.RestRequestMethodTypes;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
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
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.auto_ss.actiontabs.DifferencesActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.AutoSSBaseTest;
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
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;

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
import static aaa.main.enums.ProductConstants.TransactionHistoryType.*;
import static aaa.main.modules.policy.auto_ss.actiontabs.DifferencesActionTab.tableDifferences;
import static aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab.tableActivityInformationList;
import static aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab.tableDriverList;
import static aaa.main.pages.summary.PolicySummaryPage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.contentOf;

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
    private static final String PU_CLAIMS_DEFAULTING_DATA_MODEL = "pu_claims_defaulting_data_model.yaml";
    protected TestData adjusted;
    protected LocalDateTime policyExpirationDate;
    protected String policyNumber;
    protected static final String RENEWAL_MERGE = "RENEWAL_MERGE";
    protected static final String OOSENDORSEMENT = "OOSENDORSEMENT";


    protected static DriverTab driverTab = new DriverTab();
    protected static PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    protected static DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    protected static PasLogGrabber pasLogGrabber = new PasLogGrabber();
    protected static PurchaseTab purchaseTab = new PurchaseTab();
    protected static ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();

    private static final String CLAIM_NUMBER_1 = "1002-10-8702";
    private static final String CLAIM_NUMBER_2 = "1002-10-8703";
    private static final String CLAIM_NUMBER_3 = "1002.10>8704";
    private static final String CLAIM_NUMBER_4 = "1FAZ1111OHS";
    private static final String CLAIM_NUMBER_5 = "4FAZ44444OHS";
    private static final String CLAIM_NUMBER_6 = "1002-10-8705";
    private static final String CAS_CLUE_CLAIM = "1002-10-8704";
    private static final String CAS_CLUE_CLAIM_1 = "1002-10-8709";
    private static final String CLUE_CLAIM = "400001";

    private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE = ImmutableMap.of(CLAIM_NUMBER_1, "A12345222", CLAIM_NUMBER_2, "A12345222");
    private static final String COMP_DL_PU_CLAIMS_DATA_MODEL = "comp_dl_pu_claims_data_model.yaml";
    private static final String NAME_DOB_CLAIMS_DATA_MODEL = "name_dob_claims_data_model.yaml";
    private static final String INC_IN_RATING_3RD_RENEWAL_DATA_MODEL = "inc_in_rating_3rd_renewal_data_model.yaml";
    private static final String INC_RATING_CLAIM_1 = "IIRatingClaim1";
    private static final String INC_RATING_CLAIM_2 = "IIRatingClaim2";
    private static final String INC_RATING_CLAIM_3 = "IIRatingClaim3";
    private static final String INC_RATING_CLAIM_4 = "IIRatingClaim4";


    protected boolean newBusinessFlag = false;
    protected static aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab();
    private static final String[] CLAIM_NUMBERS_PU_DEFAULTING = {"PU_DEFAULTING_CMP","PU_DEFAULTING_1","PU_DEFAULTING_2","PU_DEFAULTING_3",
            "PU_DEFAULTING_4","PU_DEFAULTING_5","PU_DEFAULTING_6"};

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
     *
     * @param policyNumber given policy number
     * @param addDriverTd  specific details for the driver being added to the policy
     */
    public void initiateAddDriverEndorsement(String policyNumber, TestData addDriverTd) {
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        policy.getDefaultView().fill(addDriverTd);

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
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
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();
        ErrorTab errorTab = new ErrorTab();
            if (errorTab.isVisible())
            {
                errorTab.overrideAllErrors();
                errorTab.submitTab();
//                documentsAndBindTab.submitTab();
            }
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
        testData.getTestDataList("DriverTab").forEach(t -> dls.add(t.getValue("License Number")));
        return dls;
    }

    /**
     * Method returns content as String of CAS Request file
     *
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
     *
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
     *
     * @return
     */
    protected String downloadPasAppLog() {
        String pasAppLogFolder = PasLogGrabber.getPasAppLogFolder();
        return downloadPasSelectedLog(pasAppLogFolder);
    }

    /**
     * Method returns content as String of pas-admin wrapper.log file
     *
     * @return
     */
    protected String downloadPasAdminLog() {
        String pasAdminLogFolder = PasLogGrabber.getPasAdminLogFolder();
        return downloadPasSelectedLog(pasAdminLogFolder);
    }

    /**
     * Method returns content as String of combined pas-app & pas-admin log
     *
     * @return
     */
    protected String combinePasAppAndAdminLog() {
        return downloadPasAdminLog() + downloadPasAppLog();
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
     * @param policyNumber      - given Policy Number
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

    //Assert the compare screen for OOS endorsement and Renewal merge scenarios
    public void compareScreen(String scenario) {
        int rowsCount = tableDifferences.getRowsCount();
        int columnsCount = tableDifferences.getColumnsCount();
        log.info("rowsCount: " + rowsCount);
        log.info("columnsCount: " + columnsCount);
        if (tableDifferences.isPresent()) {
            log.info("row1 cell 1: " + tableDifferences.getRow(5).getCell(2).getValue());
            log.info("row2 cell 2: " + tableDifferences.getRow(5).getCell(3).getValue());
            switch (scenario){
                case RENEWAL_MERGE:
                    assertThat(tableDifferences.getRow(5).getCell(2).getValue()).isEqualTo("true");
                    assertThat(tableDifferences.getRow(5).getCell(3).getValue()).isEqualTo("false");
                    break;
                case OOSENDORSEMENT:
                    assertThat(tableDifferences.getRow(5).getCell(2).getValue()).isEqualTo("false");
                    assertThat(tableDifferences.getRow(5).getCell(3).getValue()).isEqualTo("true");
                    break;
            }
        }
    }

    private void createRenewalVersion() {
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        tableDriverList.selectRow(1);
        tableActivityInformationList.selectRow(1);
        activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).setValue("Yes");
        driverTab.submitTab();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();
        premiumAndCoveragesTab.submitTab();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();
    }

    private void OOSEndorsement(TestData endorsementTD, boolean PUcheck) {
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.endorse().performAndFill(endorsementTD);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        //log.info("Updating first driver with PU as yes");
        tableDriverList.selectRow(1);
        tableActivityInformationList.selectRow(1);
        //log.info("Current PU value" + activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).getValue());
        if (PUcheck) {
            activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).setValue("Yes");
        } else {
            activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).setValue("No");
        }
        driverTab.submitTab();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();
        premiumAndCoveragesTab.submitTab();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();
    }

    private void activityAssertions(int totalDrivers, int driverRowNo, int totalActivities, int activityRowNo, String activitySource, String claimNumber, boolean checkPU, String includeIR) {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(tableDriverList).hasRows(totalDrivers);
            tableDriverList.selectRow(driverRowNo);
            softly.assertThat(tableActivityInformationList).hasRows(totalActivities);
            tableActivityInformationList.selectRow(activityRowNo);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue(activitySource);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(claimNumber);
            if (checkPU) {
                softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isEnabled());
            } else {
                //For SS Auto PU Indicator should not be Present
                softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isPresent()).isFalse();
            }
            switch (includeIR) {
                case "Yes":
                    softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER)).hasValue("Yes");
                case "No":
                    softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER)).hasValue("No");
                case "NA":
                    break;
            }
        });
    }

    // Assertions for COMP and DL Tests
    public void compDLPuAssertions(String COMP_MATCH, String DL_MATCH, String PU_MATCH) {
        // Check 1st driver: Contains only Two Matched Claims (Verifying that PermissiveUse Claim with wrong dateOfLoss is not displayed)
        // Check 1st driver: FNI, has COMP and Permissive Use matched claims (2nd PermissiveUse Claim is not displayed, because of dateOfLoss Param > Claim dateOfLoss)
        activityAssertions(4, 1, 2, 1, "Internal Claims", COMP_MATCH, true, "NA");
        activityAssertions(4, 1, 2, 2, "Internal Claims", PU_MATCH, true, "NA");
        // Check 2nd driver: Has DL match claim
        activityAssertions(4, 2, 1, 1, "Internal Claims", DL_MATCH, false, "NA");
    }

    // Assertions for COMP and DL Tests: PAS-22172
    public void puDropAssertions(String COMP_MATCH, String PU_MATCH) {
        // Check 1st driver: Contains only one Matched Claim (Verifying that comp claim has not moved)
        activityAssertions(5, 1, 1, 1, "Internal Claims", COMP_MATCH, true, "NA");
        // Check 5th driver: Original Permissive Use claim should be on the newly added driver
        activityAssertions(5, 5, 1, 1, "CLUE", PU_MATCH, false, "NA");
    }

    // Assertions for Name/DOB Tests
    public void nameDobYobAssertions(String LASTNAME_FIRSTNAME_DOB, String LASTNAME_FIRSTNAME, String LASTNAME_FIRSTINITAL_DOB, String LASTNAME_FIRSTNAME_YOB) {
        String noClaimNumber = "";
        // Check 3rd driver
        //Check the CLUE claim
        activityAssertions(4, 3, 4, 1, "CLUE", CLUE_CLAIM, false, "NA");
        // PAS-8310 - LASTNAME_FIRSTNAME_DOB Match
        activityAssertions(4, 3, 4, 2, "Internal Claims", LASTNAME_FIRSTNAME_DOB, false, "NA");
        // PAS-17894 - LASTNAME_FIRSTNAME & LASTNAME_FIRSTINITAL_DOB //PAS-21435 - Removed LASTNAME_YOB match logic. Claim 8FAZ88888OHS is now unmatched
        activityAssertions(4, 3, 4, 3, "Internal Claims", LASTNAME_FIRSTNAME, false, "NA");
        activityAssertions(4, 3, 4, 4, "Internal Claims", LASTNAME_FIRSTINITAL_DOB, false, "NA");

        // Check 4th driver.
        //Check Company Input claim
        activityAssertions(4, 4, 3, 1, "Company Input", noClaimNumber, false, "NA");
        //Check the CLUE claim
        activityAssertions(4, 4, 3, 2, "CLUE", CAS_CLUE_CLAIM_1, false, "NA");
        // PAS-8310 - LASTNAME_FIRSTNAME_YOB Match
        activityAssertions(4, 4, 3, 3, "Internal Claims", LASTNAME_FIRSTNAME_YOB, false, "NA");
    }

    private void selectTransactionType(int rowIndex, boolean isSelected) {
        tableTransactionHistory.getRow(rowIndex).getCell(1).controls.checkBoxes.get(1).setValue(isSelected);
    }

    private void verifyTransactionHistoryType(int rowIndex, String type) {
        assertThat(tableTransactionHistory.getRow(rowIndex).getCell(2).getValue()).as("Transaction type should be %1$s.", type).isEqualTo(type);
    }

    //Verify Comp, PU and DL match more logic via automated renewal
    protected void pas14679_CompDLPUMatchMore() {
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
        // Propose the renewal version
        compDLPuAssertions(CLAIM_NUMBER_1, CLAIM_NUMBER_2, CLAIM_NUMBER_3);
        driverTab.submitTab();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();
        premiumAndCoveragesTab.submitTab();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();

        //Run the renewal job and pay the bill
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));

        createRenewalVersion();
        //Do a roll on changes on renewal version
        buttonRenewalQuoteVersion.click();
        selectTransactionType(2, true);
        PolicyActions.buttonRollOnChanges.click();
        DifferencesActionTab differencesActionTab = new DifferencesActionTab();
        differencesActionTab.applyDifferences(true);
        //Compare the two renewal versions and assert the Permissive Use indicator
        verifyTransactionHistoryType(1, RENEWAL);
        verifyTransactionHistoryType(2, RENEWAL);
        verifyTransactionHistoryType(3, RENEWAL);

        selectTransactionType(1, true);
        selectTransactionType(3, true);
        buttonCompare.click();
        compareScreen(RENEWAL_MERGE);
        Tab.buttonCancel.click();

        buttonQuoteOverview.click();
        // Propose the renewal version
        buttonRenewals.click();
        policy.dataGather().start();
        premiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();
        mainApp().close();

        //Accept Payment and renew the policy
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);

        //Scenario to check the user does not have privilege to edit the PU indicator for CAS claims in endorsement
        //Login with different user. Check the PU indicator is not editable for internal claims other than E34/L41
        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.F35);
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(!activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS).isEnabled());
        });
    }

    //Verify Comp, PU and DL match more logic via manual renewal
    protected void pas14679_CompDLPUMatchMoreManual() {
        // Create Customer and Policy with 4 drivers
        createPolicyMultiDrivers();

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedDL(policyNumber, COMP_DL_PU_CLAIMS_DATA_MODEL, CLAIM_TO_DRIVER_LICENSE);

        // Retrieve policy and generate a manual renewal image
        createManualRenewal();

        //Run Claims receive batch job, to assign claims
        JobUtils.executeJob(Jobs.renewalClaimReceiveAsyncJob);

        //Move time by one day to get claims to show in the UI
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));

        //Enter renewal image and verify claim presence
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        // Check 1st driver: FNI, has the COMP match claim & PU Match Claim. Also Making sure that Claim4: 1002-10-8704-INVALID-dateOfLoss from data model is not displayed
        // Check 2nd driver: Has DL match claim
        compDLPuAssertions(CLAIM_NUMBER_1, CLAIM_NUMBER_2, CLAIM_NUMBER_3);
        DocumentsAndBindTab.buttonSaveAndExit.click();

        policyExpirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);
        //Run the renewal job and pay the bill
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));

        //Accept Payment and renew the policy
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);

        //Initiate and bind the OOS Endorsement
        TestData endorsementTD1 = getTestSpecificTD("TestData_Plus90Days");
        OOSEndorsement(endorsementTD1, true);

        //Initiate and bind the back dated endorsement
        TestData endorsementTD2 = getTestSpecificTD("TestData_Plus30Days");
        OOSEndorsement(endorsementTD2, false);

        //Do a roll on changes on OOS endorsement
        policy.rollOn().openConflictPage(false);
        DifferencesActionTab differencesActionTab = new DifferencesActionTab();
        differencesActionTab.applyDifferences(true);

        //Compare the Endorsement and assert the PU indicator
        buttonTransactionHistory.click();
        verifyTransactionHistoryType(1, ROLLED_ON_ENORSEMENT);
        verifyTransactionHistoryType(2, OOS_ENDORSEMENT);
        verifyTransactionHistoryType(3, BACKED_OFF_ENDORSEMENT);
        verifyTransactionHistoryType(4, ANNIVERSARY_RENEWAL);
        verifyTransactionHistoryType(5, ISSUE);

        selectTransactionType(1, true);
        selectTransactionType(3, true);
        buttonCompareVersions.click();
        compareScreen(OOSENDORSEMENT);
    }


    //Verify match more claims to satisfy the Name and DOB match logic LASTNAME_FIRSTNAME_DOB,  LASTNAME_FIRSTNAME_YOB via automated renewal
    protected void pas8310_nameDOBYOBMatchMore() {
        createPolicyMultiDrivers();        // Create Customer and Policy with 4 drivers
        runRenewalClaimOrderJob();        // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(policyNumber, NAME_DOB_CLAIMS_DATA_MODEL);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy and verify claim presence on renewal image
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        //Verify LASTNAME_FIRSTNAME_DOB, LASTNAME_FIRSTNAME, LASTNAME_FIRSTINITAL_DOB, LASTNAME_FIRSTNAME_YOB matches
        //Verify  LASTNAME_YOB claim does NOT match
        nameDobYobAssertions(CLAIM_NUMBER_3, CLAIM_NUMBER_4, CLAIM_NUMBER_5, CLAIM_NUMBER_6);
    }

    //Verify match more claims to satisfy the Name and DOB match logic LASTNAME_FIRSTNAME_DOB,  LASTNAME_FIRSTNAME_YOB via manual renewal
    protected void pas8310_nameDobYobMatchMoreManual() {
        // Create Customer and Policy with 4 drivers
        createPolicyMultiDrivers();

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(policyNumber, NAME_DOB_CLAIMS_DATA_MODEL);

        // Retrieve policy and generate a manual renewal image
        createManualRenewal();

        //Run Claims receive batch job, to assign claims
        JobUtils.executeJob(Jobs.renewalClaimReceiveAsyncJob);

        //Move time by one day to get claims to show in the UI
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));

        //Enter renewal image and verify claim presence
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        //Verify LASTNAME_FIRSTNAME_DOB, LASTNAME_FIRSTNAME, LASTNAME_FIRSTINITAL_DOB, LASTNAME_FIRSTNAME_YOB matches
        //Verify  LASTNAME_YOB claim does NOT match
        nameDobYobAssertions(CLAIM_NUMBER_3, CLAIM_NUMBER_4, CLAIM_NUMBER_5, CLAIM_NUMBER_6);
    }

    //Test reconcile permissive use claims when driver/named insured is added and compare of CLUE claim from newly added driver to existing PU Yes claim on FNI
    protected void pas22172_ReconcilePUEndorsementAFRD() {
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
        TestData addDriverTd = getTestSpecificTD("Add_PU_Claim_Driver_Endorsement_AZ");
        //Initiate an endorsement: Add AFR Driver, calculate premium and order clue
        initiateAddDriverEndorsement(policyNumber, addDriverTd);

        //Navigate to Driver page and verify PU claim moved from FNI to newly added driver
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        puDropAssertions(CLAIM_NUMBER_1, CAS_CLUE_CLAIM);

        //Bind Endorsement
        bindEndorsement();
    }

    //Verify PU indicator for Company/Customer, MVR and CLUE claims for drivers in NB and Endorsement
    protected void pas22608_verifyPermissiveUseIndicator() {
        TestData testDataForFNI = getTestSpecificTD("TestData_DriverTab_PUFlagCheck").resolveLinks();
        adjusted = getPolicyTD().adjust(testDataForFNI);
        createQuoteAndFillUpTo(adjusted, DriverTab.class);
        tableDriverList.selectRow(1);
        //Assertions to verify PU Indicator does not show up for any Company/Customer type of Activity .
        activityAssertions(2, 1, 3, 1, "Company Input", "", false, "NA");
        activityAssertions(2, 1, 3, 2, "Company Input", "", false, "NA");
        activityAssertions(2, 1, 3, 3, "Customer Input", "", false, "NA");
        driverTab.submitTab();
        policy.getDefaultView().fillFromTo(adjusted, RatingDetailReportsTab.class, DriverActivityReportsTab.class, true);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        //Assertions to verify PU Indicator does not show up for MVR claim for FNI driver
        activityAssertions(2, 1, 4, 4, "MVR", "", false, "NA");
        //Assertions to verify PU Indicator does not show up for CLUE claim for non FNI driver
        activityAssertions(2, 2, 1, 1, "CLUE", CLUE_CLAIM, false, "NA");

        driverTab.submitTab();
        adjusted = getPolicyTD().mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoSSMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE.getLabel()));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        policy.getDefaultView().fillFromTo(adjusted, PremiumAndCoveragesTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        policyNumber = getPolicyNumber();
        log.info("Policy created successfully. Policy number is " + policyNumber);

        //Initiate an endorsement
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        //Driver2 clue claim is reassigned to driver1
        tableDriverList.selectRow(2);
        tableActivityInformationList.selectRow(1);
        tableActivityInformationList.getRow(1).getCell(tableActivityInformationList.getColumnsCount()).controls.links.get("Reassign").click();
        activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.SELECT_DRIVER_DIALOG).getAsset(AutoSSMetaData.DriverTab.SelectDriverDialog.ASSIGN_TO).setValueByIndex(1);
        activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.SELECT_DRIVER_DIALOG).getAsset(AutoSSMetaData.DriverTab.SelectDriverDialog.BTN_OK).click();

        //asserting the Company/Customer inputs and MVR claims for check the PU indicator for FNI driver
        activityAssertions(2, 1, 5, 1, "Company Input", "", false, "NA");
        activityAssertions(2, 1, 5, 2, "Company Input", "", false, "NA");
        activityAssertions(2, 1, 5, 3, "Customer Input", "", false, "NA");
        activityAssertions(2, 1, 5, 4, "MVR", "", false, "NA");

        //Assertions to verify PU Indicator does not show up for CLUE claim for FNI driver
        activityAssertions(2, 1, 5, 5, "CLUE", CLUE_CLAIM, false, "NA");
        driverTab.submitTab();
        bindEndorsement();
    }

    //Test Claims 'Include In Rating' determination according to Occurrence date
    protected void pas14552_includeClaimsInRatingDetermination() {
        // Claim Dates: claimDateOfLoss/claimOpenDate/claimCloseDate all are the same
        String claim1_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(1).toLocalDate().toString();
        String claim2_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).toLocalDate().toString();
        String claim3_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(3).toLocalDate().toString();

        Map<String, String> UPDATE_CAS_RESPONSE_DATE_FIELDS =
                ImmutableMap.of(INC_RATING_CLAIM_1, claim1_dates, INC_RATING_CLAIM_2, claim2_dates, INC_RATING_CLAIM_3, claim3_dates, INC_RATING_CLAIM_4, claim3_dates);

        TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(driverTab.getMetaKey(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "A19191911").resolveLinks();

        // Create Customer and Policy
        openAppAndCreatePolicy(testData);
        String policyNumber = labelPolicyNumber.getValue();
        mainApp().close();

        //Run Jobs to create and issue 2nd Renewal
        casRenewal(2);

        //Run Jobs to create 3rd required Renewal and validate the results
        runRenewalClaimOrderJob();

        // Create Updated CAS Response and Upload
        createCasClaimResponseAndUploadWithUpdatedDates(policyNumber, INC_IN_RATING_3RD_RENEWAL_DATA_MODEL, UPDATE_CAS_RESPONSE_DATE_FIELDS);
        runRenewalClaimReceiveJob();

        // Retrieve policy and verify claim presence on renewal image
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        //Select the Active Policy row
        if (tableSearchResults.isPresent()) {
            tableSearchResults.getRow("Eff. Date",
                    TimeSetterUtil.getInstance().getCurrentTime().plusDays(46).minusYears(1).format(DateTimeUtils.MM_DD_YYYY).toString())
                    .getCell(1).controls.links.getFirst().click();
        }

        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        // Check that Policy Contains 3 Claims
        assertThat(tableActivityInformationList.getAllRowsCount()).isEqualTo(4);

        // PAS14552 - Assert that Claim IS NOT Included In Rating because Date of Loss is older than two terms
        activityAssertions(1,1,4,1,"Internal Claims", INC_RATING_CLAIM_1,false, "No");
        // PAS14552 - Assert that Claim IS Included In Rating because Date of Loss is equal to two terms eff. date
        activityAssertions(1,1,4,2,"Internal Claims", INC_RATING_CLAIM_2,false, "Yes");
        //PAS-22026 - Agent Override scenario
        activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER.getLabel(), RadioGroup.class).setValue("Yes");
        // PAS14552 - Assert that Claim IS Included In Rating because Date of Loss is equal to current system date
        activityAssertions(1,1,4,3,"Internal Claims", INC_RATING_CLAIM_3,false, "Yes");
        // PAS-18300 - Assert that Permissive Use Claim IS Included In Rating because Date of Loss is equal to current system date and assigned to FNI - !!Claim will get Same Day Waiver after premium Calc
        activityAssertions(1,1,4,4,"Internal Claims", INC_RATING_CLAIM_4,false, "Yes");

        ////////////////////////////////////////////////////////////////////////////////////

        // PAS-22026 - Override claim 2 to be not included in rating
        tableActivityInformationList.selectRow(2);
        activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER.getLabel(), RadioGroup.class).setValue("No");

        //Issue 4th Renewal
        issueGeneratedRenewalImage(policyNumber);

        //Run Jobs to create create 5th renewal
        casRenewal(1);

        //Run Jobs to create and create a 6th renewal
        runRenewalClaimOrderJob();
        //Generate new CAS file for existing claims
        createCasClaimResponseAndUploadWithUpdatedDates(policyNumber, INC_IN_RATING_3RD_RENEWAL_DATA_MODEL, UPDATE_CAS_RESPONSE_DATE_FIELDS);
        runRenewalClaimReceiveJob();

        // Retrieve policy and verify claim presence on renewal image
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        if (tableSearchResults.isPresent()) {
            tableSearchResults.getRow("Eff. Date",
                    TimeSetterUtil.getInstance().getCurrentTime().plusDays(46).minusYears(1).format(DateTimeUtils.MM_DD_YYYY).toString())
                    .getCell(1).controls.links.getFirst().click();
        }

        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        // PAS_22026 - Claim 1 Include in Rating = NO (Did not pass last age check)
        activityAssertions(1,1,4,1,"Internal Claims", INC_RATING_CLAIM_1,false, "No");
        // PAS_22026 - Claim 2 Include in Rating = NO (Maintain Agent Override)
        activityAssertions(1,1,4,2,"Internal Claims", INC_RATING_CLAIM_2,false, "No");
        // PAS_22026 - Claim 3 Include in Rating = YES (Passed last Age check, still within 60 month charge window)
        activityAssertions(1,1,4,3,"Internal Claims", INC_RATING_CLAIM_3,false, "Yes");
        // PAS_22026 - Claim 4 Include in Rating = NO (Maintain Same Day Waiver from Claim 3)
        activityAssertions(1,1,4,4,"Internal Claims", INC_RATING_CLAIM_4,false, "No");

//            switch (scenario){
//                case "Chargeability":
//                    //PAS-22026 - Continue to age off claim 2
//                    issueGeneratedRenewalImage(policyNumber);
//                    casRenewal(3);
//
//                    // Retrieve policy and verify claim presence on renewal image
//                    mainApp().open();
//                    SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
//
//                    if (tableSearchResults.isPresent()) {
//                        tableSearchResults.getRow("Eff. Date",
//                                TimeSetterUtil.getInstance().getCurrentTime().plusDays(46).minusYears(1).format(DateTimeUtils.MM_DD_YYYY).toString())
//                                .getCell(1).controls.links.getFirst().click();
//                    }
//
//                    buttonRenewals.click();
//                    policy.dataGather().start();
//                    NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
//
//                    // PAS-22026 - Assert that claim 3 Included in Rating = NO (Outside of 60 month charge window)
//                    tableActivityInformationList.selectRow(3);
//                    softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(INC_RATING_CLAIM_3);
//                    softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("1500");
//                    softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER)).hasValue("No");
//                    break;
//            }
            ////////////////////////////////////////////////////////////////////////////////////

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
        tableDriverList.selectRow(1);
        activityAssertions(2, 1, 3, 1, "MVR", "", false, "NA");
        activityAssertions(2, 1, 3, 2, "Internal Claims", CLAIM_NUMBER_1, false, "NA");
        activityAssertions(2, 1, 3, 3, "Internal Claims", CLAIM_NUMBER_3, false, "NA");
        //Navigate to the General Tab and change the FNI to the second insured (Steve)
        changeFNIGeneralTab(1);  //Index starts at 0
        //Assert that the PU claims have moved to the new FNI (Steve) for a total of 2 claims now (1 existing, 1 PU)
        tableDriverList.selectRow(1);
        activityAssertions(2,1,2, 1, "Customer Input", "", false, "NA");
        activityAssertions(2,1, 2, 2, "Internal Claims", CLAIM_NUMBER_3, false, "NA");
        //Assert that old FNI  has 1 Internal Claims and 1 existing MVR claim
        tableDriverList.selectRow(2);
        activityAssertions(2, 2, 2, 2, "Internal Claims", CLAIM_NUMBER_1, false, "NA");
        //Save and exit the Renewal
        DriverTab.buttonSaveAndExit.click();
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

    /*
   Method verifies that PU indicator has correct defaulted values:
   used for pas25162_permissiveUseIndicatorDefaulting
    */
    protected void verifyPUvalues() {
        CustomSoftAssertions.assertSoftly(softly -> {
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();

            // Check 1st driver: Contains 7 Matched Claims (Verifying PU default value)
            softly.assertThat(driverTab.tableActivityInformationList).hasRows(7);

            // Verifying PU default value for all Claims
            for (int i = 0; i <= 6; i++) {
                driverTab.tableActivityInformationList.selectRow(i + 1);
                if (i == 6) { //PERMISSIVE_USE match = Yes
                    softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBERS_PU_DEFAULTING[i]);
                   // softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS)).hasValue("Yes");
                } else {
                    softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBERS_PU_DEFAULTING[i]);
                    //PU Indicator will default to No
                   // softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS)).hasValue("No");
                }

            }
        });
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

    /**
     * Method renews policies with claims batch runs: order batch at r-63, claim receive batch at r-46, renewal pt1 and pt2 at r-35, makes payment, and runs policyStatusUpdate batch at R
     * @param renewalAmount
     */
    public void casRenewal(int renewalAmount) {
        int x = 0;
        while (x < renewalAmount) {
            runRenewalClaimOrderJob();
            runRenewalClaimReceiveJob();
            issueGeneratedRenewalImage(policyNumber);
            x++;
        }
    }


}
