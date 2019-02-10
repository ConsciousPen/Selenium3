package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.logs.PasAdminLogGrabber;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsCATemplate;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static aaa.common.pages.SearchPage.tableSearchResults;
import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;
import static org.assertj.core.api.Assertions.assertThat;

@StateList(states = {Constants.States.CA})
public class TestOffLineClaims extends TestOfflineClaimsCATemplate {

	// NOTE: Claims Matching Logic: e2e tests should use HTTP instead of HTTPS in DB (value of Microservice propertyname ='aaaClaimsMicroService.microServiceUrl')
	// Example: http://claims-assignment.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1

    private static final String CLAIM_NUMBER_1 = "1002-10-8702";
    private static final String CLAIM_NUMBER_2 = "1002-10-8703";
    private static final String CLAIM_NUMBER_3 = "1002-10-8704";
    private static final String COMP_DL_PU_CLAIMS_DATA_MODEL = "comp_dl_pu_claims_data_model_select.yaml";
	private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE = ImmutableMap.of(CLAIM_NUMBER_1, "D5435433", CLAIM_NUMBER_2, "D5435433");

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @author Chris Johns
     * @author Andrii Syniagin
     * PAS-14679 - DL # matching logic
     * PAS-14058 - COMP Claims match to FNI
     * PAS-18341 - Added PermissiveUse tag to Claims Service Contract
	 * PAS-18300 - PERMISSIVE_USE match to FNI when dateOfLoss param = claim dateOfLoss
     * PAS-23269 - UI-CA: Show Permissive Use Indicator on Driver Tab
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
     * 9. Verify the Permissive Use indicator in Driver Activity is not editable for the user other than 34/41
     * @details Clean Path. Expected Result is that claims data is applied to the correct driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-14679")
    public void pas14679_CompDLPUMatchMore(@Optional("CA") @SuppressWarnings("unused") String state) {

		// Toggle ON PermissiveUse Logic
		// Set DATEOFLOSS Parameter in DB: Equal to Claim3 dateOfLoss
		// Set RISKSTATECD in DB to get policy DATEOFLOSS working
		DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
		DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-NOV-18"));

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
    }

    /**
     * @author Kiruthika Rajendran
     * PAS-23269 - UI-CA: Show Permissive Use Indicator on Driver Tab
     * @name Test Clue claims STUB/Mock Data Claims
     * @scenario Test Steps:
     * 1. Create a Quote with 4 drivers
     * 2. Calculate the Premium and click on Validate Driving History
     * 3. Go to Driver tab.
     * 4. Check for the Activity for Clue claims     *
     * 5. Verify Clue Claim Data for the correct driver.
     * 9. Verify the Permissive Use indicator in Driver Activity
     * @details Clean Path. Expected Result Permissive Use indicator in Driver Activity
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23269")
    public void pas23269_verifypermissiveUseindicator(@Optional("CA") @SuppressWarnings("unused") String state) {

        // Toggle ON PermissiveUse Logic
        // Set DATEOFLOSS Parameter in DB: Equal to Claim3 dateOfLoss
        // Set RISKSTATECD in DB to get policy DATEOFLOSS working
        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-NOV-18"));

//        TestData testData = getPolicyTD();
//        List<TestData> testDataDriverData = new ArrayList<>();// Merged driver tab with 4 drivers
//        testDataDriverData.add(testData.getTestData("DriverTab"));
//        testDataDriverData.addAll(getTestSpecificTD("TestData_DriverTab_OfflineClaim_PU").resolveLinks().getTestDataList("DriverTab"));
//        adjusted = testData.adjust("DriverTab", testDataDriverData).resolveLinks();

        TestData testData = getPolicyTD();
        adjusted = testData.adjust(getTestSpecificTD("TestData_DriverTab_OfflineClaim_PU").resolveLinks());

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
        DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
        //In driver tab, 4 drivers are added. Company Input activity is added for Driver4
        policy.getDefaultView().fillUpTo(adjusted, PremiumAndCoveragesTab.class, true);
        premiumAndCoveragesTab.submitTab();
        policy.getDefaultView().fillFromTo(adjusted, DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
        documentsAndBindTab.submitTab();
//        new ErrorTab().overrideAllErrors();
//        documentsAndBindTab.submitTab();
        new PurchaseTab().fillTab(adjusted).submitTab();
        policyNumber = PolicySummaryPage.getPolicyNumber();

        /**
         * Initiates an endorsement, calculates premium, orders CLUE report for newly added driver
         * @param policyNumber given policy number
         * @param addDriverTd specific details for the driver being added to the policy
         */
        //public void initiateAddDriverEndorsement(String policyNumber, TestData addDriverTd) {
          /*  mainApp().open();
            SearchPage.openPolicy(policyNumber);
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
            policy.getDefaultView().fill(addDriverTd);

            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
            premiumAndCoveragesTab.calculatePremium();
            premiumAndCoveragesTab.submitTab();

            //Modify default test data to mask unnecessary steps
            TestData td = getPolicyTD()
                    .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                    .mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV.getLabel()));
            new DriverActivityReportsTab().fillTab(td);*/
     //   }

        /**
         * Binds current endorsement: calculates premium, navigates to bind page, and binds endorsement
         */
        /*public void bindEndorsement() {
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
            NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
            new aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab().submitTab();
        }*/


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

    }
}
