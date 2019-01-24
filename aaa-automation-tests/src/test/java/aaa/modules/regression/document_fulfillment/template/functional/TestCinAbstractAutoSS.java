package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.AutoSSPolicyActions;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class TestCinAbstractAutoSS extends TestCinAbstract {

    /******* This section should be refactored once generic solution for building paths is implemented ********/
    //Auto SS specific paths
    protected static final String DISABLE_MEMBERSHIP = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());

    protected static final String RATING_DETAILS_REPORTS_TAB = TestData.makeKeyPath(
            AutoSSMetaData.RatingDetailReportsTab.class.getSimpleName());

    protected static final String SUPPRESS_PRIOR_BI_TRIGGER = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
            AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel());

    protected static final String DRIVER_ACTIVITY_REPORTS_PATH = TestData.makeKeyPath(
            AutoSSMetaData.DriverActivityReportsTab.class.getSimpleName());

    protected static final String NAME_INSURED_FIRST_NAME = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
            AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel());

    protected static final String NAME_INSURED_LAST_NAME = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
            AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel());

    protected static final String INSURANCE_SCORE_OVERRIDE = TestData.makeKeyPath(
            AutoSSMetaData.RatingDetailReportsTab.class.getSimpleName(),
            AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel());

    protected static final String CURRENT_CARRIER_INFORMATION = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());

    protected static final String REQUIRED_TO_ISSUE = TestData.makeKeyPath(
            AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(),
            AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel());

    protected static final String ADJUST_ELC = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
            AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE.getLabel());

    protected static final String ERROR_TAB_CALCULATE_PREMIUM = TestData.makeKeyPath(
            AutoSSMetaData.ErrorTabCalculatePremium.class.getSimpleName());

    protected static final String ERROR_TAB = TestData.makeKeyPath(
            AutoSSMetaData.ErrorTab.class.getSimpleName());

    protected static final String NAMED_INSURED_OVERRIDE = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.NamedInsuredInformation.class.getSimpleName());
    /**********************************************************************************************************/

    /**
     * Adjust Named Insured {@link TestData} with First&Last name. The reason why this method is here is that most of mappings in
     * the stub service are done using exactly those fields.
     *
     * @param testData {@link TestData}
     */
    public TestData adjustNameInsured(TestData testData, String nameInsuredTestData) {
        TestData nameInsuredTD = getTestSpecificTD(nameInsuredTestData);

        String firstName = nameInsuredTD.getValue("First Name");
        String lastName = nameInsuredTD.getValue("Last Name");

        return testData.adjust(NAME_INSURED_FIRST_NAME, firstName)
                .adjust(NAME_INSURED_LAST_NAME, lastName);
    }


    @Override
    protected void performRenewal(TestData renewalTD) {
        new AutoSSPolicyActions.Renew().performAndFill(renewalTD);
    }

    @Override
    protected void performDoNotRenew(TestData doNotRenewTD) {
    	new AutoSSPolicyActions.DoNotRenew().perform(doNotRenewTD);
    }
    
    @Override
    protected void performRemoveDoNotRenew() {
    	new AutoSSPolicyActions.RemoveDoNotRenew().perform(new SimpleDataProvider());
    }
}
