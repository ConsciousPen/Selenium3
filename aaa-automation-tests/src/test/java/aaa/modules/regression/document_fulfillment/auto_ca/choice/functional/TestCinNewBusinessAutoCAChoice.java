package aaa.modules.regression.document_fulfillment.auto_ca.choice.functional;

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

public class TestCinNewBusinessAutoCAChoice extends TestCinAbstractAutoCA {

    /**
     * @name Test CIN Document generation (CA Choice policies should always generate CIN on New Business)
     * @scenario 1. Create Customer
     * 2. Create Policy
     * 3. Verify that CIN is generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.DOCGEN, Groups.HIGH})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = "PAS-6848")
    public void testCinNewBusiness(@Optional("CA") String state) {
        TestData policyTD = enhanceWithMVR(disableMemebership(getPolicyDefaultTD()));
        caNewBusinessMainFlow(policyTD);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }
}
