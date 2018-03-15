package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import org.junit.Assert;
import toolkit.datax.TestData;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertTrue;

public class TestCinAbstractAutoSS extends TestCinAbstract{
    public static final String DISABLE_MEMBERSHIP = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

    public static final String SUPPRESS_INSURANCE_SCORE_TRIGGER = TestData.makeKeyPath(
            AutoSSMetaData.RatingDetailReportsTab.class.getSimpleName(),
            AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel());

    public static final String SUPPRESS_PRIOR_BI_TRIGGER = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
            AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel());

    public static final String DRIVER_ACTIVITY_REPORTS_PATH = TestData.makeKeyPath(
            AutoSSMetaData.DriverActivityReportsTab.class.getSimpleName());

    public static final String NAME_INSURED_FIRST_NAME = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()+"[0]",
            AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel());

    public static final String NAME_INSURED_LAST_NAME = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()+"[0]",
            AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel());

    public static final String INSURANCE_SCORE_OVERRIDE = TestData.makeKeyPath(
            AutoSSMetaData.RatingDetailReportsTab.class.getSimpleName(),
            AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel());

    public static final String CURRENT_CARRIER_INFORMATION = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());

    public static final String REQUIRED_TO_ISSUE = TestData.makeKeyPath(
            AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(),
            AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel());

    public static final String ADJUST_ELC = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
            AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE.getLabel());

    public static final String ERROR_TAB_CALCULATE_PREMIUM = TestData.makeKeyPath(
            AutoSSMetaData.ErrorTabCalculatePremium.class.getSimpleName());

    public static final String ERROR_TAB = TestData.makeKeyPath(
            AutoSSMetaData.ErrorTab.class.getSimpleName());

    protected Document ssNewBusinessMainFlow(TestData testData) {

        String policyNumber = createPolicy(testData);

        //wait for CIN specific form and a package itself to appear in the DB
        return DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

        //ToDo: verify the order
    }

    protected void verifyCinGenerated (Document cinDocument, String policyNumber) {
        Assert.assertNotNull(getPolicyErrorMessage("CIN document failed to generate", policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE), cinDocument);
    }

    protected void verifyCinNotGenerated (Document cinDocument, String policyNumber) {
        Assert.assertNull(getPolicyErrorMessage("CIN document mustn't have been generated, but it's there", policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE), cinDocument);
    }

    public TestData adjustNameInsured(TestData testData, String nameInsuredTestData) {
        TestData nameInsuredTD = getTestSpecificTD(nameInsuredTestData);

        String firstName = nameInsuredTD.getValue("First Name");
        String lastName = nameInsuredTD.getValue("Last Name");

        return testData.adjust(NAME_INSURED_FIRST_NAME, firstName)
                .adjust(NAME_INSURED_LAST_NAME, lastName);
    }

    protected void assertStateEquals(String state, String... applicableStates) {
        assertTrue(asList(applicableStates).contains(state), "Test does not support this state: " + state);
    }

}
