package aaa.modules.regression.sales.auto_ca.select.functional;

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
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.PolicyCINBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinNewBusiness extends PolicyCINBaseTest {

    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation (MVR activity)
     * @scenario
     * 1. Create Customer
     * 2. Create Policy with Driver which has chargeable violation
     * 3. Check that 'Customer Info Notice' file is generated.
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void testCinNewBusinessMvr(@Optional("CA") String state) {
        createPolicyForTest("MVR");

        //get all the documents in the package
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber);
        //check the document sequence
        verifyDocumentOrder(documentsList, DocGenEnum.Documents._55_3333, DocGenEnum.Documents._55_3500);
    }

    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation (CLUE activity)
     * @scenario
     * 1. Create Customer
     * 2. Create Policy with Driver which has chargeable violation
     * 3. Check that 'Customer Info Notice' file is generated.
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void testCinNewBusinessClue(@Optional("CA") String state) {
        createPolicyForTest("CLUE");

        //get all the documents in the package
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber);
        //check the document sequence
        verifyDocumentOrder(documentsList, DocGenEnum.Documents._55_3333, DocGenEnum.Documents._55_3500);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    @Override
    protected TestData preparePolicyTestData() {
        TestData testData = getPolicyTD();
        testData.adjust(TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName()), getTestSpecificTD("PrefillTab"))
                .adjust(TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName()), getTestSpecificTD("PremiumAndCoveragesTab"));
        return testData;
    }
}