package aaa.modules.regression.sales.template.functional;

import static aaa.main.pages.summary.PolicySummaryPage.labelPolicyNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.contentOf;
import java.io.File;
import java.io.IOException;
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
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.PasAdminLogGrabber;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.claim.datamodel.claim.Claim;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;

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


    protected TestData adjusted;
    protected LocalDateTime policyExpirationDate;
    protected String policyNumber;

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
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        JobUtils.executeJob(Jobs.renewalClaimReceiveAsyncJob);
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
     Method returns pas-admins wrapper.log as String
     */
    protected String downloadPasAdminLog() {
        String pasAdminLogFolder = PasAdminLogGrabber.getPasAdminLogFolder();
        List<String> adminLogFiles = RemoteHelper.get().getListOfFiles(pasAdminLogFolder);
        RemoteHelper.get().downloadFile("wrapper.log", PAS_ADMIN_LOG_PATH);
        File pasAdminLogFile = new File(PAS_ADMIN_LOG_PATH + File.separator + "wrapper.log");
        assertThat(pasAdminLogFile).exists().isFile().canRead().isAbsolute();
        String content = contentOf(pasAdminLogFile, Charset.defaultCharset());
        log.info("Downloaded PAS Admin Log File: {}" + content);
        return content;
    }

    /**
     Method goes though all Claim Analytics items and returns required value according to claimNumber
     *
     * @param listOfClaims list Of Claim JSONs as strings;
     * @param claimNumber given claim number
     * @param key key value which you want to get
     */
    protected String retrieveClaimValueFromAnalytics(List<String> listOfClaims, String claimNumber, String key){
        String claimValue = null;

        for (int i=0;i <= listOfClaims.size()-1; i++){
            JSONObject specificClaimData = new JSONObject(listOfClaims.get(i)).getJSONObject("claims-assignment");
            if (specificClaimData.getString("claimNumber").equals(claimNumber)) {
                claimValue = specificClaimData.getString(key);
            } else {
                log.info("Moving to the next Claim List Item.. Required Claim in this Claim Analytics JSON Item couldn't be found. Claim Number: "
                        +claimNumber+" Moving to the next one..");
            }
        }
        return claimValue;
    }

    protected void createCasClaimResponseAndUpload(String policyNumber, String dataModelFileName,
                                                   Map<String, String> claimToDriverLicence) {
        // Create Cas response file
        String casResponseFileName = getCasResponseFileName();
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper(dataModelFileName, casResponseFileName);
        File claimResponseFile = batchClaimHelper.processClaimTemplate((response) -> {
            setPolicyNumber(policyNumber, response);
            if (claimToDriverLicence != null) updateDriverLicence(claimToDriverLicence, response);
        });
        String content = contentOf(claimResponseFile, Charset.defaultCharset());
        log.info("Generated CAS claim response filename {} content {}", casResponseFileName, content);

        // Upload claim response
        RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(),
                Jobs.getClaimReceiveJobFolder() + File.separator + claimResponseFile.getName());
    }
}
