package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.AutoCaPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.junit.Assert;
import toolkit.datax.TestData;

import java.time.LocalDateTime;

public class TestCinAbstractAutoCA extends TestCinAbstract {
    //CA Path Constants
    public static final String PRODUCT_OWNED_PATH = TestData.makeKeyPath(
            AutoCaMetaData.GeneralTab.class.getSimpleName(),
            AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

    public static final String PREFILL_TAB_FIRSTNAME = TestData.makeKeyPath(
            AutoCaMetaData.PrefillTab.class.getSimpleName()+"[0]",
            AutoCaMetaData.PrefillTab.FIRST_NAME.getLabel());

    public static final String PREFILL_TAB_LASTNAME = TestData.makeKeyPath(
            AutoCaMetaData.PrefillTab.class.getSimpleName()+"[0]",
            AutoCaMetaData.PrefillTab.LAST_NAME.getLabel());

    public static final String DOCUMENTS_AND_BIND_PATH = TestData.makeKeyPath(
            AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName());

    public static final String DRIVER_ACTIVITY_REPORTS_PATH = TestData.makeKeyPath(
            AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName());


    public void renewPolicy(String policyNumber, TestData renewalTD) {
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        new AutoCaPolicyActions.Renew().performAndFill(renewalTD);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String renewedPolicyNumber = PolicySummaryPage.getPolicyNumber();

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, renewedPolicyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);

        Assert.assertNotNull(getPolicyErrorMessage("CIN document failed to generate", renewedPolicyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE), cinDocument);
    }

    protected void caNewBusinessMainFlow(TestData testData) {

        String policyNumber = createPolicy(testData);

        //wait for CIN specific form and a package itself to appear in the DB
        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

        Assert.assertNotNull(getPolicyErrorMessage("CIN document failed to generate", policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE), cinDocument);

        //ToDo: verify the order
    }

    /*******************************
     *
     * Utility methods for AutoCA
     *
     *******************************/

    public TestData disableMemebership(TestData testData) {
        return testData.adjust(PRODUCT_OWNED_PATH, getTestSpecificTD("AAAProductOwned"));
    }

    public TestData enhanceWithMVR(TestData testData) {
        String firstName = getTestSpecificTD("PrefillTabMVR").getValue("First Name");
        String lastName = getTestSpecificTD("PrefillTabMVR").getValue("Last Name");

        return testData.adjust(PREFILL_TAB_FIRSTNAME, firstName)
                .adjust(PREFILL_TAB_LASTNAME, lastName);
    }

    public TestData enhanceWithCLUE(TestData testData) {
        return testData.adjust(PRODUCT_OWNED_PATH, getTestSpecificTD("AAAProductOwned"));
    }

    public TestData overrideDocumentsAndBind(TestData testData) {
        return testData.adjust(DOCUMENTS_AND_BIND_PATH, getTestSpecificTD("DocumentsAndBindTab"));
    }

    public TestData overrideDriverActivityReports(TestData testData) {
        return testData.adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"));
    }

}
