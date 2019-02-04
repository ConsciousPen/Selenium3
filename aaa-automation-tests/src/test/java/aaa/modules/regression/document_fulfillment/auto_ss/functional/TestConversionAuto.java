package aaa.modules.regression.document_fulfillment.auto_ss.functional;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestConversionAuto extends TestMaigConversionHomeAbstract {

    /**
     * @name Creation converted policy for checking 'Expiration Notice' letter AH64XX
     * @scenario
     * 1. Create Customer
     * 2. Create Conversion Policy
     * 3. Generate Bill at R-20
     * 4. Generate 'Expiration Notice' at R+10
     * 5. Check that form is getting generated with correct content
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = {"PAS-21331", "PAS-21588"})
    public void pas20836_expirationNoticeFormGeneration(@Optional("AZ") String state) throws NoSuchFieldException {
        super.pas20836_expirationNoticeFormGeneration(state);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

}
