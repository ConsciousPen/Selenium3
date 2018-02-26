package aaa.modules.regression.document_fulfillment.auto_ca.select.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.document_fulfillment.template.functional.TestCinAbstractAutoCA;
import org.junit.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinRenewalAutoCASelect extends TestCinAbstractAutoCA{
    /**
     * Depends on ChoicePointMvrMockData mocksheet was updated with Applicant: First Name: MvrChargeable Last Name: Activity
     *
     * @name Test CIN Document generation (MVR activity exists)
     * @scenario
     * 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Renewal  - add Driver which has chargeable MVR violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = "PAS-6341")
    public void testCinRenewalCLUE(@Optional("CA") String state) {
        TestData policyTD = overrideDocumentsAndBind(disableMemebership(getPolicyDefaultTD()));

        String policyNumber = createPolicy(policyTD);

        TestData renewalTD = getTestSpecificTD("TestData_Renewal_Clue");
        renewPolicy(policyNumber, overrideDriverActivityReports(renewalTD));
    }

    /**
     * Depends on ChoicePointMvrMockData mocksheet was updated with Applicant: First Name: MvrChargeable Last Name: Activity
     *
     * @name Test CIN Document generation (MVR activity exists)
     * @scenario
     * 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Renewal  - add Driver which has chargeable MVR violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = "PAS-6341")
    public void testCinRenewalMVR(@Optional("CA") String state) {
        TestData policyTD = overrideDocumentsAndBind(disableMemebership(getPolicyDefaultTD()));

        String policyNumber = createPolicy(policyTD);

        TestData renewalTD = getTestSpecificTD("TestData_Renewal_Clue");
        renewPolicy(policyNumber, overrideDriverActivityReports(renewalTD));
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }
}
