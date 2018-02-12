package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.inject.internal.ImmutableList;
import com.google.inject.internal.ImmutableMap;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;
import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static aaa.main.enums.DocGenEnum.Documents.*;

public abstract class TestMaigConversionHomeAbstract extends PolicyBaseTest {

    private static final Map<AaaDocGenEntityQueries.EventNames, List<Job>> JOBS_FOR_EVENT =
            ImmutableMap.of(PRE_RENEWAL, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.aaaPreRenewalNoticeAsyncJob),
                    RENEWAL_OFFER, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.renewalOfferGenerationPart2));

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that HSPRNXX document is getting generated
     * @details
     */
    public void pas2305_preRenewalLetterHSPRNXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGeneration(getConversionPolicyTD(), HSPRNXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Creation converted policy for checking Pre-renewal letter
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data based on Test Data
     * 3. Check that form is getting generated with correct content
     * @details
     */
    private void preRenewalLetterFormGeneration(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
        String policyNumber = createPolicyForTD(testData);
        String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
                getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();

        processRenewal(PRE_RENEWAL, null, policyNumber);

        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
        verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
        verifyDocumentTagData(document, testData, isPupPresent);
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Initiate PUP conversion policy
     * 5. Check that HSPRNXX document is getting generated
     * @details
     */
    public void pas2305_preRenewalLetterPupConvHSPRNXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGenerationPup(getConversionPolicyTD(), HSPRNXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Creation converted policy for checking Pre-renewal letter
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data based on Test Data
     * 4. Initiate PUP conversion policy.
     * 3. Check that form is getting generated with correct content
     * @details
     */
    private void preRenewalLetterFormGenerationPup(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
        String policyNumber = createPolicyForTD(testData);
        String legacyPolicyNumber = createPolicyForTDPup();

        processRenewal(PRE_RENEWAL, null, policyNumber);

        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
        verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
        verifyDocumentTagData(document, testData, isPupPresent);
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with PUP added to OtherActiveAAAPolicies for Home
     * 4. Check that HSPRNXX document is getting generated with PUP section
     * @details
     */
    public void pas9170_preRenewalLetterPupHSPRNXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGeneration(adjustWithPupData(getConversionPolicyTD()), HSPRNXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Check that HSPRNMXX document is getting generated
     * @details
     */
    public void pas7342_preRenewalLetterHSPRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyTD()), HSPRNMXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan
     * 4. Initiate PUP conversion policy
     * 5. Check that HSPRNMXX document is getting generated
     * @details
     */
    public void pas7342_preRenewalLetterPupConvHSPRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyTD()), HSPRNMXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan and PUP added to OtherActiveAAAPolicies  for Home
     * 4. Check that HSPRNMXX document is getting generated
     * @details
     */
    public void pas9170_preRenewalLetterPupHSPRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        preRenewalLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyTD())), HSPRNMXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that HSRNHODPXX document is getting generated
     * @details
     */
    public void pas2309_renewalCoverLetterHSRNHODPXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGeneration(getConversionPolicyTD("TestData"), HSRNHODPXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Creation converted policy for checking Renewal Cover letter
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data based on Test Data
     * 3. Check that form is getting generated with correct content
     * @details
     */
    private void renewalCoverLetterFormGeneration(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
        String policyNumber = createPolicyForTD(testData);
        LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
        String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
                getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();

        processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
        verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
        verifyDocumentTagData(document, testData, isPupPresent);
    }

    /**
     * @name Creation converted policy for checking Renewal Cover letter
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data based on Test Data
     * 4. Initiate PUP conversion policy
     * 3. Check that form is getting generated with correct content
     * @details
     */
    private void renewalCoverLetterFormGenerationPup(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
        String policyNumber = createPolicyForTD(testData);
        LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
        String legacyPolicyNumber = createPolicyForTDPup();

        processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
        verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
        verifyDocumentTagData(document, testData, isPupPresent);
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for home
     * 4. Initiate PUP conversion policy
     * 5. Check that HSRNHODPXX document is getting generated
     * @details
     */
    public void pas2309_renewalCoverLetterPupConvHSRNHODPXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGenerationPup(getConversionPolicyTD("TestData"), HSRNHODPXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with PUP added to OtherActiveAAAPolicies for Home
     * 4. Check that HSRNHODPXX document is getting generated
     * @details
     */
    public void pas2309_renewalCoverLetterPupHSRNHODPXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGeneration(adjustWithPupData(getConversionPolicyTD()), HSRNHODPXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Check that HSRNMXX document is getting generated
     * @details
     */
    public void pas2570_renewalCoverLetterHSRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyTD()), HSRNMXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
     * 4. Initiate PUP conversion policy.
     * 5. Check that HSRNMXX document is getting generated
     * @details
     */
    public void pas2570_renewalCoverLetterPupConvHSRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyTD()), HSRNMXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @name Test Conversion Document generation (Renewal cover letter)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data with Mortgagee payment plan and PUP added to OtherActiveAAAPolicies for Home
     * 4. Check that HSRNMXX document is getting generated
     * @details
     */
    public void pas2570_renewalCoverLetterPupHSRNMXX(String state) throws NoSuchFieldException {
        CustomAssert.enableSoftMode();

        renewalCoverLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyTD())), HSRNMXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * Run needed renewal job based on event name for the document
     */
    private void processRenewal(AaaDocGenEntityQueries.EventNames eventName, LocalDateTime effectiveDate, String policyNumber) {
        SearchPage.openPolicy(policyNumber);
        ProductRenewalsVerifier productRenewalsVerifier = new ProductRenewalsVerifier();
        productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
        switch (eventName) {
            case PRE_RENEWAL:
                JOBS_FOR_EVENT.get(eventName).forEach(job -> JobUtils.executeJob(job));
                break;
            case RENEWAL_OFFER:
                LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(effectiveDate);
                TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
                JOBS_FOR_EVENT.get(eventName).forEach(job -> JobUtils.executeJob(job));
                mainApp().open();
                SearchPage.openPolicy(policyNumber);
                productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
                break;
            default:
                throw new IllegalArgumentException("Undefined eventName " + eventName.name());
        }
    }

    /**
     * Create conversion policy based on Test Data
     */
    private String createPolicyForTD(TestData testData) {
        mainApp().open();
        initiateManualConversionForTest("TestData_Home_MAIG");
        policy.getDefaultView().fillUpTo(testData, BindTab.class, false);
        policy.getDefaultView().getTab(BindTab.class).submitTab();
        return PolicySummaryPage.linkPolicy.getValue();
    }

    /**
     * Create conversion policy based on Test Data with linked PUP converted
     * ToDo: Refactor this after moving InitiateRenewalEntry to policy level
     */
    private String createPolicyForTDPup() {
        String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
                getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
        String sourceSystem = retrieveConversionImageTestData("TestData_Home_MAIG").getTestData("InitiateRenewalEntryActionTab").getValue("Previous Source System");
        TestData testDataPup = getCustomerIndividualTD("InitiateRenewalEntry", "TestData_Pup_Default")
                .adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.RISK_STATE.getLabel()), getState())
                .adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.PREVIOUS_SOURCE_SYSTEM.getLabel()), sourceSystem)
                .adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.PREVIOUS_POLICY_NUMBER.getLabel()), legacyPolicyNumber);
        initiateManualConversion(testDataPup);
        Tab.buttonSaveAndExit.click();
        return legacyPolicyNumber;
    }

    /**
     * Method to verify tags are present and contain specific values in Package
     * Note: Will be refactored after the refactoring of {@link DocGenHelper}
     *
     * @param legacyPolicyNumber
     * @param policyNumber
     * @param eventName
     */
    private void verifyPackageTagData(String legacyPolicyNumber, String policyNumber, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
        CustomAssert.assertTrue(MessageFormat.format("Problem is in tags: [{0}], [{1}]", "PlcyPrfx", "PlcyNum"), policyNumber
                .equals(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName)));
        CustomAssert.assertTrue(MessageFormat.format("Problem is in tag: [{0}]", "HdesPlcyNum"), legacyPolicyNumber
                .equals(getPackageTag(policyNumber, "HdesPlcyNum", eventName).replaceAll("-", "")));
    }

    /**
     * Method to verify tags are present and contain specific values in Document
     * Note: Will be refactored after the refactoring of {@link DocGenHelper}
     *
     * @param document
     * @param testData
     * @param isPupPresent
     */
    private void verifyDocumentTagData(Document document, TestData testData, boolean isPupPresent) throws NoSuchFieldException {
        if (isPupPresent) {
            verifyTagData(document, "PupCvrgYN", "Y");
        } else {
            verifyTagData(document, "PupCvrgYN", "N");
        }
        if ("Yes".equals(testData.getTestData("MortgageesTab").getValue("Mortgagee"))) {
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
    private String getPackageTag(String policyNumber, String tag, AaaDocGenEntityQueries.EventNames name) throws NoSuchFieldException {
        return getPackageDataElemByName(policyNumber, "PolicyDetails", tag, name);
    }

    /**
     * Utility method that enhances Conversion {@link TestData} with Mortgagee info
     */
    private TestData adjustWithMortgageeData(TestData policyTD) {
        //adjust TestData with Mortgagee tab data
        String mortgageeTabKey = TestData.makeKeyPath(HomeSSMetaData.MortgageesTab.class.getSimpleName());
        TestData mortgageeTD = getConversionPolicyTD("MortgageesTab");
        //adjust TestData with Premium and Coverage tab data
        String premiumAndCoverageTabKey = TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName());
        TestData premiumAndCoverageTD = getConversionPolicyTD("PremiumsAndCoveragesQuoteTab_Mortgagee");
        return policyTD.adjust(mortgageeTabKey, mortgageeTD).adjust(premiumAndCoverageTabKey, premiumAndCoverageTD);
    }

    /**
     * Utility method that enhances Conversion {@link TestData} with PUP in OtherActiveAAAPolicies
     */
    private TestData adjustWithPupData(TestData policyTD) {
        TestData pupTD = getConversionPolicyTD("OtherActiveAAAPolicies");
        String pupOtherActiveAAAPoliciesTabKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        return policyTD.adjust(pupOtherActiveAAAPoliciesTabKey, pupTD);
    }

}
