package aaa.modules.e2e.auto_ca;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario3;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario3 extends Scenario3 {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    @Parameters({"state"})
    @StateList(states = States.CA)
    @Test
    public void TC01_createPolicy(@Optional("CA") String state) {
        tdPolicy = testDataManager.policy.get(getPolicyType());

        TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

        createTestPolicy(policyCreationTD);
        CustomSoftAssertions.assertSoftly(softly -> {
            generateFirstBill(softly);
            generateCancellationNotice();
            cancelPolicy(installmentDueDates.get(1));
            createRemittanceFile();
            payCancellationNoticeByRemittance();
            verifyDocGenForms(true, DocGenEnum.Documents._55_5003);
            verifyDocGenForms(false, DocGenEnum.Documents._55_5080);
            renewalImageGeneration();
            renewalPreviewGeneration();
            renewalOfferGeneration(softly);
            expirePolicy();
            customerDeclineRenewal();
            payRenewOffer();
            verifyDocGenForms(false, DocGenEnum.Documents._55_5003, DocGenEnum.Documents._55_5080);
        });
    }
}