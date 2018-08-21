package aaa.modules.regression.document_fulfillment.home_ss.ho4.functional;

import java.util.Arrays;
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
import aaa.modules.regression.document_fulfillment.template.functional.TestCinAbstractHomeSS;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinNewBusinessHomeHO4 extends TestCinAbstractHomeSS {

    /**
     * @name Test CIN Document generation (PROPERTY activity)
     *
     * Depends on ChoicePointCluePropertyMockData mocksheet was updated with Applicant: First Name: PropChargeable Last Name: Activity
     *
     * @scenario
     * 1. Create Customer
     * 2. Create Policy with Applicant having chargeable CLUE property violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-7278")
    public void testCinHomeSSCluePropertyViolation(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NamedInsured_CluePropertyViolation")
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership_CIN"))
                .adjust(MEMBERSHIP_REPORT_PATH, getTestSpecificTD("MembershipReport_CIN"));

        if(!Arrays.asList("MD").contains(state)) {
            policyTD.adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_940"));
        }

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO4;
    }
}
