package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.models.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
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
     * @name Test CIN Document generation for CA Select New Business
     * @scenario
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void cinHomeTestScenario(@Optional("CA") String state) {
        String policyNumber = createPolicyForTest("N/A");
        //get all the documents in the package
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber);
        //check the document sequence
        verifyDocumentOrder(documentsList, DocGenEnum.Documents._61_2006, null);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }

    @Override
    protected TestData preparePolicyTestData() {
        TestData testData = getPolicyTD();
        testData.adjust(TestData.makeKeyPath(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel()), getTestSpecificTD("PublicProtectionClass"));
        //TODO:Add mocked data and deal with ISO360Report
        return testData;
    }
}
