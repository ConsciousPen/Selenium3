package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import aaa.utils.StateList;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

import java.io.File;
import java.util.Map;

import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;
import static org.assertj.core.api.Assertions.assertThat;

@StateList(states = {Constants.States.AZ})
public class TestOffLineClaims extends TestOfflineClaimsTemplate {

    private static final String CLAIM_NUMBER_1 = "1002-10-8702";
    private static final String CLAIM_NUMBER_2 = "1002-10-8703";
    private static final String CLAIM_NUMBER_3 = "1002-10-8704";
	private static final String CLAIM_NUMBER_4 = "1FAZ1111OHS";
	private static final String CLAIM_NUMBER_5 = "4FAZ8888OHS";
	private static final String CLAIM_NUMBER_6 = "8FAZ8888OHS";
    private static final String CLAIM_NUMBER_7 = "1002-10-8705";
    private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE =
            ImmutableMap.of(CLAIM_NUMBER_1, "A12345222", CLAIM_NUMBER_2, "A12345222");
    private static final String TWO_CLAIMS_DATA_MODEL = "two_claims_data_model.yaml";
    private static final String NAME_DOB_CLAIMS_DATA_MODEL = "name_dob_claims_data_model.yaml";

    /**
     * @author Andrii Syniagin
     * @name Test generation cas claim reponse.
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
    public void PAS14679_DLMatchMore(@Optional("AZ") @SuppressWarnings("unused") String state) {
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
        });
    }


    /**
     * @author Kiruthika Rajendran
     * @author Chris Johns
     * PAS-8310 - LASTNAME_FIRSTNAME_DOB & LASTNAME_FIRSTNAME_YOB matches
     * PAS-17894 - LASTNAME_FIRSTNAME, LASTNAME_FIRSTINITAL_DOB, & LASTNAME_YOB matches
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
    public void PAS8310_nameDOBYOBMatchMore(@Optional("AZ") @SuppressWarnings("unused") String state) {
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
	        // PAS-17894 - LASTNAME_FIRSTNAME, LASTNAME_FIRSTINITAL_DOB, & LASTNAME_YOB matches
            DriverTab.tableDriverList.selectRow(3);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_3);
	        DriverTab.tableActivityInformationList.selectRow(2);
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_4);
	        DriverTab.tableActivityInformationList.selectRow(3);
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_5);
	        DriverTab.tableActivityInformationList.selectRow(4);
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
	        softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_6);

            // Check 4th driver.
	        // PAS-8310 - LASTNAME_FIRSTNAME_YOB Match
            DriverTab.tableDriverList.selectRow(4);
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("Internal Claims");
            softly.assertThat(activityInformationAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_NUMBER)).hasValue(CLAIM_NUMBER_7);
        });
    }
}





