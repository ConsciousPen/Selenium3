package aaa.modules.regression.sales.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsCATemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class TestClaimsImpactOnDiscounts extends TestOfflineClaimsCATemplate {

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
        pas18303_goodDriverDiscountForPUClaims();
    }
}
