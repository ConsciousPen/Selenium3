package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.HomeSSPolicyActions;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

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

    public static final String NAME_INSURED_FIRST_NAME = TestData.makeKeyPath(
            HomeSSMetaData.ApplicantTab.class.getSimpleName(),
            HomeSSMetaData.ApplicantTab.NamedInsured.class.getSimpleName(),
            HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel());

    public static final String NAME_INSURED_LAST_NAME = TestData.makeKeyPath(
            HomeSSMetaData.ApplicantTab.class.getSimpleName(),
            HomeSSMetaData.ApplicantTab.NamedInsured.class.getSimpleName(),
            HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel());

    public static final String INSURANCE_SCORE_OVERRIDE = TestData.makeKeyPath(
            HomeSSMetaData.ReportsTab.class.getSimpleName(),
            HomeSSMetaData.ReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel());


    /**********************************************************************************************************/

    /**
     * Adjust Named Insured {@link TestData} with First&Last name. The reason why this method is here is that most of mappings in
     * the stub service are done using exactly those fields.
     *
     * @param testData {@link TestData}
     */
    public TestData adjustNameInsured(TestData testData, String nameInsuredTestData) {
        TestData nameInsuredTD = getTestSpecificTD(nameInsuredTestData);

        String firstName = nameInsuredTD.getValue("First name");
        String lastName = nameInsuredTD.getValue("Last name");

        return testData.adjust(NAME_INSURED_FIRST_NAME, firstName)
                .adjust(NAME_INSURED_LAST_NAME, lastName);
    }

    @Override
    protected void performRenewal(TestData renewalTD) {
        new HomeSSPolicyActions.Renew().performAndFill(renewalTD);
    }
    
    @Override
    protected void performDoNotRenew(TestData doNotRenewTD) {
    	new HomeSSPolicyActions.DoNotRenew().perform(doNotRenewTD);
    }
    
    @Override
    protected void performRemoveDoNotRenew() {
    	new HomeSSPolicyActions.RemoveDoNotRenew().perform(new SimpleDataProvider());
    }
}
