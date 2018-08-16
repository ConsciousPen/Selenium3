package aaa.modules.regression.document_fulfillment.home_ss.ho3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestCinAbstractHomeSS;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinRenewalHomeHO3 extends TestCinAbstractHomeSS {

    /**
     * @param state any except MD
     * @name CLUE trigger for HO Renewal(All States) CIN generation should be surpressed.
     * <p>
     * Depends on ChoicePointCluePropertyMockData mocksheet was updated with Applicant: First Name: PropChargeable Last Name: Activity
     * @scenario 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Renewal with Name Insured having chargeable CLUE property violation
     * 3. Check that CIN document is NOT getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-7278")
    public void testCluePropertyChargeableDriver(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD()
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership_CIN"))
                .adjust(MEMBERSHIP_REPORT_PATH, getTestSpecificTD("MembershipReport_CIN"));

        TestData renewalTD = getTestSpecificTD("TestData_Renewal")
                .adjust(APPLICANT_TAB_NAME_INSURED, getTestSpecificTD("AddNamedInsuredWithCluePropertyViolation").resolveLinks().getTestDataList("NamedInsured"))
                .adjust(REPORTS_TAB, getTestSpecificTD("Reorder_InsuranceScore_CLUEReport"));

        String policyNumber = createPolicy(policyTD);

        renewPolicy(policyNumber, renewalTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER)).isNull();
        });
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }
}
