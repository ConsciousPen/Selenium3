package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.models.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.PolicyCINBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.util.List;

public class TestCinNewBusiness extends PolicyCINBaseTest {

    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation (MVR activity exists)
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void testCinNewBusinessMvr(@Optional("CA") String state) {
        String policyNumber = createPolicyForTest(MVR);
        //get all the documents in the package
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber);
        //check the document sequence
        verifyDocumentOrder(documentsList, DocGenEnum.Documents.AA02CA, DocGenEnum.Documents.WUAECA);
    }

    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation (CLUE activity exists)
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void testCinNewBusinessClue(@Optional("CA") String state) {
        String policyNumber = createPolicyForTest(CLUE);
        //get all the documents in the package
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber);
        //check the document sequence
        verifyDocumentOrder(documentsList, DocGenEnum.Documents.AA02CA, DocGenEnum.Documents.WUAECA);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    @Override
    protected TestData preparePolicyTestData() {
        TestData testData = getPolicyTD();
        testData.adjust(TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName()), getTestSpecificTD("PrefillTab"))
                .adjust(TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName()), getTestSpecificTD("PremiumAndCoveragesTab"));
        return testData;
    }
}