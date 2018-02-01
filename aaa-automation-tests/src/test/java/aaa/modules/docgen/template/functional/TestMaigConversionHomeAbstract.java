package aaa.modules.docgen.template.functional;

import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import org.testng.annotations.Optional;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.text.MessageFormat;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static aaa.main.enums.DocGenEnum.Documents.HSPRNMXX;
import static aaa.main.enums.DocGenEnum.Documents.HSPRNXX;

public abstract class TestMaigConversionHomeAbstract extends PolicyBaseTest {

    /**
     * @name Test MAIG Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate MAIG Renewal Entry
     * 3. Fill Conversion Policy data
     * 3. Check that HSPRNXX document section is getting generated
     * @details
     */
    public void pas2305_preRenewalLetterHSPRNXXProductSpecific(@Optional("VA") String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();
        mainApp().open();

        //populate individual test data, initiate renewal entry and fill data in all tabs
        initiateManualConversionForTest("TestData_Home_MAIG");

        policy.getDefaultView().fillUpTo(getConversionPolicyTD(), BindTab.class, false);
        policy.getDefaultView().getTab(BindTab.class).submitTab();

        String policyNumber = PolicySummaryPage.linkPolicy.getValue();
        //policy generated, proceed with PreRenewal
        JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);

        //check pre-renewal package
        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(HSPRNXX, policyNumber, "PRE_RENEWAL");

        //check tags in pre-renewal package
        verifyTagData(policyNumber, document, false, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test MAIG Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate MAIG Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan
     * 3. Check that HSPRNMXX document section is getting generated
     * @details
     */
    public void pas2305_preRenewalLetterHSPRNMXXProductSpecific(@Optional("VA") String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();
        mainApp().open();

        //populate individual test data, initiate renewal entry and fill data in all tabs
        initiateManualConversionForTest("TestData_Home_MAIG");

        policy.getDefaultView().fillUpTo(getConversionPolicyWithMortgageeTD(), BindTab.class, false);
        policy.getDefaultView().getTab(BindTab.class).submitTab();

        String policyNumber = PolicySummaryPage.linkPolicy.getValue();
        //policy generated, proceed with PreRenewal
        JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);

        //check pre-renewal package
        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(HSPRNMXX, policyNumber, "PRE_RENEWAL");

        //check tags in pre-renewal package
        verifyTagData(policyNumber, document, false, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * Method to verify tags are present and contain specific values
     * Note: Will be refactored after the refactoring of {@link DocGenHelper}
     *
     * @param policyNumber
     * @param isPupPresent
     * @param isMotgageePresent
     */
    private void verifyTagData(String policyNumber, Document document, boolean isPupPresent, boolean isMotgageePresent) throws NoSuchFieldException {
        CustomAssert.assertTrue(MessageFormat.format("Problem is in tags: [{0}], [{1}]", "PlcyPrfx", "PlcyNum"), policyNumber
                .equals(getPackageTag(policyNumber, "PlcyPrfx") + getPackageTag(policyNumber, "PlcyNum")));
        if (isPupPresent) {
            verifyTagData(document, "PupCvrgYN", "Y");
        } else {
            verifyTagData(document, "PupCvrgYN", "N");
        }
        if (isMotgageePresent) {
            verifyTagData(document, "ThrdPrtyHdr", "TestName");
            verifyTagData(document, "ThrdPrtyLnNum", "12345678");
        }
    }

    /**
     * Verify that tag value is present in the Documents section
     */
    private void verifyTagData(Document document, String tag, String textFieldValue) {
        CustomAssert.assertTrue(MessageFormat.format("Problem is in tag: [{0}]", tag), textFieldValue
                .equals(DocGenHelper.getDocumentDataElemByName(tag, document).getDataElementChoice().getTextField()));
    }

    /**
     * Verify that tag value is present in the Package
     */
    private String getPackageTag(String policyNumber, String tag) throws NoSuchFieldException {
        return getPackageDataElemByName(policyNumber, "PolicyDetails", tag, PRE_RENEWAL);
    }

    /**
     * Utility method that enhances Conversion {@link TestData} with Mortgagee info
     */
    private TestData getConversionPolicyWithMortgageeTD() {
        TestData policyTD = getPolicyTD("Conversion", "TestData_ConversionHomeSS");
        //adjust TestData with Mortgagee tab data
        String mortgageeTabKey = TestData.makeKeyPath(new MortgageesTab().getMetaKey());
        TestData mortgageeTD = getPolicyTD("Conversion", "MortgageesTab_ConversionHomeSS");
        //adjust TestData with Premium and Coverage tab data
        String premiumAndCoverageTabKey = TestData.makeKeyPath(new PremiumsAndCoveragesQuoteTab().getMetaKey());
        TestData premiumAndCoverageTD = getPolicyTD("Conversion", "PremiumsAndCoveragesQuoteTab_Mortgagee_ConversionHomeSS");
        return policyTD.adjust(mortgageeTabKey, mortgageeTD).adjust(premiumAndCoverageTabKey, premiumAndCoverageTD);
    }
}
