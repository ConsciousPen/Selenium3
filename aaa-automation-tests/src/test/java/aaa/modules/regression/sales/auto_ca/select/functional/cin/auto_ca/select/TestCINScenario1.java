package aaa.modules.regression.sales.auto_ca.select.functional.cin.auto_ca.select;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.models.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.auto_ca.select.functional.cin.PolicyCINBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.util.List;

public class TestCINScenario1 extends PolicyCINBaseTest {

    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation for CA Select New Business
     * @scenario
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void cftTestScenario1(@Optional("CA") String state) {
        String policyNumber = createPolicyForTest();
        //get all the documents in the package
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE.name());
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
                .adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName()), getTestSpecificTD("DriverTab"))
                .adjust(TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName()), getTestSpecificTD("PremiumAndCoveragesTab"))
                .adjust(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName()), getTestSpecificTD("DocumentsAndBindTab"));
        return testData;
    }
}
