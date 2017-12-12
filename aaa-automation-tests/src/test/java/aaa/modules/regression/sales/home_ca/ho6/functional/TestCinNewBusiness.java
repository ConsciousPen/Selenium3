package aaa.modules.regression.sales.home_ca.ho6.functional;

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
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.PolicyCINBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinNewBusiness extends PolicyCINBaseTest {
    /**
     * @author Rokas Lazdauskas
     * @name Test CIN Document generation (PROPERTY activity)
     * @details
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-6341")
    @Parameters({STATE_PARAM})
    public void testCinNewBusinessProperty(@Optional("CA") String state) {
        mainApp().open();
        String policyNumber = createPolicyForTest(PROPERTY);
        //get all the documents in the package
        List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);
        //check the document sequence
        verifyDocumentOrder(documentsList, DocGenEnum.Documents._61_2006, null);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO6;
    }

    @Override
    protected TestData preparePolicyTestData() {
        TestData testData = getPolicyTD();
        testData.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeCaMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel()), "PropChargeable")
                .adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeCaMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel()), "Activity");
        return testData;
    }
}
