package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.common.pages.SearchPage.tableSearchResults;
import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
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
import aaa.helpers.claim.ClaimCASResponseTags;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;

@StateList(states = {Constants.States.AZ})
public class TestOffLineClaims extends TestOfflineClaimsTemplate {

    private static final String CLAIM_NUMBER_1 = "1002-10-8702";
    private static final String CLAIM_NUMBER_2 = "1002-10-8703";
    private static final String CLAIM_NUMBER_3 = "1002-10-8704";
	private static final String CLAIM_NUMBER_4 = "1FAZ1111OHS";
	private static final String CLAIM_NUMBER_5 = "4FAZ44444OHS";
    private static final String CLAIM_NUMBER_6 = "1002-10-8705";
    private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE =
            ImmutableMap.of(CLAIM_NUMBER_1, "A12345222", CLAIM_NUMBER_2, "A12345222");
    private static final String TWO_CLAIMS_DATA_MODEL = "two_claims_data_model.yaml";
    private static final String NAME_DOB_CLAIMS_DATA_MODEL = "name_dob_claims_data_model.yaml";
    private static final String INC_IN_RATING_3RD_RENEWAL_DATA_MODEL = "inc_in_rating_3rd_renewal_data_model.yaml";
    private static final String INC_RATING_CLAIM_1 = "IIRatingClaim1";
    private static final String INC_RATING_CLAIM_2 = "IIRatingClaim2";
    private static final String INC_RATING_CLAIM_3 = "IIRatingClaim3";


    /**
     * @author Andrii Syniagin
     * @name Test generation cas claim reponse.
     * @details This is to verify that the framework can correctly build a CAS Response XML. Run this after making any Response or YAML changes
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @Parameters({"state"})
    public void testCreateCasResponse(@Optional("AZ") @SuppressWarnings("unused") String state) {
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper(NAME_DOB_CLAIMS_DATA_MODEL, getCasResponseFileName());
        String policyNumber = "AZSS999999999";
        File claimResponse = batchClaimHelper.processClaimTemplate((response) ->
                setPolicyNumber(policyNumber, response));
        assertThat(claimResponse).exists().isFile();
        assertThat(Assertions.contentOf(claimResponse)).contains(policyNumber);
    }

    /**
     * @author Chris Johns
     * @author Andrii Syniagin
     * PAS-14679 - DL # matching logic
     * PAS-14058 - COMP Claims match to FNI
     * @name Test Offline STUB/Mock Data Claims
     * @scenario Test Steps:
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
    public void pas14679_CompDLMatchMore(@Optional("AZ") @SuppressWarnings("unused") String state) {
        createPolicyMultiDrivers();    // Create Customer and Policy with 4 drivers
        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        createCasClaimResponseAndUpload(policyNumber, TWO_CLAIMS_DATA_MODEL, CLAIM_TO_DRIVER_LICENSE);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        // Enter renewal image and verify claim presence
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        CustomSoftAssertions.assertSoftly(softly -> {
            DriverTab driverTab = new DriverTab();
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();
            softly.assertThat(DriverTab.tableDriverList).hasRows(4);

            // Check 1st driver: FNI, has the COMP match claim
            softly.assertThat(DriverTab.tableActivityInformationList).hasRows(1);
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_1);

            // Check 2nd driver: Has DL match claim
            DriverTab.tableDriverList.selectRow(2);
	        softly.assertThat(DriverTab.tableActivityInformationList).hasRows(1);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_2);
        });
    }

    /**
     * @author Kiruthika Rajendran
     * @author Chris Johns
     * PAS-8310 - LASTNAME_FIRSTNAME_DOB & LASTNAME_FIRSTNAME_YOB matches
     * PAS-17894 - LASTNAME_FIRSTNAME & LASTNAME_FIRSTINITAL_DOB matches
     * @name Test Match more claims to satisfy the Name and DOB match logic LASTNAME_FIRSTNAME_DOB,  LASTNAME_FIRSTNAME_YOB
     * @scenario Test Steps:
     * 1. Create a Policy with 4 drivers
     * 2. Move time to R-63
     * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 4. Run Claims Offline Batch Job
     * 5. Move Time to R-46
     * 6. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 7. Retrieve policy and enter renewal image
     * 8. Verify Claim Data is applied to the driver3 and driver4
     * @details Clean Path. Expected Result is that claims data is applied to the correct driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
    public void pas8310_nameDOBYOBMatchMore(@Optional("AZ") @SuppressWarnings("unused") String state) {
        createPolicyMultiDrivers();        // Create Customer and Policy with 4 drivers
        runRenewalClaimOrderJob();        // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        createCasClaimResponseAndUpload(policyNumber, NAME_DOB_CLAIMS_DATA_MODEL, null);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy and verify claim presence on renewal image
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
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


    /**
     * @author Mantas Garsvinskas
     * PAS-14552 - INC IN RATING: Determine if Previously Unmatched but Now matched should be Included in Rating
     * @name Test Claims 'Include In Rating' determination according to Occurrence date
     * @scenario Test Steps:
     * 1. Create a Policy with 1 driver ANNUAL TERM;
     * 3. Repeat Steps below and get to the Third Renewal Image
     * 3.1. Move time to R-63
     * 3.2. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 3.3. Move Time to R-46
     * 3.4. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 4 Retrieve policy and enter 3rd renewal image
     * 5. Verify Claim Data:
     * 5.1 Claim1: claimNumber1 - NOT INCLUDED IN RATING dateOfLoss = Two Policy Terms - 1 day (R1-1)
     * 5.1 Claim2: claimNumber2 - INCLUDED IN RATING dateOfLoss = Two Policy Terms (R1)
     * 5.1 Claim3: claimNumber3 - INCLUDED IN RATING dateOfLoss = Current Date (R3-46)
     *
     * //TODO:gunxgar add one more claim on 1st Renewal for Existing match, will be implemented in other Story
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14552")
    public void PAS14552_includeClaimsInRatingDetermination(@Optional("AZ") @SuppressWarnings("unused") String state) {

        // Toggle ON MatchMoreClaims Logic
        DBService.get().executeUpdate(SQL_UPDATE_MATCHMORECLAIMS_DISPLAYVALUE);

        // Claim Dates: claimDateOfLoss/claimOpenDate/claimCloseDate all are the same
        String claim1_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(1).toLocalDate().toString();
        String claim2_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).toLocalDate().toString();
        String claim3_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(3).toLocalDate().toString();

        Map<String, String> UPDATE_CAS_RESPONSE_FIELD_DATEOFLOSS =
                ImmutableMap.of(INC_RATING_CLAIM_1, claim1_dates, INC_RATING_CLAIM_2, claim2_dates, INC_RATING_CLAIM_3, claim3_dates);

        Map<String, String> UPDATE_CAS_RESPONSE_FIELD_OPENDATE =
                ImmutableMap.of(INC_RATING_CLAIM_1, claim1_dates, INC_RATING_CLAIM_2, claim2_dates, INC_RATING_CLAIM_3, claim3_dates);

        Map<String, String> UPDATE_CAS_RESPONSE_FIELD_CLOSEDATE =
                ImmutableMap.of(INC_RATING_CLAIM_1, claim1_dates, INC_RATING_CLAIM_2, claim2_dates, INC_RATING_CLAIM_3, claim3_dates);

        TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(driverTab.getMetaKey(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "A19191911").resolveLinks();

        // Create Customer and Policy
	    openAppAndCreatePolicy(testData);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        mainApp().close();

        //Run Jobs to create and issue 1st Renewal
        runRenewalClaimOrderJob();
        runRenewalClaimReceiveJob();
        issueGeneratedRenewalImage(policyNumber);

        //Run Jobs to create and issue 2nd Renewal
        runRenewalClaimOrderJob();
        runRenewalClaimReceiveJob();
        issueGeneratedRenewalImage(policyNumber);

        //Run Jobs to create 3rd required Renewal and validate the results
        runRenewalClaimOrderJob();

        // Create CAS response file
        String casResponseFileName = getCasResponseFileName();
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper(INC_IN_RATING_3RD_RENEWAL_DATA_MODEL, casResponseFileName);

        // Make Changes to created CAS Response file
        File claimResponseFile = batchClaimHelper.processClaimTemplate((response) -> {
            setPolicyNumber(policyNumber, response);
            updateFieldForClaim(UPDATE_CAS_RESPONSE_FIELD_DATEOFLOSS, response, ClaimCASResponseTags.TagNames.CLAIM_DATE_OF_LOSS);
            updateFieldForClaim(UPDATE_CAS_RESPONSE_FIELD_OPENDATE, response, ClaimCASResponseTags.TagNames.CLAIM_OPEN_DATE);
            updateFieldForClaim(UPDATE_CAS_RESPONSE_FIELD_CLOSEDATE, response, ClaimCASResponseTags.TagNames.CLAIM_CLOSE_DATE);
        });

        uploadCasResponseFile(claimResponseFile, casResponseFileName);
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
        CustomSoftAssertions.assertSoftly(softly -> {
            ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();

			// Check that Policy Contains 3 Claims
            softly.assertThat(DriverTab.tableActivityInformationList.getAllRowsCount()).isEqualTo(3);

			// PAS14552 - Assert that Claim IS NOT Included In Rating because Date of Loss is older than two terms
            DriverTab.tableActivityInformationList.selectRow(1);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(INC_RATING_CLAIM_1);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("1500");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER)).hasValue("No");

			// PAS14552 - Assert that Claim IS Included In Rating because Date of Loss is equal to two terms eff. date
            DriverTab.tableActivityInformationList.selectRow(2);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(INC_RATING_CLAIM_2);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("1500");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER)).hasValue("Yes");

			// PAS14552 - Assert that Claim IS Included In Rating because Date of Loss is equal to current system date
            DriverTab.tableActivityInformationList.selectRow(3);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(INC_RATING_CLAIM_3);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("1500");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER)).hasValue("Yes");

        });
    }



}
