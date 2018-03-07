package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import org.junit.Assert;
import toolkit.datax.TestData;

public class TestCinAbstractAutoSS extends TestCinAbstract{
    public static final String PRODUCT_OWNED_PATH = TestData.makeKeyPath(
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

    protected void ssNewBusinessMainFlow(TestData testData) {

        String policyNumber = createPolicy(testData);

        //wait for CIN specific form and a package itself to appear in the DB
        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

        Assert.assertNotNull(getPolicyErrorMessage("CIN document failed to generate", policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE), cinDocument);

        //ToDo: verify the order
    }
}
