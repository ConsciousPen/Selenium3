package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.MaigManualConversionHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.inject.internal.ImmutableList;
import com.google.inject.internal.ImmutableMap;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;
import static aaa.main.enums.DocGenEnum.Documents.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class TestMaigConversionHomeTemplate extends PolicyBaseTest {
	private MaigManualConversionHelper maigManualConversionHelper = new MaigManualConversionHelper();

    private static final Map<AaaDocGenEntityQueries.EventNames, List<Job>> JOBS_FOR_EVENT =
            ImmutableMap.of(PRE_RENEWAL, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.aaaPreRenewalNoticeAsyncJob),
                    RENEWAL_OFFER, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.renewalOfferGenerationPart2));

    MaigManualConversionHelper manualConvHelper = new MaigManualConversionHelper();

	public void verifyHo3FormsSequence(TestData testData,LocalDateTime effDate){
		String policyNumber = createManualConversionRenewalEntry(testData, effDate);

		processRenewal(AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER, effDate, policyNumber);

		List<Document> actualDocumentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		assertThat(actualDocumentsList).isNotEmpty().isNotNull();

		if(Constants.States.NJ.equals(getState())){
			maigManualConversionHelper.verifyFormSequence(maigManualConversionHelper.getHO3NJForms(), actualDocumentsList);
		}
		else{
			maigManualConversionHelper.verifyFormSequence(maigManualConversionHelper.getHO3OtherStatesForms(), actualDocumentsList);
		}

	}


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

        preRenewalLetterFormGeneration(getConversionPolicyDefaultTD(), HSPRNXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
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

        preRenewalLetterFormGenerationPup(getConversionPolicyDefaultTD(), HSPRNXX, true);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
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

        preRenewalLetterFormGeneration(adjustWithPupData(getConversionPolicyDefaultTD()), HSPRNXX, true);

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

        preRenewalLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSPRNMXX, false);

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

        preRenewalLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSPRNMXX, true);

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

        preRenewalLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyDefaultTD())), HSPRNMXX, true);

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

        renewalCoverLetterFormGeneration(getConversionPolicyDefaultTD(), HSRNHODPXX, false);

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
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

        renewalCoverLetterFormGenerationPup(getConversionPolicyDefaultTD(), HSRNHODPXX, true);

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

        renewalCoverLetterFormGeneration(adjustWithPupData(getConversionPolicyDefaultTD()), HSRNHODPXX, true);

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

        renewalCoverLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRNMXX, false);

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

        renewalCoverLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRNMXX, true);

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

        renewalCoverLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyDefaultTD())), HSRNMXX, true);

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
    protected void preRenewalLetterFormGeneration(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
        String policyNumber = createManualConversionRenewalEntry(testData);
        String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
                getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();

        processRenewal(PRE_RENEWAL, null, policyNumber);

        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
        manualConvHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
        manualConvHelper.verifyDocumentTagData(document, testData, isPupPresent);
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
    protected void preRenewalLetterFormGenerationPup(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
        String policyNumber = createManualConversionRenewalEntry(testData);
        String legacyPolicyNumber = createPolicyForTDPup();

        processRenewal(PRE_RENEWAL, null, policyNumber);

        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
        manualConvHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
        manualConvHelper.verifyDocumentTagData(document, testData, isPupPresent);
    }

    /**
     * @name Creation converted policy for checking Renewal Cover letter
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data based on Test Data
     * 3. Check that form is getting generated with correct content
     * @details
     */
    protected void renewalCoverLetterFormGeneration(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
        String policyNumber = createManualConversionRenewalEntry(testData);
        LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
        String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
                getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();

        processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
        manualConvHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
        manualConvHelper.verifyDocumentTagData(document, testData, isPupPresent);
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
    protected void renewalCoverLetterFormGenerationPup(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
        String policyNumber = createManualConversionRenewalEntry(testData);
        LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
        String legacyPolicyNumber = createPolicyForTDPup();

        processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
        manualConvHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
        manualConvHelper.verifyDocumentTagData(document, testData, isPupPresent);
    }

    /**
     * Create conversion policy based on Test Data
     */
    private String createManualConversionRenewalEntry(TestData testData) {

        LocalDateTime renewalOfferEffectiveDate = getTimePoints().getEffectiveDateForTimePoint(
                TimeSetterUtil.getInstance().getCurrentTime(), TimePoints.TimepointsList.RENEW_GENERATE_OFFER).plusDays(5);

        mainApp().open();
        createCustomerIndividual();
        createManualConversion(testData, renewalOfferEffectiveDate);
        return PolicySummaryPage.getPolicyNumber();
    }

    public String createManualConversionRenewalEntry(TestData testData, LocalDateTime renewalOfferEffectiveDate) {
        mainApp().open();
        createCustomerIndividual();
        createManualConversion(testData, renewalOfferEffectiveDate);
        return PolicySummaryPage.getPolicyNumber();
    }

    public void createManualConversion(TestData testData, LocalDateTime renewalOfferEffectiveDate) {
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalOfferEffectiveDate);
        policy.getDefaultView().fillUpTo(testData, BindTab.class, false);
        Tab.buttonSaveAndExit.click();
       // policy.getDefaultView().getTab(BindTab.class).submitTab();
    }

    /**
     * Create conversion policy based on Test Data with linked PUP converted
     * ToDo: Refactor this after moving InitiateRenewalEntry to policy level
     */
    private String createPolicyForTDPup() {
        String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
                getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
        TestData testDataPup = getManualConversionInitiationTd()
                .adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.PREVIOUS_POLICY_NUMBER.getLabel()), legacyPolicyNumber);
        createCustomerIndividual();
        customer.initiateRenewalEntry().perform(testDataPup);
        Tab.buttonSaveAndExit.click();
        return legacyPolicyNumber;
    }

    /**
     * Run needed renewal job based on event name for the document
     */
    public void processRenewal(AaaDocGenEntityQueries.EventNames eventName, LocalDateTime effectiveDate, String policyNumber) {
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
     * Utility method that enhances Conversion {@link TestData} with Mortgagee info
     */
    public TestData adjustWithMortgageeData(TestData policyTD) {
        //adjust TestData with Mortgagee tab data
        String mortgageeTabKey = TestData.makeKeyPath(HomeSSMetaData.MortgageesTab.class.getSimpleName());
        TestData mortgageeTD = getTestSpecificTD("MortgageesTab");
        //adjust TestData with Premium and Coverage tab data
        String premiumAndCoverageTabKey = TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName());
        TestData premiumAndCoverageTD = getTestSpecificTD("PremiumsAndCoveragesQuoteTab_Mortgagee");
        return policyTD.adjust(mortgageeTabKey, mortgageeTD).adjust(premiumAndCoverageTabKey, premiumAndCoverageTD);
    }

    /**
     * Utility method that enhances Conversion {@link TestData} with PUP in OtherActiveAAAPolicies
     */
    public TestData adjustWithPupData(TestData policyTD) {
        TestData pupTD = getTestSpecificTD("OtherActiveAAAPolicies").resolveLinks();
        String pupOtherActiveAAAPoliciesTabKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        return policyTD.adjust(pupOtherActiveAAAPoliciesTabKey, pupTD);
    }

    public TestData adjustWithSeniorInsuredData(TestData policyTD){
        String newDateOfBirth = "12/05/1942";
        String insuredDOBPath = TestData.makeKeyPath(new ApplicantTab().getMetaKey(), HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel());
        return policyTD.adjust(insuredDOBPath, newDateOfBirth);
    }


    public  void pas2674_formsPresenceAndSequence(TestData testData, List<String> expectedFormsOrder){

        if (getState().equals("NJ")){
            testData = adjustWithSeniorInsuredData(testData);
        }

        LocalDateTime effDate = getTimePoints().getEffectiveDateForTimePoint(TimePoints.TimepointsList.RENEW_GENERATE_OFFER);

        String policyNumber = createManualConversionRenewalEntry(testData);
        processRenewal(AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER, effDate, policyNumber);

        List<Document> actualDocumentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
        manualConvHelper.verifyFormSequence(expectedFormsOrder, actualDocumentsList);
    }

//    protected void verifyFormsPresenceAndSequence (TestData testData, List<String> expectedFormsOrder){
//
//        LocalDateTime effDate = getTimePoints().getEffectiveDateForTimePoint(TimePoints.TimepointsList.RENEW_GENERATE_OFFER);
//
//        String policyNumber = createManualConversionRenewalEntry(testData);
//        processRenewal(AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER, effDate, policyNumber);
//
//        List<Document> actualDocumentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
//        manualConvHelper.verifyFormSequence(expectedFormsOrder, actualDocumentsList);
//    }
//
//
//    public void pas2674_test(String state){
//        TestData testData;
//        if (getState().equals("NJ")){
//            testData = adjustWithSeniorInsuredData(getConversionPolicyDefaultTD());
//        }
//        else{
//            testData = getConversionPolicyDefaultTD();
//        }
//        test(testData, HSPRNXX, false);
//    }
//
//    protected void test(TestData testData, DocGenEnum.Documents form, boolean isPupPresent){
//        String policyNumber = createManualConversionRenewalEntry(testData);
//        String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
//                getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
//        LocalDateTime effDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(45);
//
//
//        processRenewal(RENEWAL_OFFER, effDate, policyNumber);
//
//        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
//                verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
//                verifyDocumentTagData(document, testData, isPupPresent);
//    }
}
