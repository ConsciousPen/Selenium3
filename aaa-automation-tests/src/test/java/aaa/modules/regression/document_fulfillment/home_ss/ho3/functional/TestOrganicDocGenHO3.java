package aaa.modules.regression.document_fulfillment.home_ss.ho3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestOrganicDocGenAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestOrganicDocGenHO3 extends TestOrganicDocGenAbstract {

    /**
     * @name Creation organic policy for checking 'Expiration Notice' letter AH64XX transaction code
     * @scenario
     * 1. Create Customer
     * 2. Create Organic Policy
     * 3. Generate Bill at R-20
     * 4. Generate 'Expiration Notice' at R+10
     * 5. Check that form is getting generated with correct content
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {Constants.States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-29335"})
    public void pas29335_expirationNoticeFormGeneration(@Optional("AZ") String state) throws NoSuchFieldException {
        super.pas29335_expirationNoticeFormGeneration(state);
    }
    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }
}
