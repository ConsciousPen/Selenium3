package aaa.modules.docgen.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static aaa.main.enums.DocGenEnum.Documents.HSPRNMXX;
import static aaa.main.enums.DocGenEnum.Documents.HSPRNXX;
import java.text.MessageFormat;
import org.testng.annotations.Optional;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.conversion.manual.ManualConversionUtil;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public abstract class TestMaigConversionHomeAbstract extends PolicyBaseTest{


    private int PRE_RENEWAL_LETTER_TIMELINE = 40;
    private BindTab bindTab = new BindTab();
    private PremiumsAndCoveragesQuoteTab premiumAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    private static final String POLICY_TYPE_PATH = TestData.makeKeyPath(
            CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(),
            CustomerMetaData.InitiateRenewalEntryActionTab.POLICY_TYPE.getLabel());


    public void pas2305_preRenewalLetterHSPRNXXProductSpecific(@Optional("VA") String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();
        mainApp().open();

        //populate individual test data, initiate renewal entry and fill data in all tabs
        initiateManualConversionForTest();
        fillConvertedPolicy(getConversionPolicyTD());

        //policy generated, proceed with PreRenewal
        String policyNumber = runPreRenewalJob();

        //check pre-renewal package
        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(HSPRNXX, policyNumber, "PRE_RENEWAL");

        //check tags in pre-renewal package
        tagsVerification (policyNumber, document, false,false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    public void pas2305_preRenewalLetterHSPRNMXXProductSpecific(@Optional("VA") String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();
        mainApp().open();

        //populate individual test data, initiate renewal entry and fill data in all tabs
        initiateManualConversionForTest();
        fillConvertedPolicy(getConversionPolicyWithMortgageeTD());

        //policy generated, proceed with PreRenewal
        String policyNumber = runPreRenewalJob();

        //check pre-renewal package
        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(HSPRNMXX, policyNumber, "PRE_RENEWAL");

        //check tags in pre-renewal package
        tagsVerification (policyNumber, document, false,true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private void tagsVerification(String policyNumber, Document document, boolean isPupPresent, boolean isMotgageePresent) throws NoSuchFieldException {
        CustomAssert.assertTrue(MessageFormat.format("Problem is in tags: [{0}], [{1}]", "PlcyPrfx", "PlcyNum"), policyNumber
                .equals(getPackageTag(policyNumber, "PlcyPrfx")+getPackageTag(policyNumber, "PlcyNum")));
        if(isPupPresent) {
            tagVerification(document,"PupCvrgYN", "Y");
        }
        else{
            tagVerification(document,"PupCvrgYN", "N");
        }
        if(isMotgageePresent) {
            tagVerification(document,"ThrdPrtyHdr", "TestName");
            tagVerification(document,"ThrdPrtyLnNum", "12345678");
        }
    }

    /**
     * Verify that tag value is present in the Documents section
     */
    private void tagVerification(Document document,String tag, String textFieldValue) {
        CustomAssert.assertTrue(MessageFormat.format("Problem is in tag: [{0}]", tag), textFieldValue
                .equals(DocGenHelper.getDocumentDataElemByName(tag, document).getDataElementChoice().getTextField()));
    }

    /**
     * Verify that tag value is present in the Package
     */
    private String getPackageTag(String policyNumber,String tag) throws NoSuchFieldException {
        return getPackageDataElemByName(policyNumber, "PolicyDetails", tag, PRE_RENEWAL);
    }

    private String runPreRenewalJob() {
        String policyNumber = PolicySummaryPage.linkPolicy.getValue();
        TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getEffectiveDate().minusDays(PRE_RENEWAL_LETTER_TIMELINE));
        JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);
        return policyNumber;
    }

    private void fillConvertedPolicy(TestData testData) {
        policy.getDefaultView().fillUpTo(testData, PremiumsAndCoveragesQuoteTab.class, true);
        if ("Mortgagee".equals(premiumAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.BILL_TO_AT_RENEWAL.getLabel()).getValue())){
            premiumAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue("Mortgagee Bill (Renewal)");
            premiumAndCoveragesQuoteTab.calculatePremium();
            premiumAndCoveragesQuoteTab.submitTab();
        }
        else{
            premiumAndCoveragesQuoteTab.submitTab();
        }
        policy.getDefaultView().fillFromTo(testData, MortgageesTab.class, BindTab.class);
        bindTab.submitTab();
    }


    /**
     * Initiate Customer Individual test data, fill the InitiateRenewalEntry
     * ToDo: find a more flexible way to define the short Product Name
     */
    private void initiateManualConversionForTest() {
        TestData customerTestData = getCustomerIndividualTD("InitiateRenewalEntry", "TestData_Home_MAIG")
                .adjust(POLICY_TYPE_PATH, ManualConversionUtil.getShortPolicyType(getPolicyType()));
        initiateManualConversion(customerTestData);
    }

    private TestData getConversionPolicyTD() {
        return getPolicyTD("Conversion", "TestData_ConversionHomeSS");
    }

    private TestData getConversionPolicyWithMortgageeTD() {
        TestData policyTD = getPolicyTD("Conversion", "TestData_ConversionHomeSS");
        //adjust TestData with Mortgagee tab data
        String mortgageeTabKey = TestData.makeKeyPath(new MortgageesTab().getMetaKey());
        TestData mortgageeTD = getPolicyTD("Conversion", "MortgageesTab_ConversionHomeSS");
        //adjust TestData with Premium and Coverage tab data
        String premiumAndCoverageTabKey = TestData.makeKeyPath(new PremiumsAndCoveragesQuoteTab().getMetaKey());
        TestData premiumAndCoverageTD = getPolicyTD("Conversion", "PremiumsAndCoveragesQuoteTab_Mortgagee_ConversionHomeSS");
        return policyTD.adjust(mortgageeTabKey, mortgageeTD).adjust(premiumAndCoverageTabKey,premiumAndCoverageTD);
    }
}
