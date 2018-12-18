package aaa.modules.regression.sales.home_ca.ho6.functional;

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

@StateList(states = {Constants.States.CA})
public class TestFireline extends TestFirelineTemplate {

    @Override
    protected PolicyType getPolicyType() { return PolicyType.HOME_CA_HO6; }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - CA
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
     * 9. Verify the underwriting error Fireline_CA02122017 fires
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - CA")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-18311")
    public void pas18311_firelineScore_GreaterThan_firelineLookupTable(@Optional("CA") String state) {
        pas18311_CA_firelineRuleForFirelineTableLookup("94940", "265 CHIPMAN AVE", 8,6, PrivilegeEnum.Privilege.L41, false, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - CA
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
     * 9. Verify the underwriting error Fireline_CA02122017 does not fire
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - CA")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-18914")
    public void pas18311_firelineScore_equalORLess_firelineLookupTable(@Optional("CA") String state) {
        pas18311_CA_firelineRuleForFirelineTableLookup("95963", "265 CHIPMAN AVE", 6,6, PrivilegeEnum.Privilege.L41, false, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with L41
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 5 or above
    (Address in a RetrievePropertyClassificationMockData and should not be in Firelinelookup table)
     * 5. Check that fireline score from report is 5 or above in Reports tab
     * 6. Verify that ADDRESS level is match in Property Info Tab
     * 7. Try Purchase policy
     * 8. Check that Error tab appeared
     * 9. Verify the underwriting error Fireline_CA02122017 fires
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - CA")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-18914")
    public void pas18311_firelineScore_GreaterThan_notIN_firelineLookupTable(@Optional("CA") String state) {
        pas18311_CA_firelineRuleForFirelineTableLookup("92674", "265 CHIPMAN AVE", 5,4, PrivilegeEnum.Privilege.L41, false, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - SS
     * @scenario
     * 1. Open application with L41
     * 2. Create Customer
     * 3. Initiate Policy creation
     * 4. In Applicant tab fill specific address to get fireline score of 4 or below
    (Address in a RetrievePropertyClassificationMockData and should not be in Firelinelookup table)
     * 5. Check that fireline score from report is 4 or below in Reports tab
     * 6. Verify that ADDRESS level is match in Property Info Tab
     * 7. Try Purchase policy
     * 8. Check that Error tab appeared
     * 9. Verify the underwriting error Fireline_CA02122017 does not fire
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - CA")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-18914")
    public void pas18311_firelineScore_EqualORLess_notIN_firelineLookupTable(@Optional("CA") String state) {
        pas18311_CA_firelineRuleForFirelineTableLookup("92676", "265 CHIPMAN AVE", 4,4, PrivilegeEnum.Privilege.L41, false, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - CA
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
     * 9. Verify the underwriting error Fireline_CA02122017 fires
     * 10.Verify that they do not  have the ability to override the rule
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - CA")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-18311")
    public void pas18311_firelineScore_notOverridePrivileged(@Optional("CA") String state) {
        pas18311_CA_firelineRuleForFirelineTableLookup("94940", "265 CHIPMAN AVE", 8,6, PrivilegeEnum.Privilege.F35, false, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - CA
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
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - CA")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-18311")
    public void pas18914_firelineRuleForWoodShingleRoofs_trigger(@Optional("CA") String state) {
        pas18311_CA_firelineRuleForFirelineTableLookup("93711", "3680 W Shaw Ave", 4, 6, PrivilegeEnum.Privilege.L41, false, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - CA
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
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - CA")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-18311")
    public void pas18914_firelineRuleForWoodShingleRoofs_noTrigger(@Optional("CA") String state) {
        pas18311_CA_firelineRuleForFirelineTableLookup("92899", "265 CHIPMAN AVE", 2, 6, PrivilegeEnum.Privilege.L41, false, ADDRESS);
    }

    /**
     * @author Parth Varmora
     * @name test: Fireline rules for the Fireline Lookup table  - CA
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
     * 10. Verify the underwriting error AAA_HO_CA10107077 does fire
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fireline rules for the Fireline Lookup table  - CA")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-18311")
    public void pas18311_firelineScore_ZIP_firelineLookupTable(@Optional("CA") String state) {
        pas18311_CA_firelineRuleForFirelineTableLookup("95925", "265 CHIPMAN AVE", 7,6, PrivilegeEnum.Privilege.L41,false, ZIP);
    }

    /**
     * @author Rokas Lazdauskas
     * @name test: Revise CA Automated Exception model to remove AE#5 rule
     * @scenario
     * 1. Create Customer
     * 2. Initiate Policy creation
     * 3. Fill information which should satisfy AE#5 rule for CA
     * 3.1. Have underlying AAA Auto policy (which has Auto policy BI limit != $25.000/$50.000)
     * 3.2. Applicant must be a member
     * 3.3. Fireline score should be 5 or 6
     * 3.4. Coverage A must be less than a million dollars
     * 4. Try Purchase policy
     * 5. Check that Error tab appeared and fireline rule ERROR_AAA_HO_Fireline_CA02122017 appeared
     * // Note: This was automated exception rule that if quote meets all requirments from step 3. error wouldn't be fire and quote was eligible to purchase)
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-22425")
    public void pas18296_AE5RuleNotTriggering(@Optional("CA") String state) {
        pas18296_AE5RuleNotTriggering("91016", "265 CHIPMAN AVE", 5);
    }
}