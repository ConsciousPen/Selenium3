package aaa.modules.regression.document_fulfillment.pup.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMaigConversionHomePUP extends TestMaigConversionHomeTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.PUP;
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-2674"})
    public void pas2674_formsPresenceAndSequenceNJ(@Optional("NJ") String state) {

        TestData testData = adjustWithSeniorInsuredData(adjustWithPupData(getConversionPolicyDefaultTD()));
        createPolicyPupConvForTD(testData, PolicyType.HOME_SS_HO3);
        verifyFormsSequence(testData);
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-2674"})
    public void pas2674_formsPresenceAndSequence(@Optional("VA") String state) {

        TestData testData = adjustWithMortgageeData(getConversionPolicyDefaultTD());

        verifyFormsSequence(testData);
    }
}
