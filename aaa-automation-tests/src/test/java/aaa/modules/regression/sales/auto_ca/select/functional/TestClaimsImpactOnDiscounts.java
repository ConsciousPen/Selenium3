package aaa.modules.regression.sales.auto_ca.select.functional;

import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsCATemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class TestClaimsImpactOnDiscounts extends TestOfflineClaimsCATemplate {

    private static final String CLAIM_NUMBER_1 = "Claim-GDD-111";
    private static final String CLAIM_NUMBER_2 = "Claim-GDD-222";
    private static final String GDD_PU_CLAIMS_DATA_MODEL = "gdd_PUClaims_data_model.yaml";

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @author Mantas Garsvinskas
     * PAS-18303 - Renewal: Good Driver Discount Cannot be Influenced by Permissive Use Claims (CAS/CLUE/CCInputs)
     * PAS-23190 - Endorsement/NB/Rewrite: Good Driver Discount Cannot be Influenced by Permissive Use Claims (CAS/CLUE/CCInputs)
     * @name Test Permissive Use Claims (CC input/internal/CLUE) impact on policy Good Driver Discount (GDD)
     * @scenario Test Steps:
     * 1. Create Auto Select Quote with 1 driver:
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = {"PAS-18303", "PAS-23190"})
    public void pas18303_goodDriverDiscountForPUClaims(@Optional("CA") @SuppressWarnings("unused") String state) {

        // Claim Dates: claimDateOfLoss/claimOpenDate/claimCloseDate all are the same
        String claim1_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(93).toLocalDate().toString();
        String claim2_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(80).toLocalDate().toString();

        Map<String, String> UPDATE_CAS_RESPONSE_DATE_FIELDS =
                ImmutableMap.of(CLAIM_NUMBER_1, claim1_dates, CLAIM_NUMBER_2, claim2_dates);

        //Adjusted Test Data for: CCInput/CLUE/Internal Claims
        TestData testDataForCLUE = getTestSpecificTD("TestData_DriverTab_DiscountsGDD_CAS").resolveLinks();
        TestData td = getPolicyTD().adjust(testDataForCLUE);

        //Adjusted Test Data after assertions
        TestData tdAfterValidation = getTestSpecificTD("TestData_DriverActivityReportsTab_CAS").resolveLinks();
        TestData td2 = getPolicyTD().adjust(tdAfterValidation);

        // Verify GDD during NB Quote Creation
        createQuoteAndFillUpTo(td, DriverActivityReportsTab.class);
        validateGDD();

        policy.getDefaultView().fillFromTo(td2, PremiumAndCoveragesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        mainApp().close();

        // Retrieve Internal Claims
        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        createCasClaimResponseAndUploadWithUpdatedDates(policyNumber, GDD_PU_CLAIMS_DATA_MODEL, UPDATE_CAS_RESPONSE_DATE_FIELDS);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Verify GDD during: Renewal Quote, Endorsement Quote, Rewritten Quote
        validateGDDonRenewalEndorsementRewrittenQuote();

    }
}
