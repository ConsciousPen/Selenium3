package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestMembershipTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestMembershipPendingCA extends TestMembershipTemplate {
    @Override
    protected PolicyType getPolicyType() {return PolicyType.HOME_CA_HO3;}
    /**
     * @author Robert Boles
     * @name Test Align Current AAA Member for CA products (Auto and Property) with SS - PAS-17784
     * @scenario
     * 1. Create Customer.
     * 2. Create CA H03 Quote
     * 3. Current AAA member will have the option with Membership "Membership Pending"
     * 4. Navigate to Premium & Coverages tab and Calculate Premium
     * 5. Premium will reduce and AAA Membership discount applies in Discounts section
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17784: Align Current AAA Member for CA products (Auto and Property) with SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-17784")
    public void pas17784_AC1_CA_HO3_Pending_Option(@Optional("") String state) {
        setKeyPathsAndGenerateQuote();
        pendingMembershipValidations_AC1_3();
    }

    /**
     * @author Robert Boles
     * @name Test Align Current AAA Member for CA products (Auto and Property) with SS - PAS-17784
     * @scenario
     * 1. Create Customer.
     * 2. Create CA HO3 Policy and add endorsement
     * 5. Navigate to tab and validate Membership Pending is not an available option for Current AAA Member
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17784: Align Current AAA Member for CA products (Auto and Property) with SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-17784")
    public void pas17784_AC4_AC5_CA_Pending_Removed_Endorsement_HO3(@Optional("") String state) {
        addEndorsementAndCheckForMSPending();
    }

    /**
     * @author Robert Boles
     * @name Test Align Current AAA Member for CA products (Auto and Property) with SS - PAS-17784
     * @scenario
     * 1. Create Customer.
     * 2. Create CA HO3 Quote
     * 3. Current AAA member will have the option with Membership "Membership Pending"
     * 4. Bind policy and move to renewal TP1 - Run policyAutomatedRenewalAsyncTaskGenerationJob
     * 5. Select renewal image and validate Membership Pending is not an available option for Current AAA Member
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17784: Align Current AAA Member for CA products (Auto and Property) with SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-17784")
    public void pas17784_AC4_AC5_CA_Pending_Removed_Renewal_HO3(@Optional("") String state) {
        openAppAndCreatePolicy();
        generateRenewalImageAndCheckForMSPending();
    }

}
