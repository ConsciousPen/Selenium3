package aaa.modules.regression.sales.auto_ca.select.functional;

import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.google.common.collect.ImmutableMap;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsCATemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class TestClaimsImpactOnDiscounts extends TestOfflineClaimsCATemplate {

    private static final String GDD_PU_CLAIMS_DATA_MODEL = "gdd_PUClaims_data_model.yaml";

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @author Mantas Garsvinskas
     * PAS-18303 - Renewal: Good Driver Discount Cannot be Influenced by Permissive Use Claims (CAS/CLUE/CCInputs)
     * PAS-23190 - Endorsement/NB/Rewrite: Good Driver Discount Cannot be Influenced by Permissive Use Claims (CAS/CLUE/CCInputs)
     * PAS-18317 - UI-CA: do NOT Show Permissive Use Indicator on Driver Tab (non-FNI)
     * @name Test Permissive Use Claims (CC input/internal/CLUE) impact on policy Good Driver Discount (GDD)
     * @scenario Test Steps:
     * 1. Create Auto Select Quote with 2 drivers:
     * 1.1: FNI: 1 CLUE Claims (Accidents); 2 CAS Claims (visible on R only); 1 CC Input Claims: Major Violation
     * 1.2. Non FNI 1 CC Input Claims: Major Violation;
     * 1.3 All Claims have Points: more than 1 and are Included in rating.
     * 2. Change all Claims to PU Claims;
     * 3. - Verify: GDD is available for Quote with only PU Claims;
     * 4. Leave one CC Input Claim as NOT Permissive Use (PU = No)
     * 5. - Verify: GDD is not available for Quote
     * 6. Verify that 2nd Driver doesnt have PU indicator
     * 7. Issue Policy
     * 8. R-63: Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 9. R-46: Run Renewal Part2 + "renewalClaimReceiveAsyncJob"
     * 10. Repeat Validations on Renewal, Endorsement, Rewritten Quote;
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = {"PAS-18303", "PAS-23190", "PAS-18317"})
    public void pas18303_goodDriverDiscountForPUClaims(@Optional("CA") @SuppressWarnings("unused") String state) {

        Map<String, String> UPDATE_CAS_RESPONSE_DATE_FIELDS = ImmutableMap.of(CLAIM_NUMBER_1_GDD, claim1_dates_gdd, CLAIM_NUMBER_2_GDD, claim2_dates_gdd);

        //Adjusted Test Data for: CCInput/CLUE/Internal Claims
        TestData testDataForCLUE = getTestSpecificTD("TestData_DriverTab_DiscountsGDD_CAS").resolveLinks();
        TestData td = getPolicyTD().adjust(testDataForCLUE);

        //Adjusted Test Data after assertions
        TestData tdAfterValidation = getTestSpecificTD("TestData_DriverActivityReportsTab_CAS").resolveLinks();
        TestData td2 = getPolicyTD().adjust(tdAfterValidation);

        // Verify GDD during NB Quote Creation
        createQuoteAndFillUpTo(td, PremiumAndCoveragesTab.class, true);
        premiumAndCoveragesTab.submitTab();

        // Overriding Errors caused by created ActivityInformation entries (Auto Select Rules)
        if (errorTab.isVisible()) {
            errorTab.overrideAllErrors();
            errorTab.buttonOverride.click();
            premiumAndCoveragesTab.submitTab();
        }

        // Verify GDD during NB quote, Also Verify that PU ind is not shown for NON First Named Insured
        validateGDDAndPUIndicatorOnNB(td, td2);

        // Retrieve Internal Claims
        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        createCasClaimResponseAndUploadWithUpdatedDates(policyNumber, GDD_PU_CLAIMS_DATA_MODEL, UPDATE_CAS_RESPONSE_DATE_FIELDS);
        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Verify GDD during: Renewal Quote, Endorsement Quote, Rewritten Quote
        validateGDDonRenewalEndorsementRewrittenQuote();

    }
}
