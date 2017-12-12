package aaa.modules.regression.sales.auto_ca.choice.functional;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.models.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.PolicyCINBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinRenewal extends PolicyCINBaseTest {

    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation (MVR Activity)
     * @scenario
     * 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Manual Renewal for Policy (add driver which has chargeable violation)
     * 5. Check that 'Customer Info Notice' file is generated.
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void testCinRenewalMvr(@Optional("CA") String state) {
        createPolicyForTest("N/A");
        renewPolicy("MVR");

        //Get actual value
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber);
        verifyDocumentOrder(documentsList, DocGenEnum.Documents._55_3333, DocGenEnum.Documents._55_3500);
    }

    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation (CLUE Activity)
     * @scenario
     * 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Manual Renewal for Policy (add driver which has chargeable violation)
     * 5. Check that 'Customer Info Notice' file is generated.
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void testCinRenewalClue(@Optional("CA") String state) {
        createPolicyForTest("N/A");
        renewPolicy("CLUE");

        //Get actual value
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber);
        verifyDocumentOrder(documentsList, DocGenEnum.Documents._55_3333, DocGenEnum.Documents._55_3500);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    @Override
    protected TestData preparePolicyTestData() {
        return getPolicyDefaultTD();
    };
}
