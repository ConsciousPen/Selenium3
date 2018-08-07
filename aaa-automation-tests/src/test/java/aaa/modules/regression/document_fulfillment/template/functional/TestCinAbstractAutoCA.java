package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.AutoCaPolicyActions;
import org.assertj.core.api.SoftAssertions;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class TestCinAbstractAutoCA extends TestCinAbstract {

    /******* This section should be refactored once generic solution for building paths is implemented ********/
    //Auto CA specific paths
    protected static final String PRODUCT_OWNED_PATH = TestData.makeKeyPath(
            AutoCaMetaData.GeneralTab.class.getSimpleName(),
            AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

    protected static final String PREFILL_TAB_FIRSTNAME = TestData.makeKeyPath(
            AutoCaMetaData.PrefillTab.class.getSimpleName(),
            AutoCaMetaData.PrefillTab.FIRST_NAME.getLabel());

    protected static final String PREFILL_TAB_LASTNAME = TestData.makeKeyPath(
            AutoCaMetaData.PrefillTab.class.getSimpleName(),
            AutoCaMetaData.PrefillTab.LAST_NAME.getLabel());

    protected static final String DOCUMENTS_AND_BIND_PATH = TestData.makeKeyPath(
            AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName());

    protected static final String DRIVER_ACTIVITY_REPORTS_PATH = TestData.makeKeyPath(
            AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName());
    /**********************************************************************************************************/

    /**
     * Create CA Auto policy based on the provided {@link TestData} and verify CIN document has been generated
     *
     * @param testData {@link TestData}
     */
    protected void caNewBusinessMainFlow(TestData testData) {
        String policyNumber = createPolicy(testData);
        //wait for CIN specific form and a package itself to appear in the DB
        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
        //ToDo: verify the order
    }

    @Override
    protected void performRenewal(TestData renewalTD) {
        new AutoCaPolicyActions.Renew().performAndFill(renewalTD);
    }
    
    @Override
    protected void performDoNotRenew(TestData doNotRenewTD) {
    	new AutoCaPolicyActions.Renew().perform(doNotRenewTD);
    }
    
    @Override
    protected void performRemoveDoNotRenew() {
    	new AutoCaPolicyActions.RemoveDoNotRenew().perform(new SimpleDataProvider());
    }

}
