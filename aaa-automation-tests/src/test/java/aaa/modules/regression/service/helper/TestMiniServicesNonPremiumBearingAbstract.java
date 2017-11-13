package aaa.modules.regression.service.helper;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import toolkit.db.DBService;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesNonPremiumBearingAbstract extends PolicyBaseTest {

    protected abstract String getGeneralTab();
    protected abstract String getPremiumAndCoverageTab();
    protected abstract String getDocumentsAndBindTab();

    protected abstract Tab getGeneralTabElement();
    protected abstract Tab getPremiumAndCoverageTabElement();
    protected abstract Tab getDocumentsAndBindTabElement();

    protected abstract AssetDescriptor<Button> getCalculatePremium();



    private void emailAddressChangedInEndorsementCheck(String emailAddressChanged) {
        policy.policyInquiry().start();

        getGeneralTabElement().getInquiryAssetList().getStaticElement("Email").verify.value(emailAddressChanged);
        NavigationPage.toViewTab(getDocumentsAndBindTab());

        if(getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement("Email").isPresent()) {
            getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement("Email").verify.value(emailAddressChanged);
        }
        Tab.buttonCancel.click();
    }

    private void secondEndorsementIssueCheck() {
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(getPremiumAndCoverageTab());
        getPremiumAndCoverageTabElement().getAssetList().getAsset(getCalculatePremium()).click();
        getPremiumAndCoverageTabElement().saveAndExit();


        TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
        testEValueDiscount.simplifiedPendedEndorsementIssue();
        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
    }

    protected void pas1441_emailChangeOutOfPasTestBody(PolicyType policyType){
        HelperCommon helperCommon = new HelperCommon();

        mainApp().open();
        createCustomerIndividual();
        policyType.get().createPolicy(getPolicyTD());
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        //BUG PAS-5815 There is an extra Endorse action available for product
        NavigationPage.comboBoxListAction.verify.noOption("Endorse");

        //PAS-343 start
        String numberOfDocumentsRecordsInDbQuery = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "%%", "%%");
        int numberOfDocumentsRecordsInDb = Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get());
        //PAS-343 end


        String emailAddressChanged = "osi.test@email.com";
        helperCommon.executeRequest(policyNumber, emailAddressChanged);

        helperCommon.emailUpdateTransactionHistoryCheck(policyNumber);
        emailAddressChangedInEndorsementCheck(emailAddressChanged);

        //PAS-343 start
        CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get()), numberOfDocumentsRecordsInDb);
        //PAS-343 end

        secondEndorsementIssueCheck();
    }

}
