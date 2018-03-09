package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import org.junit.Assert;
import toolkit.datax.TestData;

public class TestCinAbstractHomeCA extends TestCinAbstract{

    public static final String PRODUCT_OWNED_PATH = TestData.makeKeyPath(
            HomeCaMetaData.ApplicantTab.class.getSimpleName(),
            HomeCaMetaData.ApplicantTab.AAAMembership.class.getSimpleName());

    public static final String MEMBERSHIP_REPORT_PATH = TestData.makeKeyPath(
            HomeCaMetaData.ReportsTab.class.getSimpleName(),
            HomeCaMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());

    public static final String NAME_INSURED_FIRST_NAME = TestData.makeKeyPath(
            HomeCaMetaData.ApplicantTab.class.getSimpleName(),
            HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(),
            HomeCaMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel());

    public static final String NAME_INSURED_LAST_NAME = TestData.makeKeyPath(
            HomeCaMetaData.ApplicantTab.class.getSimpleName(),
            HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(),
            HomeCaMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel());

    public static final String PUBLIC_PROTECTION_CLASS_PATH = TestData.makeKeyPath(
            HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
            HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel());

    public static final String RENTAL_CLAIM_PATH = TestData.makeKeyPath(
            HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
            HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel(),
            HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM.getLabel());

    protected void mainFlow(TestData testData) {

        String policyNumber = createPolicy(testData);

        //wait for CIN specific form and a package itself to appear in the DB
        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

        Assert.assertNotNull(getPolicyErrorMessage("CIN document failed to generate", policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE), cinDocument);

        //ToDo: verify the order
    }
}
