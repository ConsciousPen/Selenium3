package aaa.modules.regression.sales.auto_ss.functional;

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

import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.claim.datamodel.claim.Claim;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import toolkit.verification.CustomAssertions;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.contentOf;

public class TestOffLineClaims extends AutoSSBaseTest {

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

	private static final String CLAIM_NUMBER_1 = "1002-10-8702";

	private static final String CLAIM_NUMBER_2 = "1002-10-8703";

	private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE =
			ImmutableMap.of(CLAIM_NUMBER_1, "A12345222", CLAIM_NUMBER_2, "A12345222");
	public static final String TWO_CLAIMS_DATA_MODEL = "two_claims_data_model.yaml";

	@BeforeTest
	public void prepare() {
		try {
			FileUtils.forceDeleteOnExit(Paths.get(CAS_REQUEST_PATH).toFile());
			FileUtils.forceDeleteOnExit(Paths.get(CAS_RESPONSE_PATH).toFile());
			Files.createDirectories(Paths.get(CAS_REQUEST_PATH));
			Files.createDirectories(Paths.get(CAS_RESPONSE_PATH));
		} catch (IOException e) {
			throw new IllegalStateException("Can't delete directories " + CAS_RESPONSE_PATH + " " + CAS_REQUEST_PATH, e);
		}
	}

	/**
	 *  @author Andrii Syniagin
	 *  @name Test generation cas claim reponse.
	 */
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@SuppressWarnings("SpellCheckingInspection")
    public void testCreateCasResponse() {
		BatchClaimHelper batchClaimHelper = new BatchClaimHelper("two_claims_data_model.yaml",
				"claim_resp.xml");
		String policyNumber = "AZSS999999999";
        File claimResponse = batchClaimHelper.processClaimTemplate((response) ->
				setPolicyNumber(policyNumber, response));
		assertThat(claimResponse).exists().isFile();
		assertThat(Assertions.contentOf(claimResponse)).contains(policyNumber);
    }

	/**
     * * @author Chris Johns
     * @name Test Offline STUB/Mock Data Claims
     * @IMPORTANT: This test is written under the current stub structure and is subject to change
     * @scenario
     * Test Steps:
     * 1. Create a Policy with 3 drivers; 1 with no STUB data match, 2, and 3 with STUB data match
     * 2. Move time to R-63
     * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 4. Run Claims Offline Batch Job
     * 5. Move Time to R-46
     * 6. Run Renewal Part2 + "claimsRenewBatchRecieveJob"
     * 7. Retrieve policy and enter renewal image
     * 8. Verify Claim Data is applied to the correct driver.
     * @details Clean Path. Expected Result is that claims data is applied to the correct driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
    public void PAS14679_TestCase1(@Optional("AZ") String state) throws IOException {
	    PurchaseTab purchaseTab = new PurchaseTab();
	    TestData testData = getPolicyTD();
	    TestData driverTabTestData = getTestSpecificTD("TestData_DriverTab_OfflineClaim").resolveLinks();

	    // Create Customer and Policy with 3 drivers
	    mainApp().open();
	    createCustomerIndividual();
	    policy.initiate();
	    policy.getDefaultView().fillUpTo(testData, DriverTab.class, true);
	    policy.getDefaultView().fill(driverTabTestData);

	    // Fill remaining Policy info and bind
	    policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, PurchaseTab.class, true);
	    purchaseTab.submitTab();

	    // Gather Policy details: Policy Number and expiration date
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
	    LocalDateTime policyExpirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);
	    mainApp().close();

	    // Move to R-63, run batch job part 1 and offline claims batch job
	    TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(63));
		LocalDateTime updatedTime = TimeSetterUtil.getInstance().getCurrentTime();
		assertThat(updatedTime).isEqualToIgnoringHours(policyExpirationDate.minusDays(63));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalClaimOrderAsyncJob);
		String claimRequestFolder = Jobs.getClaimOrderJobFolder();

		// Download the claim request
		List<String> requests = RemoteHelper.get().getListOfFiles(claimRequestFolder);
		assertThat(requests).hasSize(1);
		String claimRequest = requests.get(0);
		RemoteHelper.get().downloadFile(claimRequest, CAS_REQUEST_PATH);
		File claimRequestFile = new File(CAS_REQUEST_PATH + File.separator + claimRequest);
		assertThat(claimRequestFile).exists().isFile().canRead().isAbsolute();

		// Check request it it contains DL and PolicyNumber
		List<String> driverLicenseList = getDriverLicences(testData, driverTabTestData);
		String content = contentOf(claimRequestFile, Charset.defaultCharset());
		assertThat(content)
				.contains("ClaimBatchRequest")
				.contains(policyNumber)
				.endsWith("ClaimBatchRequest>");
		driverLicenseList.forEach(l -> assertThat(content).contains(l));

		// Create the claim response
		String currentTime = TimeSetterUtil.getInstance().getCurrentTime()
				.truncatedTo(ChronoUnit.SECONDS).format(DATE_TIME_FORMATTER);
		String casResponseFileName = getCasResponseFileName(currentTime);
		BatchClaimHelper batchClaimHelper = new BatchClaimHelper(TWO_CLAIMS_DATA_MODEL,
				casResponseFileName);
		File claimResponseFile = batchClaimHelper.processClaimTemplate((response) -> {
			setPolicyNumber(policyNumber, response);
			updateDriverLicence(CLAIM_TO_DRIVER_LICENSE, response);
		});

        // Upload claim response
		RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(),
				Jobs.getClaimReceiveJobFolder() + File.separator + claimResponseFile.getName());

        // Move to R-46 and run batch job part 2 and offline claims receive batch job
	    TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(46));
	    JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        JobUtils.executeJob(Jobs.renewalClaimReceiveAsyncJob);

        // Retrieve policy
	    mainApp().open();
	    SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

	    // Enter renewal image and verify claim presence
	    PolicySummaryPage.buttonRenewals.click();
	    policy.dataGather().start();
	    NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		DriverTab driverTab = new DriverTab();
		ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
		CustomAssertions.assertThat(DriverTab.tableDriverList).hasRows(3);

		// Check 1st driver. No Claims
		CustomAssertions.assertThat(DriverTab.tableActivityInformationList).hasRows(0);

		// Check 2nd driver. 2 Claim.
		DriverTab.tableDriverList.selectRow(2);
		CustomAssertions.assertThat(DriverTab.tableActivityInformationList).hasRows(2);
		CustomAssertions.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
		CustomAssertions.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_1);
		DriverTab.tableActivityInformationList.selectRow(2);
		CustomAssertions.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
		CustomAssertions.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_2);

		// Check 3rd driver. No Claims
		DriverTab.tableDriverList.selectRow(3);
		CustomAssertions.assertThat(DriverTab.tableActivityInformationList).isPresent(false);
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

	private void setPolicyNumber(String policyNumber, CASClaimResponse response) {
		response.getClaimLineItemList().forEach(claimLineItem -> {
			claimLineItem.setAgreementNumber(policyNumber);
			claimLineItem.getClaimList().forEach(claim -> claim.setClaimPolicyReferenceNumber(policyNumber));
		});
	}

	private static String getCasResponseFileName(String prefix) {
		return String.format(CAS_RESPONSE_FILE_NAME_TEMPLATE, CAS_RESPONSE_PATH + File.separator + prefix);
	}

	private List<String> getDriverLicences(@Nonnull TestData testData, @Nonnull TestData driverTab) {
		List<String> dls = new ArrayList<>();
		dls.add(testData.getTestData("DriverTab").getValue("License Number"));
    	driverTab.getTestDataList("DriverTab").forEach(t -> dls.add(t.getValue("License Number")));
    	return dls;
	}
}


