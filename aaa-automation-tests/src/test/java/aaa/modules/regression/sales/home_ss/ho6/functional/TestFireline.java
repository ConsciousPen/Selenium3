package aaa.modules.regression.sales.home_ss.ho6.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.PrivilegeEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestFirelineTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

//Fireline rules is applicable for those states, but for testing purposes as we need mock data, tests only testing AZ
@StateList(states = {Constants.States.AZ, Constants.States.CO, Constants.States.ID, Constants.States.NV, Constants.States.OR, Constants.States.UT})
public class TestFireline extends TestFirelineTemplate {

    @Override
    protected PolicyType getPolicyType() { return PolicyType.HOME_SS_HO6; }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with L41
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 7 or above
    (found in a RetrievePropertyClassificationMockData)
     * 5. Check that fireline score from report is 7 or above in Reports tab
     * 6. Verify that ADDRESS level is match in Property Info Tab
     * 7. Try Purchase policy
     * 8. Check that Error tab appeared
     * 9. Verify the underwriting error AAA_HO_Fireline fires
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-21652")
    public void pas21652_firelineScore_GreaterThan_firelineLookupTable(@Optional("AZ") String state) {
        pas21652_SS_firelineRuleForFirelineTableLookup("85713", "21 MEADOW PARK DR", 7,6, PrivilegeEnum.Privilege.L41, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with L41
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 7 or above
    (Address in a RetrievePropertyClassificationMockData and should not be in Firelinelookup table)
     * 5. Check that fireline score from report is 7 or above in Reports tab
     * 6. Verify that ADDRESS level is match in Property Info Tab
     * 7. Try Purchase policy
     * 8. Check that Error tab appeared
     * 9. Verify the underwriting error AAA_HO_Fireline fires
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-21652")
    public void pas21652_firelineScore_GreaterThan_notIN_firelineLookupTable(@Optional("AZ") String state) {
        pas21652_SS_firelineRule_notIN_FirelineTableLookup("85192", "14602 N 19th Ave", 5,4, PrivilegeEnum.Privilege.L41);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with L41
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 6 or below
    (found in a RetrievePropertyClassificationMockData)
     * 5. Check that fireline score from report is 6 or below in Reports tab
     * 6. Verify that ADDRESS level is match in Property Info Tab
     * 7. Try Purchase policy
     * 8. Check that Error tab appeared
     * 9. Verify the underwriting error AAA_HO_Fireline does not fire
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-21652")
    public void pas21652_firelineScore_EqualORLess_firelineLookupTable(@Optional("AZ") String state) {
        pas21652_SS_firelineRuleForFirelineTableLookup("85019", "314 KINGSWOOD ST", 6, 6, PrivilegeEnum.Privilege.L41, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with L41
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 6 or below
    (Address in a RetrievePropertyClassificationMockData and should not be in Firelinelookup table)
     * 5. Check that fireline score from report is 6 or below in Reports tab
     * 6. Verify that ADDRESS level is match in Property Info Tab
     * 7. Try Purchase policy
     * 8. Check that Error tab appeared
     * 9. Verify the underwriting error AAA_HO_Fireline does not fire
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-21652")
    public void pas21652_firelineScore_EqualORLess_notIN_firelineLookupTable(@Optional("UT") String state) {
        pas21652_SS_firelineRuleForFirelineTableLookup("84663", "913 S ARTISTIC CIR", 4, 4, PrivilegeEnum.Privilege.L41, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with F35
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 7 or above
    (found in a RetrievePropertyClassificationMockData)
     * 5. Check that fireline score from report is 7 or above in Reports tab
     * 6. Verify that ADDRESS level is match in Property Info Tab
     * 7. Try Purchase policy
     * 8. Check that Error tab appeared
     * 9. Verify the underwriting error AAA_HO_Fireline fires
     * 10. Verify that they do not have the ability to override the rule
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-21652")
    public void pas21652_firelineRules_notOverridePrivileged(@Optional("AZ") String state) {
        pas21652_SS_firelineRuleForFirelineTableLookup("85713", "21 MEADOW PARK DR", 7,6, PrivilegeEnum.Privilege.F35, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with L41
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 2 (found in a RetrievePropertyClassificationMockData)
     * 5. Check that fireline score from report is 2 in Reports tab
     * 6. Try Purchase policy
     * 7. Check that Error tab did NOT appear
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-21652")
    public void pas18302_firelineRuleForWoodShingleRoofs_noTrigger_privileged(@Optional("AZ") String state) {
        pas21652_SS_firelineRuleForFirelineTableLookup("85601", "586 EAGLE RD.", 2,6, PrivilegeEnum.Privilege.L41, ADDRESS);
    }


    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with L41
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 4 (found in a RetrievePropertyClassificationMockData)
     * 5. Check that fireline score from report is 4 in Reports tab
     * 6. Try Purchase policy
     * 7. Check that Error tab appeared
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-21652")
    public void pas18302_firelineRuleForWoodShingleRoof_trigger_privileged(@Optional("AZ") String state) {
        pas21652_SS_firelineRuleForFirelineTableLookup("85741", "4321 Monsoon Trail", 4,6, PrivilegeEnum.Privilege.L41, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with L41
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 7 or above
    (found in a RetrievePropertyClassificationMockData)
     * 5. Check that fireline score from report is 7 or above in Reports tab
     * 6. Verify that ADDRESS level is not match in Property Info Tab
     * 7. Try Purchase policy
     * 8. Check that Error tab appeared
     * 9. Verify the underwriting error AAA_HO_Fireline does not fire
     * 10. Verify the underwriting error AAA_HO_SS14061993 does fire
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-21652")
    public void pas21652_firelineRules_addressLevelNotMatched(@Optional("AZ") String state) {
        pas21652_SS_firelineRuleForFirelineTableLookup("85713", "9485 SHARK DR.", 7,6, PrivilegeEnum.Privilege.L41, ZIP);
    }
}