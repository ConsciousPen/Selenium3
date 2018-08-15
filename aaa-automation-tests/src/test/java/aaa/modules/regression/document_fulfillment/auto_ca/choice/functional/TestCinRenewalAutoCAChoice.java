package aaa.modules.regression.document_fulfillment.auto_ca.choice.functional;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestCinAbstractAutoCA;
import aaa.utils.StateList;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinRenewalAutoCAChoice extends TestCinAbstractAutoCA {
    /**
     * Depends on ChoicePointMvrMockData mocksheet was updated with Applicant: First Name: MvrChargeable Last Name: Activity
     *
     * @name Test CIN Document generation (MVR activity exists)
     * @scenario 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Renewal  - add Driver which has chargeable MVR violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(states = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
    public void testCinRenewalCLUE(@Optional("CA") String state) {
        TestData policyTD = getPolicyDefaultTD().adjust(DOCUMENTS_AND_BIND_PATH, getTestSpecificTD("DocumentsAndBindTab"))
                .adjust(PRODUCT_OWNED_PATH, getTestSpecificTD("AAAProductOwned"));

        String policyNumber = createPolicy(policyTD);

        TestData renewalTD = getTestSpecificTD("TestData_Renewal_Clue").resolveLinks()
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"));
        renewPolicy(policyNumber, renewalTD);
    }

    /**
     * Depends on ChoicePointMvrMockData mocksheet was updated with Applicant: First Name: MvrChargeable Last Name: Activity
     *
     * @name Test CIN Document generation (MVR activity exists)
     * @scenario 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Renewal  - add Driver which has chargeable MVR violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(states = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
    public void testCinRenewalMVR(@Optional("CA") String state) {
        TestData policyTD = getPolicyDefaultTD().adjust(DOCUMENTS_AND_BIND_PATH, getTestSpecificTD("DocumentsAndBindTab"))
                .adjust(PRODUCT_OWNED_PATH, getTestSpecificTD("AAAProductOwned"));

        String policyNumber = createPolicy(policyTD);

        TestData renewalTD = getTestSpecificTD("TestData_Renewal_Mvr").resolveLinks()
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"));
        renewPolicy(policyNumber, renewalTD);
        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER)).isNotNull();
        });
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }
}
