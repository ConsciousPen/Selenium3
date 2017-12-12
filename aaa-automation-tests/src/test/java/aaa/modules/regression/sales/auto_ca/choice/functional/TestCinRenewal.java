package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.models.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.PolicyCINBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.util.List;

public class TestCinRenewal extends PolicyCINBaseTest {

    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation (MVR Activity)
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void testCinRenewalMvr(@Optional("CA") String state) {
        String policyNumber = createPolicyForTest("N/A");
        renewPolicy(MVR, policyNumber);
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber);
        verifyDocumentOrder(documentsList, DocGenEnum.Documents.AA02CA, DocGenEnum.Documents.AA09CA);
    }

    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation (CLUE Activity)
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void testCinRenewalClue(@Optional("CA") String state) {
        String policyNumber = createPolicyForTest("N/A");
        renewPolicy(CLUE, policyNumber);
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber);
        verifyDocumentOrder(documentsList, DocGenEnum.Documents.AA02CA, DocGenEnum.Documents.AA09CA);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    @Override
    protected TestData preparePolicyTestData() {
        return getPolicyDefaultTD();
    }

    ;
}
