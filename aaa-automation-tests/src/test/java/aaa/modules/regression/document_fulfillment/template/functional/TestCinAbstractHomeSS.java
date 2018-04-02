package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.HomeSSPolicyActions;
import toolkit.datax.TestData;

public class TestCinAbstractHomeSS extends TestCinAbstract {

    /******* This section should be refactored once generic solution for building paths is implemented ********/
    //Home SS specific paths
    public static final String DISABLE_MEMBERSHIP = TestData.makeKeyPath(
            HomeSSMetaData.ApplicantTab.class.getSimpleName(),
            HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());

    public static final String MEMBERSHIP_REPORT_PATH = TestData.makeKeyPath(
            HomeSSMetaData.ReportsTab.class.getSimpleName(),
            HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());

    public static final String APPLICANT_TAB_NAME_INSURED = TestData.makeKeyPath(
            HomeSSMetaData.ApplicantTab.class.getSimpleName(),
            HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel());

    public static final String REPORTS_TAB = TestData.makeKeyPath(
            HomeSSMetaData.ReportsTab.class.getSimpleName());

    /**********************************************************************************************************/

    @Override
    protected void performRenewal(TestData renewalTD) {
        new HomeSSPolicyActions.Renew().performAndFill(renewalTD);
    }
}
