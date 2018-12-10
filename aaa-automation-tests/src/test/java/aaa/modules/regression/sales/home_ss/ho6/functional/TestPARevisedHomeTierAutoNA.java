package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.RevisedHomeTierPATemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.PA)
public class TestPARevisedHomeTierAutoNA extends RevisedHomeTierPATemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO6;
    }

    /**
     * @author Josh Carpenter
     * @name Test that the Auto 'Policy Tier' is present on the Applicant tab when adding a PA companion Auto policy and that when the Auto Tier is not available
     * N/A is assigned as the value of Auto Tier after rating calculation.
     * @scenario
     * 1. Create customer
     * 2. Create Auto policy for PA
     * 3. Initiate HO policy for PA with an effective date on or after 5/28/18
     * 4. Add the above create Auto companion policy on the Applicant Tab
     * 5. Verify the Auto 'Policy Tier' is displayed in place of Auto Insurance Score
     * 6. Continue to the Premiums & Coverages Tab and calculate
     * 7. Open rating details dialog and confirm the value of N/A was assigned to the companion Auto Tier
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6849")
    public void pas6849_TestDisplayAutoTierOnApplicantTab(@Optional("PA") String state) {

        pas6849_TestDisplayAutoTierOnApplicantTab();

    }

    /**
     * @author Josh Carpenter
     * @name Test assign N/A value to Auto Tier with existing companion auto policy from a non-PA state (OH) and verify that when the companion auto
     * tier is populated during pre-fill that the blank Auto Tier value is disabled (greyed out).
     * @scenario
     * 1. Create customer
     * 2. Create non-PA Auto policy (OH)
     * 3. Initiate HO policy for PA with an effective date on or after 5/28/18
     * 4. After the Auto Tier is populated during pre-fill, verify the blank Auto Policy Tier value is disabled (greyed out)
     * 5. Proceed with the policy and after calculating premium, open the ratings details dialog
     * 6. Verify Auto tier value assigned to 'N/A'
     * 7. Verify policy can be bound
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6849")
    public void pas6849_TestAutoNAValueWithNonPACompanionAuto(@Optional("PA") String state) {

        pas6849_TestAutoNAValueWithNonPACompanionAuto();

    }
}