package aaa.modules.regression.document_fulfillment.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeTemplate;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMaigConversionHomePUP extends TestMaigConversionHomeTemplate {
    TestData testDataPolicy = testDataManager.policy.get(getPolicyType());

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.PUP;
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-2674"})
    public void pas2674_SpecificConversionPacketGenerationForNJ(@Optional("NJ") String state) throws NoSuchFieldException {
        TestData policyCreationTD = getStateTestData(testDataPolicy, "Conversion", "TestData");

        verifyConversionFormsSequence(policyCreationTD.resolveLinks());
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-2674"})
    public void pas2674_SpecificConversionPacketGenerationForOtherStates(@Optional("VA") String state) throws NoSuchFieldException {
        TestData policyCreationTD = getStateTestData(testDataPolicy, "Conversion", "TestData");

        verifyConversionFormsSequence(policyCreationTD.resolveLinks());
    }

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-9816"})
    public void pas9816_SpecificBillingPacketGenerationForOtherStates(@Optional("DE") String state) throws NoSuchFieldException {
        // CW, DE, VA
        TestData policyCreationTD = getStateTestData(testDataPolicy, "Conversion", "TestData").resolveLinks();
        verifyBillingFormsSequence(policyCreationTD.adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","Payment Plan"),"Monthly (Renewal)"));
    }
}
