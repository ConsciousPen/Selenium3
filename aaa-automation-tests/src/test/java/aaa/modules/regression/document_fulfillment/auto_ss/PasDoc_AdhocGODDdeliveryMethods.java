package aaa.modules.regression.document_fulfillment.auto_ss;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
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

        verifyOptionIsEnabled("eSignature", false, DocGenEnum.Documents.ACPUBI);
        verifyOptionIsEnabled("Email", true, DocGenEnum.Documents.ACPUBI);
        verifyOptionIsEnabled("Fax", false, DocGenEnum.Documents.ACPUBI);
        verifyOptionIsEnabled("Central Print", false, DocGenEnum.Documents.ACPUBI);
        verifyOptionIsEnabled("Local Print", true, true, DocGenEnum.Documents.ACPUBI);

        verifyOptionIsEnabled("eSignature", true, DocGenEnum.Documents.AA11AZ);
        verifyOptionIsEnabled("Email", true, DocGenEnum.Documents.AA11AZ);
        verifyOptionIsEnabled("Fax", false, DocGenEnum.Documents.AA11AZ);
        verifyOptionIsEnabled("Central Print", true, DocGenEnum.Documents.AA11AZ);
        verifyOptionIsEnabled("Local Print", true, true, DocGenEnum.Documents.AA11AZ);

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
        verifyOptionIsEnabled("eSignature", true, true, DocGenEnum.Documents.AA11AZ);
        documentActionTab.buttonCancel.click();
        //cancel policy2
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        policy.policyDocGen().start();
        verifyOptionIsEnabled("eSignature", true, false, DocGenEnum.Documents.AA11AZ);
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

        verifyOptionIsEnabled("eSignature", false, DocGenEnum.Documents.AA11AZ);
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

        //the expected result is a failure, because if one of the forms has delivery method - eSignature,
        // then it will be available for all forms
        verifyOptionIsEnabled("eSignature", false, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);
        verifyOptionIsEnabled("Fax", false, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);
        verifyOptionIsEnabled("Central Print", false, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);
        verifyOptionIsEnabled("Email", true, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);
        verifyOptionIsEnabled("Local Print", true, DocGenEnum.Documents.AAUBI, DocGenEnum.Documents.ACPUBI);

        documentActionTab.saveAndExit();
    }

//    @Parameters({"state"})
//    @StateList(states = Constants.States.AZ)
//    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
//    public void testScenario4(@Optional("") String state) {
//
//        mainApp().open();
//        createCustomerIndividual();
//        createPolicy();
//        log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
//        policy.policyDocGen().start();
//        verifyOptionIsEnabled("Email",true,DocGenEnum.Documents.AA11AZ);
//        documentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.Documents.AA11AZ);
//
//
//    }


//    @Parameters({"state"})
//    @StateList(states = Constants.States.AZ)
//    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
//    public void testScenario5(@Optional("") String state) {
//
//        mainApp().open();
//        createCustomerIndividual();
//        createPolicy();
//        log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
//        policy.policyDocGen().start();
//        verifyOptionIsEnabled("Email",true,DocGenEnum.Documents.AA11AZ);
//        documentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.EMAIL,null,null, DocGenEnum.Documents.ACPUBI);
//        documentActionTab.saveAndExit();
//    }
//
//    @Parameters({"state"})
//    @StateList(states = Constants.States.AZ)
//    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
//    public void testScenario6(@Optional("") String state) {
//
//        mainApp().open();
//        createCustomerIndividual();
//        createPolicy();
//        log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
//        policy.policyDocGen().start();
//        verifyOptionIsEnabled("Email",true,DocGenEnum.Documents.AA11AZ);
//        documentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.EMAIL,null,null, DocGenEnum.Documents.ACPUBI);
//        documentActionTab.saveAndExit();
//    }
//
//
//    @Parameters({"state"})
//    @StateList(states = Constants.States.AZ)
//    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
//    public void testScenario7(@Optional("") String state) {
//
//        mainApp().open();
//        createCustomerIndividual();
//        createPolicy();
//        log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
//        policy.policyDocGen().start();
//        verifyOptionIsEnabled("Email",true,DocGenEnum.Documents.AA11AZ);
//        documentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.EMAIL,null,null, DocGenEnum.Documents.ACPUBI);
//        documentActionTab.saveAndExit();
//    }
//
//    @Parameters({"state"})
//    @StateList(states = Constants.States.AZ)
//    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
//    public void testScenario8(@Optional("") String state) {
//
//        mainApp().open();
//        createCustomerIndividual();
//        createPolicy();
//        log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
//        policy.policyDocGen().start();
//        verifyOptionIsEnabled("Email",true,DocGenEnum.Documents.AA11AZ);
//        documentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.EMAIL,null,null, DocGenEnum.Documents.ACPUBI);
//        documentActionTab.saveAndExit();
//    }
//
//    @Parameters({"state"})
//    @StateList(states = Constants.States.AZ)
//    @Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
//    public void testScenario9(@Optional("") String state) {
//
//        mainApp().open();
//        createCustomerIndividual();
//        createPolicy();
//        log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
//        policy.policyDocGen().start();
//        verifyOptionIsEnabled("Email",true,DocGenEnum.Documents.AA11AZ);
//        documentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.EMAIL,null,null, DocGenEnum.Documents.ACPUBI);
//        documentActionTab.saveAndExit();
//    }


    private void verifyOptionIsEnabled(String radioBtnLabel, boolean unselectDocuments, boolean expectedValue, DocGenEnum.Documents... documents) {
        if (!unselectDocuments) documentActionTab.selectDocuments(documents);
        documentActionTab.selectDocuments(documents);
        if (expectedValue) {
            assertThat(documentActionTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD).
                    getRadioButton(radioBtnLabel).isEnabled()).isTrue();
        } else {
            assertThat(documentActionTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD).
                    getRadioButton(radioBtnLabel).isEnabled()).isFalse();
        }
        if (unselectDocuments) documentActionTab.unselectDocuments(documents);
    }

    private void verifyOptionIsEnabled(String radioBtnLabel, boolean expectedValue, DocGenEnum.Documents... documents) {
        verifyOptionIsEnabled(radioBtnLabel, false, expectedValue, documents);
    }
}
