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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.claim.datamodel.claim.Claim;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.logs.PasAdminLogGrabber;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
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
    private static final String PAS_ADMIN_LOG_PATH = System.getProperty("user.dir")
            + PropertyProvider.getProperty("test.downloadfiles.location") + "pas_admin_log";
    public static final String SQL_UPDATE_MATCHMORECLAIMS_DISPLAYVALUE = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = 'TRUE' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'MatchMoreClaims'";
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

    //TODO:gunxgar improve method to be able to pass specifc test data
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
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalClaimOrderAsyncJob);
    }

    // Assertions for Name/DOB Tests
    public void nameDobYobAssertions(String CLAIM_NUMBER_3, String CLAIM_NUMBER_4, String CLAIM_NUMBER_5, String CLAIM_NUMBER_6 ) {
        CustomSoftAssertions.assertSoftly(softly -> {
            DriverTab driverTab = new DriverTab();
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
            softly.assertThat(DriverTab.tableDriverList).hasRows(4);

            // Check 3rd driver
            // PAS-8310 - LASTNAME_FIRSTNAME_DOB Match
            DriverTab.tableDriverList.selectRow(3);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_3);
            // PAS-17894 - LASTNAME_FIRSTNAME & LASTNAME_FIRSTINITAL_DOB //PAS-21435 - Removed LASTNAME_YOB match logic. Claim 8FAZ88888OHS is now unmatched
            DriverTab.tableActivityInformationList.selectRow(2);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_4);
            DriverTab.tableActivityInformationList.selectRow(3);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_5);

            // Check 4th driver.
            // PAS-8310 - LASTNAME_FIRSTNAME_YOB Match
            DriverTab.tableDriverList.selectRow(4);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_6);
        });
    }

    public void generateClaimRequest() {
        // Download the claim request
        File claimRequestFile = downloadClaimRequest();

        //PAS-2467 -  Check if request contains DL and PolicyNumber. Should NOT contain DL
        List<String> driverLicenseList = getDriverLicences(adjusted);
        String content = contentOf(claimRequestFile, Charset.defaultCharset());
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

    /*
    Method changes current date to policy expiration date and issues generated renewal image
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
     * @param updatableFieldValueMap given value according to Claim Number
     * @param response
     * @param updatableField given XML Tag name
     */
    protected void updateFieldForClaim(Map<String, String> updatableFieldValueMap, CASClaimResponse response, String updatableField) {
        List<Claim> claims = response.getClaimLineItemList().stream()
                .flatMap(claimLineItem -> claimLineItem.getClaimList().stream())
                .collect(Collectors.toList());
        claims.forEach(c -> {
            String updatableFieldValue = updatableFieldValueMap.get(c.getClaimNumber());
            if (updatableFieldValue != null) {
                try {
                    Field field = Claim.class.getDeclaredField(updatableField);
                    field.setAccessible(true);
                    field.set(c, updatableFieldValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
	                throw new IllegalStateException("Can't update field " + updatableField + " by " + updatableFieldValue, e);
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

    protected File downloadClaimRequest() {
        String claimRequestFolder = Jobs.getClaimOrderJobFolder();
        List<String> requests = RemoteHelper.get().getListOfFiles(claimRequestFolder);
        assertThat(requests).hasSize(1);
        String claimRequest = requests.get(0);
        RemoteHelper.get().downloadFile(claimRequest, CAS_REQUEST_PATH);
        File claimRequestFile = new File(CAS_REQUEST_PATH + File.separator + claimRequest);
        assertThat(claimRequestFile).exists().isFile().canRead().isAbsolute();
        String content = contentOf(claimRequestFile, Charset.defaultCharset());
        log.info("Downloaded CAS claim request: {}" + content);
        return claimRequestFile;
    }

    /**
     Method returns pas-admins wrapper.log as String TODO:gunxgar combine download methods and move to common classes to be able to use downloadFILE from any method.
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

    //TODO:gunxgar refactor is needed following method and its uses. More info in PAS14552_includeClaimsInRatingDetermination, now duplicates
    protected void createCasClaimResponseAndUpload(String policyNumber, String dataModelFileName,
            Map<String, String> claimToDriverLicence) {
        // Create Cas response file
        String casResponseFileName = getCasResponseFileName();
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper(dataModelFileName, casResponseFileName);
        File claimResponseFile = batchClaimHelper.processClaimTemplate((response) -> {
            setPolicyNumber(policyNumber, response);
            if (claimToDriverLicence != null)
                updateDriverLicence(claimToDriverLicence, response);
        });
        String content = contentOf(claimResponseFile, Charset.defaultCharset());
        log.info("Generated CAS claim response filename {} content {}", casResponseFileName, content);

        // Upload claim response
        RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(),
                Jobs.getClaimReceiveJobFolder() + File.separator + claimResponseFile.getName());
    }

    protected void uploadCasResponseFile(File claimResponseFile, String casResponseFileName) {
        String content = contentOf(claimResponseFile, Charset.defaultCharset());
        log.info("Generated CAS claim response filename {} content {}", casResponseFileName, content);

        // Upload claim response
        RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(),
                Jobs.getClaimReceiveJobFolder() + File.separator + claimResponseFile.getName());
    }
}
