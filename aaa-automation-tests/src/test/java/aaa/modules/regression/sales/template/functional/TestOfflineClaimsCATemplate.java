package aaa.modules.regression.sales.template.functional;

import static aaa.common.pages.SearchPage.tableSearchResults;
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
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.RestRequestMethodTypes;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.ClaimCASResponseTags;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.claim.datamodel.claim.Claim;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.logs.PasAdminLogGrabber;
import aaa.helpers.rest.JsonClient;
import aaa.helpers.rest.RestRequestInfo;
import aaa.helpers.rest.dtoClaim.ClaimsAssignmentResponse;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;

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
    protected String policyNumber;

    protected static DriverTab driverTab = new DriverTab();
    protected static PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    protected static DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

    @BeforeTest
    public void prepare() {
        try {
            FileUtils.forceDeleteOnExit(Paths.get(CAS_REQUEST_PATH).toFile());
            FileUtils.forceDeleteOnExit(Paths.get(CAS_RESPONSE_PATH).toFile());
            Files.createDirectories(Paths.get(CAS_REQUEST_PATH));
            Files.createDirectories(Paths.get(CAS_RESPONSE_PATH));
        } catch (IOException e) {
            throw new IllegalStateException("Can't delete directories " + CAS_RESPONSE_PATH + " "
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

    // Move to R-63, run batch job part 1 and offline claims batch job
    public void runRenewalClaimOrderJob() {
        policyExpirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(63));
        LocalDateTime updatedTime = TimeSetterUtil.getInstance().getCurrentTime();
        assertThat(updatedTime).isEqualToIgnoringHours(policyExpirationDate.minusDays(63));
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
        JobUtils.executeJob(BatchJob.renewalClaimOrderAsyncJob);
    }

    // Assertions for COMP and DL Tests
    public void compDLPuAssertions(String COMP_MATCH, String DL_MATCH, String PU_MATCH) {
        CustomSoftAssertions.assertSoftly(softly -> {
            DriverTab driverTab = new DriverTab();
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
            softly.assertThat(DriverTab.tableDriverList).hasRows(4);

            // Check 1st driver: Contains only Two Matched Claims (Verifying that PermissiveUse Claim with wrong dateOfLoss is not displayed)
            softly.assertThat(DriverTab.tableActivityInformationList).hasRows(2);

            // Check 1st driver: FNI, has COMP and Permissive Use matched claims (2nd PermissiveUse Claim is not displayed, because of dateOfLoss Param > Claim dateOfLoss)
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(COMP_MATCH);

	        DriverTab.tableActivityInformationList.selectRow(2);
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(PU_MATCH);

            // Check 2nd driver: Has DL match claim
            DriverTab.tableDriverList.selectRow(2);
            softly.assertThat(DriverTab.tableActivityInformationList).hasRows(1);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(DL_MATCH);
        });
    }

    // Assertions for Name/DOB Tests
    public void nameDobYobAssertions(String LASTNAME_FIRSTNAME_DOB, String LASTNAME_FIRSTNAME, String LASTNAME_FIRSTINITAL_DOB, String LASTNAME_FIRSTNAME_YOB ) {
        CustomSoftAssertions.assertSoftly(softly -> {
            DriverTab driverTab = new DriverTab();
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
            softly.assertThat(DriverTab.tableDriverList).hasRows(4);

            // Check 3rd driver
            // PAS-8310 - LASTNAME_FIRSTNAME_DOB Match
            DriverTab.tableDriverList.selectRow(3);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(LASTNAME_FIRSTNAME_DOB);
            // PAS-17894 - LASTNAME_FIRSTNAME & LASTNAME_FIRSTINITAL_DOB //PAS-21435 - Removed LASTNAME_YOB match logic. Claim 8FAZ88888OHS is now unmatched
            DriverTab.tableActivityInformationList.selectRow(2);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(LASTNAME_FIRSTNAME);
            DriverTab.tableActivityInformationList.selectRow(3);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(LASTNAME_FIRSTINITAL_DOB);

            // Check 4th driver.
            // PAS-8310 - LASTNAME_FIRSTNAME_YOB Match
            DriverTab.tableDriverList.selectRow(4);
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
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
        JobUtils.executeJob(BatchJob.renewalClaimReceiveAsyncJob);
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
        testData.getTestDataList("DriverTab").forEach(t -> dls.add(t.getValue("License #")));
        return dls;
    }

    /**
     * Method returns content as String of CAS Request file
     * @return
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

    /**
     * Method returns content as String of pas-admins wrapper.log file
     * @return
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
        RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(),//todo doublecheck
                BatchJob.getRenewalClaimOrderAsyncJobParameters().get(BatchJob.ParametersName.IMPORT_FOLDER) + File.separator + claimResponseFile.getName());
    }

    /**
     * Method creates CAS Response file and Uploads to required folder: With Updated Policy Number only
     *
     * @param policyNumber - given Policy Number
     * @param dataModelFileName given CAS Response data model
     */
    public void createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(String policyNumber, String dataModelFileName){
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
            Map<String, String> claimToDriverLicence){
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
            Map<String, String> claimDatesToUpdate){
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
        while (x < microServiceResponse.getUnmatchedClaims().size())
        {
            String claimNumber = microServiceResponse.getUnmatchedClaims().get(x).getClaimNumber();
            actualUnmatchedClaims.add(claimNumber);
            x++;
        }

        //Verify the actual UNMATCHED claims equal the expected UNMATCHED claims
        //PAS-21435 - Removed LASTNAME_YOB match logic. These claims will now be unmatched
        log.info("expected: "+expectedUnmatchedClaims);
        log.info("actual: "+actualUnmatchedClaims);
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


}
