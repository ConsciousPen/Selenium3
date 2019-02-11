package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsCATemplate;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

import java.util.Map;

import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;

@StateList(states = {Constants.States.CA})
public class TestClaimsImpactOnDiscounts extends TestOfflineClaimsCATemplate {

    private static final String CLAIM_NUMBER_1 = "Claim-GDD-111";
    private static final String CLAIM_NUMBER_2 = "Claim-GDD-222";
    private static final String COMP_DL_PU_CLAIMS_DATA_MODEL = "comp_dl_pu_claims_data_model_choice.yaml"; //TODO:gunxgar change

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    /**
     * @author Mantas Garsvinskas
     * PAS-18303 - Renewal: Good Driver Discount Cannot be Influenced by Permissive Use Claims (CAS/CLUE/CCInputs)
     * PAS-23190 - Endorsement/NB/Rewrite: Good Driver Discount Cannot be Influenced by Permissive Use Claims (CAS/CLUE/CCInputs)
     * @name Test Permissive Use Claims (CC input/internal/CLUE) impact on policy Good Driver Discount (GDD)
     * @scenario Test Steps:
     * 1. Create a Quote with 1 driver:
     * 1.1: FNI: 2 CLUE Claims (Accidents); 2 CAS Claims (visible on R only); 2 CC Input Claims: Major/Minor Violation;
     * 1.2 All Claims have Points: more than 1 and are Included in rating.
     * 2. Leave one CC Input Claim as NOT Permissive Use (PU = No)
     * 3. - Verify: GDD is not available for Quote
     * 4. Change all Claims to PU Claims;
     * 5. - Verify: GDD is available for Quote with only PU Claims;
     * 6. Issue Policy
     * 7. R-63: Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 8. R-46: Run Renewal Part2 + "renewalClaimReceiveAsyncJob"
     * 9. Repeat Validations on Renewal, Endorsement, Rewritten Quote;
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = {"PAS-18303", "PAS-23190"})
    public void pas18303_goodDriverDiscountForPUClaims(@Optional("CA") @SuppressWarnings("unused") String state) {

        // Claim Dates: claimDateOfLoss/claimOpenDate/claimCloseDate all are the same
        String claims_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(1).toLocalDate().toString();

        Map<String, String> UPDATE_CAS_RESPONSE_DATE_FIELDS =
                ImmutableMap.of(CLAIM_NUMBER_1, claims_dates, CLAIM_NUMBER_2, claims_dates);


        // Toggle ON PermissiveUse Logic
		// Set DATEOFLOSS Parameter in DB: Equal to Claim3 dateOfLoss
		DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
		DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-NOV-18"));

        TestData testData = getTestSpecificTD("TestData_DriverTab_DiscountsGDD_CAC").resolveLinks();
        TestData td = getPolicyTD().adjust(testData);

        mainApp().open();
        createCustomerIndividual();
        policy.createPolicy(td);
       /* TODO:gunxgar - FillUpTo Driver TAB: change all except One Customer Input CLaim to PU;
        - Validate P&C, that GDD is NOT displayed
        - Navigate back change all to be PU;
        - Validate P&C, that GDD is displayed;
        Fill Up From - Finish Quote Creation and issue policy;
        */

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        mainApp().close();


        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedDates(policyNumber, COMP_DL_PU_CLAIMS_DATA_MODEL, UPDATE_CAS_RESPONSE_DATE_FIELDS);

        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        // Enter renewal image and verify claim presence
        buttonRenewals.click();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        /* TODO:gunxgar - Change all except one internal CLaim to PU
        - Validate P&C, that GDD is NOT displayed
        - Navigate back change all to be PU;
        - Validate P&C, that GDD is displayed;
        */

        premiumAndCoveragesTab.saveAndExit();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        /* TODO:gunxgar - Change all except one CLUE Claim
        - Validate P&C, that GDD is NOT displayed
        - Navigate back change all to be PU;
        - Validate P&C, that GDD is displayed;
        */


        premiumAndCoveragesTab.saveAndExit();
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));

        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

        /* TODO:gunxgar - Change all except one Company Input Claim
        - Validate P&C, that GDD is NOT displayed
        - Navigate back change all to be PU;
        - Validate P&C, that GDD is displayed;
        */
    }
}
