package aaa.modules.regression.document_fulfillment.home_ca.ho3.functional;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestCinAbstractHomeCA;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinNewBusinessHomeCAHO3 extends TestCinAbstractHomeCA {

    /**
     * @author Pavel Mikhnevich
     * @name Test CIN Document generation (PROPERTY activity)
     * @scenario 1. Create Customer
     * 2. Create Policy with Applicant having chargeable CLUE property violation
     * 2.1. ChoicePointCluePropertyMockData mocksheet was updated with Applicant: First Name: PropChargeable Last Name: Activity
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(states = States.CA)
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = "PAS-7225")
    public void testCinNewBusinessProperty(@Optional("CA") String state) {
        TestData policyTD = getPolicyDefaultTD().adjust(PRODUCT_OWNED_PATH, getTestSpecificTD("AAAMembership_CIN"))
                .adjust(MEMBERSHIP_REPORT_PATH, getTestSpecificTD("MembershipReport_CIN"))
                .adjust(NAME_INSURED_FIRST_NAME, getTestSpecificTD("NamedInsuredProperty").getValue("First Name"))
                .adjust(NAME_INSURED_LAST_NAME, getTestSpecificTD("NamedInsuredProperty").getValue("Last Name"))
                .adjust(PUBLIC_PROTECTION_CLASS_PATH, getTestSpecificTD("PublicProtectionClass"));

        String policyNumber = createPolicy(policyTD);

        //wait for CIN specific form and a package itself to appear in the DB
        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }
}
