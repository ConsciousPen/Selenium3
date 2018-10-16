package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.contentOf;
import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

@StateList(states = {Constants.States.AZ})
public class TestOffLineClaims extends TestOfflineClaimsTemplate {

	private static final String CLAIM_NUMBER_1 = "1002-10-8702";

	private static final String CLAIM_NUMBER_2 = "1002-10-8703";

	private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE =
			ImmutableMap.of(CLAIM_NUMBER_1, "A12345222", CLAIM_NUMBER_2, "A12345222");

	private static final String TWO_CLAIMS_DATA_MODEL = "two_claims_data_model.yaml";

	/**
	 *  @author Andrii Syniagin
	 *  @name Test generation cas claim reponse.
	 */
	@SuppressWarnings("SpellCheckingInspection")
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@Parameters({"state"})
	public void testCreateCasResponse(@Optional("AZ") @SuppressWarnings("unused") String state) {
		BatchClaimHelper batchClaimHelper = new BatchClaimHelper(TWO_CLAIMS_DATA_MODEL, getCasResponseFileName());
		String policyNumber = "AZSS999999999";
		File claimResponse = batchClaimHelper.processClaimTemplate((response) ->
				setPolicyNumber(policyNumber, response));
		assertThat(claimResponse).exists().isFile();
		assertThat(Assertions.contentOf(claimResponse)).contains(policyNumber);
	}

	/**
	 * @author Chris Johns
	 * @author Andrii Syniagin
	 * @name Test Offline STUB/Mock Data Claims
	 * @IMPORTANT: This test is written under the current stub structure and is subject to change
	 * @scenario
	 * Test Steps:
	 * 1. Create a Policy with 3 drivers; 1 with no STUB data match, 2, and 3 with STUB data match
	 * 2. Move time to R-63
	 * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
	 * 4. Run Claims Offline Batch Job
	 * 5. Move Time to R-46
	 * 6. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
	 * 7. Retrieve policy and enter renewal image
	 * 8. Verify Claim Data is applied to the correct driver.
	 * @details Clean Path. Expected Result is that claims data is applied to the correct driver
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
	public void PAS14679_TestCase1(@Optional("AZ") @SuppressWarnings("unused") String state) {
		TestData testData = getPolicyTD();
		List<TestData> testDataDriverData = new ArrayList<>();// Merged driver tab with 3 drivers
		testDataDriverData.add(testData.getTestData("DriverTab"));
		testDataDriverData.addAll(getTestSpecificTD("TestData_DriverTab_OfflineClaim").resolveLinks().getTestDataList("DriverTab"));
		TestData adjusted = testData.adjust("DriverTab",testDataDriverData);

		// Create Customer and Policy with 3 drivers
		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(adjusted);

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

		// Create the claim response
		createCasClaimResponseAndUpload(policyNumber, TWO_CLAIMS_DATA_MODEL, CLAIM_TO_DRIVER_LICENSE);

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
		CustomSoftAssertions.assertSoftly(softly -> {
			DriverTab driverTab = new DriverTab();
			ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
			softly.assertThat(DriverTab.tableDriverList).hasRows(3);

			// Check 1st driver. No Claims
			softly.assertThat(DriverTab.tableActivityInformationList).hasRows(0);

			// Check 2nd driver. 2 Claim.
			DriverTab.tableDriverList.selectRow(2);
			softly.assertThat(DriverTab.tableActivityInformationList).hasRows(2);
			softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
			softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_1);
			DriverTab.tableActivityInformationList.selectRow(2);
			softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
			softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_2);

			// Check 3rd driver. No Claims
			DriverTab.tableDriverList.selectRow(3);
			softly.assertThat(DriverTab.tableActivityInformationList).isPresent(false);
		});
	}

}


