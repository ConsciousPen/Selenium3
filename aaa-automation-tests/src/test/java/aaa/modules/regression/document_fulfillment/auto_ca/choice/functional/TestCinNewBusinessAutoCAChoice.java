package aaa.modules.regression.document_fulfillment.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestCinAbstractAutoCA;
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
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = "PAS-6848")
    public void testCinNewBusiness(@Optional("CA") String state) {
        TestData policyTD = getPolicyDefaultTD()
                .adjust(PREFILL_TAB_FIRSTNAME, getTestSpecificTD("PrefillTabMVR").getValue("First Name"))
                .adjust(PREFILL_TAB_LASTNAME, getTestSpecificTD("PrefillTabMVR").getValue("Last Name"))
                .adjust(PRODUCT_OWNED_PATH, getTestSpecificTD("AAAProductOwned"));
        caNewBusinessMainFlow(policyTD);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }
}
