package aaa.modules.regression.document_fulfillment.auto_ss;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

import static toolkit.verification.CustomAssertions.assertThat;

public class PasDoc_AdhocGODDdeliveryMethods extends AutoSSBaseTest {

    private GenerateOnDemandDocumentActionTab documentActionTab = new GenerateOnDemandDocumentActionTab();


    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
    public void testScenario1(@Optional("") String state) {

        mainApp().open();
        createCustomerIndividual();
        TestData td_1 = getPolicyTD().adjust(getTestSpecificTD("TestData_with_EnrolledInUBI_1").resolveLinks());
        createPolicy(td_1);
        log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
        policy.policyDocGen().start();

        CustomSoftAssertions.assertSoftly(softly -> {

            verifyOptionIsEnabled(softly, "eSignature", false, DocGenEnum.Documents.ACPUBI);
            verifyOptionIsEnabled(softly, "Email", true, DocGenEnum.Documents.ACPUBI);
            verifyOptionIsEnabled(softly, "Fax", false, DocGenEnum.Documents.ACPUBI);
            verifyOptionIsEnabled(softly, "Central Print", false, DocGenEnum.Documents.ACPUBI);
            verifyOptionIsEnabled(softly, "Local Print", true, true, DocGenEnum.Documents.ACPUBI);

            verifyOptionIsEnabled(softly, "eSignature", true, DocGenEnum.Documents.AA11AZ);
            verifyOptionIsEnabled(softly, "Email", true, DocGenEnum.Documents.AA11AZ);
            verifyOptionIsEnabled(softly, "Fax", false, DocGenEnum.Documents.AA11AZ);
            verifyOptionIsEnabled(softly, "Central Print", true, DocGenEnum.Documents.AA11AZ);
            verifyOptionIsEnabled(softly, "Local Print", true, true, DocGenEnum.Documents.AA11AZ);
        });

        documentActionTab.saveAndExit();
    }

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
    public void testScenario2(@Optional("") String state) {
        mainApp().open();
        createCustomerIndividual();
        //policy1
        String policy_declined = createPolicy();
        //policy2
        policy.copyPolicy(getPolicyTD("CopyFromPolicy", "TestData"));
        policy.policyDocGen().start();
        verifyOptionIsEnabled(null, "eSignature", true, true, DocGenEnum.Documents.AA11AZ);
        documentActionTab.buttonCancel.click();
        //cancel policy2
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        policy.policyDocGen().start();
        verifyOptionIsEnabled(null, "eSignature", true, false, DocGenEnum.Documents.AA11AZ);
        documentActionTab.buttonCancel.click();

        SearchPage.openPolicy(policy_declined);
        policy.endorse().performAndExit(getPolicyTD("Endorsement", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();
        policy.declineByCompanyQuote().perform(getPolicyTD("DeclineByCompany", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();
        assertThat(PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status"))
                .hasValue(ProductConstants.PolicyStatus.COMPANY_DECLINED);
        log.info("PAS DOC: Policy created with #" + policy_declined);
        policy.policyDocGen().start();

        verifyOptionIsEnabled(null, "eSignature", false, DocGenEnum.Documents.AA11AZ);
        documentActionTab.saveAndExit();
        //without verification delivery method - eSignature in policy3 (policy status - expired) - NOT APPLICABLE
    }

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
    public void testScenario3(@Optional("") String state) {

        mainApp().open();
        createCustomerIndividual();
        TestData td_1 = getPolicyTD().adjust(getTestSpecificTD("TestData_with_EnrolledInUBI_1").resolveLinks());
        createPolicy(td_1);
        log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
        policy.policyDocGen().start();


        CustomSoftAssertions.assertSoftly(softly -> {
            verifyOptionIsEnabled(softly, "eSignature", false, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);
            verifyOptionIsEnabled(softly, "Fax", false, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);
            verifyOptionIsEnabled(softly, "Central Print", false, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);
            verifyOptionIsEnabled(softly, "Email", true, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);
            verifyOptionIsEnabled(softly, "Local Print", true, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);
        });
        documentActionTab.saveAndExit();
    }

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
    public void testScenario4(@Optional("") String state) {
//        String content = RemoteHelper.get().getFileContent("/home/DocGen/pasdoc/outbound/AZSS952028460_1556182520435_4998987910801489950.xml");
//        XmlHelper.xmlToModel(content, DocumentGenerationRequest.class, false);
        PasDocImpl.verifyDocumentsGenerated("AZSS952028460", DocGenEnum.Documents.AA11AZ).getDistributionChannel();
        /*mainApp().open();*/


        System.out.println(" ");
//        ;
//        log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
//        policy.policyDocGen().start();
//        verifyOptionIsEnabled(null,"Email",true,DocGenEnum.Documents.AA11AZ);
//        documentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.CENTRAL_PRINT,  DocGenEnum.Documents.AA11AZ);
//
        //check distribution channel methods
    }

    private void verifyOptionIsEnabled(ETCSCoreSoftAssertions softly, String radioBtnLabel, boolean unselectDocuments, boolean expectedValue, DocGenEnum.Documents... documents) {
        if (!unselectDocuments) documentActionTab.selectDocuments(documents);
        documentActionTab.selectDocuments(documents);

        if (softly != null) {
            softly.assertThat(documentActionTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD).
                    getRadioButton(radioBtnLabel).isEnabled()).isEqualTo(expectedValue);
        } else {
            assertThat(documentActionTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD).
                    getRadioButton(radioBtnLabel).isEnabled()).isEqualTo(expectedValue);
        }


        if (unselectDocuments) documentActionTab.unselectDocuments(documents);
    }

    private void verifyOptionIsEnabled(ETCSCoreSoftAssertions softly, String radioBtnLabel, boolean expectedValue, DocGenEnum.Documents... documents) {
        verifyOptionIsEnabled(softly, radioBtnLabel, false, expectedValue, documents);
    }
}
