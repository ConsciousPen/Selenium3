package aaa.modules.regression.document_fulfillment.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.*;
import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static aaa.main.enums.DocGenEnum.Documents.*;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.inject.internal.ImmutableList;
import com.google.inject.internal.ImmutableMap;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
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
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public abstract class TestMaigConversionHomeAbstract extends PolicyBaseTest {

	private static final Map<AaaDocGenEntityQueries.EventNames, List<Job>> JOBS_FOR_EVENT =
			ImmutableMap.of(PRE_RENEWAL, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.aaaPreRenewalNoticeAsyncJob, Jobs.aaaDocGenBatchJob),
					RENEWAL_OFFER, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.renewalOfferGenerationPart2, Jobs.aaaDocGenBatchJob),
					RENEWAL_BILL, ImmutableList.of(Jobs.aaaRenewalNoticeBillAsyncJob, Jobs.aaaDocGenBatchJob),
					BILL_FIRST_RENEW_REMINDER_NOTICE, ImmutableList.of(Jobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob, Jobs.aaaDocGenBatchJob),
					MORTGAGEE_BILL_FINAL_EXP_NOTICE, ImmutableList.of(Jobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob, Jobs.aaaDocGenBatchJob));

	ProductRenewalsVerifier productRenewalsVerifier = new ProductRenewalsVerifier();

	/**
	 * @name Test Conversion Document generation (Pre-renewal package)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that HSPRNXX document is getting generated
	 * @details
	 */
	public void pas2305_preRenewalLetterHSPRNXX(String state) throws NoSuchFieldException {
		preRenewalLetterFormGeneration(getConversionPolicyDefaultTD(), HSPRNXX, false);
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
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		preRenewalJobExecution(effectiveDate, policyNumber);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
		verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
		verifyRenewalDocumentTagData(document, testData, isPupPresent, PRE_RENEWAL);
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
		preRenewalLetterFormGenerationPup(getConversionPolicyDefaultTD(), HSPRNXX, true);
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
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		String legacyPolicyNumber = createPolicyForTDPup();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		preRenewalJobExecution(effectiveDate, policyNumber);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
		verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
		verifyRenewalDocumentTagData(document, testData, isPupPresent, PRE_RENEWAL);
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
		preRenewalLetterFormGeneration(adjustWithPupData(getConversionPolicyDefaultTD()), HSPRNXX, true);
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
		preRenewalLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSPRNMXX, false);
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
		preRenewalLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSPRNMXX, true);
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
		preRenewalLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyDefaultTD())), HSPRNMXX, true);
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
		renewalCoverLetterFormGeneration(getConversionPolicyDefaultTD(), HSRNHODPXX, false);
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
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		renewalOfferCoverLetterJobExecution(effectiveDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		assertThat(organicDocument).isEqualTo(null);
		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
		verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
		verifyRenewalDocumentTagData(document, testData, isPupPresent, RENEWAL_OFFER);
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
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		renewalOfferCoverLetterJobExecution(effectiveDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		assertThat(organicDocument).isEqualTo(null);
		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
		verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
		verifyRenewalDocumentTagData(document, testData, isPupPresent, RENEWAL_OFFER);
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
		renewalCoverLetterFormGenerationPup(getConversionPolicyDefaultTD(), HSRNHODPXX, true);

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
		renewalCoverLetterFormGeneration(adjustWithPupData(getConversionPolicyDefaultTD()), HSRNHODPXX, true);
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
		renewalCoverLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRNMXX, false);
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
		renewalCoverLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRNMXX, true);
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
		renewalCoverLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyDefaultTD())), HSRNMXX, true);
	}

	/**
	 * @name Test Conversion Document generation (Renewal cover letter)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for PUP
	 * 4. Check that HSRNPUPXX document is getting generated
	 * @details
	 */
	public void pas2571_renewalCoverLetterHSRNPUPXX(String state, PolicyType policyType) throws NoSuchFieldException {
		renewalCoverLetterFormGenerationConvPup(HSRNPUPXX, policyType);
	}

	/**
	 * @name Creation converted policy for checking Renewal Cover letter
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 4. Check that form is getting generated with correct content
	 * @details
	 */
	private void renewalCoverLetterFormGenerationConvPup(DocGenEnum.Documents form, PolicyType policyType) throws NoSuchFieldException {
		String policyNumber = createPolicyPupConvForTD(policyType);
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		log.info("Conversion PUP policy number: " + policyNumber);

		renewalOfferCoverLetterJobExecution(effectiveDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		assertThat(organicDocument).isEqualTo(null);
		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
		verifyTagDataPup(document, policyNumber, policyType, RENEWAL_OFFER);
	}


	/**
	 * @name Test Conversion Document generation (Insurance Renewal Bill)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that AHRBXX document is getting generated
	 * @details
	 */
	public void pas8789_insuranceRenewalBillHomeAHRBXX(String state) throws NoSuchFieldException {
		insuranceRenewalBillHomeGeneration(getConversionPolicyDefaultTD(), AHRBXX, false);
	}

	/**
	 * @name Test Conversion Document generation (Insurance Renewal Bill)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
	 * 4. Check that AHRBXX document is getting generated
	 * @details
	 */
	public void pas10241_insuranceRenewalBillHomeMortAHRBXX(String state) throws NoSuchFieldException {
		insuranceRenewalBillHomeGeneration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), AHRBXX, false);
	}

	/**
	 * @name Test Conversion Document generation (Insurance Renewal Bill)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for PUP
	 * 4. Check that AHRBXX document is getting generated
	 * @details
	 */
	public void pas8789_insuranceRenewalBillPupAHRBXX(String state, PolicyType policyType) throws NoSuchFieldException {
		insuranceRenewalBillPupGeneration(AHRBXX, policyType);
	}

	/**
	 * @name Creation converted policy for checking Insurance Renewal Bill
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 3. Check that form is getting generated with correct content
	 * @details
	 */
	private void insuranceRenewalBillHomeGeneration(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
		String policyNumber = createPolicyForTD(testData);
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		renewalBillJobExecution(effectiveDate);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_BILL);
		verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_BILL);
		verifyRenewalDocumentTagData(document, testData, isPupPresent, RENEWAL_BILL);
	}

	/**
	 * @name Creation converted policy for checking Insurance Renewal Bill
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 3. Check that form is getting generated with correct content
	 * @details
	 */
	private void insuranceRenewalBillPupGeneration(DocGenEnum.Documents form, PolicyType policyType) throws NoSuchFieldException {
		String policyNumber = createPolicyPupConvForTD(policyType);
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		log.info("Conversion PUP policy number: " + policyNumber);

		renewalBillJobExecution(effectiveDate);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_BILL);
		verifyTagDataPup(document, policyNumber, policyType, RENEWAL_BILL);
	}

	/**
	 * @name Test Conversion Document generation (Mortgagee Bill Final Expiration Notice)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
	 * 4. Check that HSRR2XX document is getting generated
	 * @details
	 */
	public void pas8763_mortgageeBillFinalExpirationHSRR2XX(String state) throws NoSuchFieldException {
		mortgageeBillFinalExpiration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRR2XX);
	}

	/**
	 * @name Creation converted policy for checking Mortgagee Bill Final Expiration Notice
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 3. Check that form is getting generated with correct content
	 * @details
	 */
	private void mortgageeBillFinalExpiration(TestData testData, DocGenEnum.Documents form) throws NoSuchFieldException {
		String policyNumber = createPolicyForTD(testData);
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		billFinalxpNoticeJobExecution(effectiveDate);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, MORTGAGEE_BILL_FINAL_EXP_NOTICE);
		verifyTagDataBill(document, policyNumber,MORTGAGEE_BILL_FINAL_EXP_NOTICE);
	}

	/**
	 * @name Test Conversion Document generation (Mortgagee Bill First Renewal Reminder)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
	 * 4. Check that HSRRXX document is getting generated
	 * @details
	 */
	public void pas8762_mortgageeBillFirstRenewalReminderHSRRXX(String state) throws NoSuchFieldException {
		mortgageeBillFirstRenewalReminder(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRRXX);
	}

	/**
	 * @name Creation converted policy for checking Mortgagee Bill First Renewal Reminder
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 3. Check that form is getting generated with correct content
	 * @details
	 */
	private void mortgageeBillFirstRenewalReminder(TestData testData, DocGenEnum.Documents form) throws NoSuchFieldException {
		String policyNumber = createPolicyForTD(testData);
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		billFirstReminderNoticeJobExecution(effectiveDate);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, BILL_FIRST_RENEW_REMINDER_NOTICE);
		verifyTagDataBill(document, policyNumber, BILL_FIRST_RENEW_REMINDER_NOTICE);
	}

	private void verifyTagDataPup(Document document, String policyNumber, PolicyType policyType, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		assertThat(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName)).isEqualTo(policyNumber);
		switch (policyType.getShortName()) {
			case "HomeSS":
				assertThat(getPackageTag(policyNumber, "PrmPlcyGrp", eventName)).isEqualTo("Homeowners");
				break;
			case "HomeSS_HO4":
				assertThat(getPackageTag(policyNumber, "PrmPlcyGrp", eventName)).isEqualTo("Renters");
				break;
			case "HomeSS_HO6":
				assertThat(getPackageTag(policyNumber, "PrmPlcyGrp", eventName)).isEqualTo("Condominium Owners");
				break;
			default:
				throw new IllegalArgumentException("Undefined policyType " + policyType.getShortName());
		}
		if(RENEWAL_BILL.equals(eventName)){
			verifyTagData(document, "ConvFlgYN", "Y");
		}
	}

	private void preRenewalJobExecution(LocalDateTime effectiveDate, String policyNumber){
		SearchPage.openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		LocalDateTime preRenewalGenDate = getTimePoints().getPreRenewalLetterGenerationDate(effectiveDate);
		TimeSetterUtil.getInstance().nextPhase(preRenewalGenDate);
		JOBS_FOR_EVENT.get(PRE_RENEWAL).forEach(job -> JobUtils.executeJob(job));
	}

	private void renewalOfferCoverLetterJobExecution(LocalDateTime effectiveDate, String policyNumber){
		SearchPage.openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(effectiveDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JOBS_FOR_EVENT.get(RENEWAL_OFFER).forEach(job -> JobUtils.executeJob(job));
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
	}

	private void renewalBillJobExecution(LocalDateTime effectiveDate){
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(effectiveDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JOBS_FOR_EVENT.get(RENEWAL_OFFER).forEach(job -> JobUtils.executeJob(job));
		LocalDateTime billGenerationDate = getTimePoints().getBillGenerationDate(effectiveDate);
		TimeSetterUtil.getInstance().nextPhase(billGenerationDate);
		JOBS_FOR_EVENT.get(RENEWAL_BILL).forEach(job -> JobUtils.executeJob(job));
	}

	private void billFirstReminderNoticeJobExecution(LocalDateTime effectiveDate){
		renewalBillJobExecution(effectiveDate);
		LocalDateTime mortgageeBillFirstRenewalReminder = getTimePoints().getMortgageeBillFirstRenewalReminder(effectiveDate);
		TimeSetterUtil.getInstance().nextPhase(mortgageeBillFirstRenewalReminder);
		JOBS_FOR_EVENT.get(BILL_FIRST_RENEW_REMINDER_NOTICE).forEach(job -> JobUtils.executeJob(job));
	}

	private void billFinalxpNoticeJobExecution(LocalDateTime effectiveDate){
		billFirstReminderNoticeJobExecution(effectiveDate);
		LocalDateTime mortgageeBillFinalExpNotice = getTimePoints().getMortgageeBillFinalExpirationNotice(effectiveDate);
		TimeSetterUtil.getInstance().nextPhase(mortgageeBillFinalExpNotice);
		JOBS_FOR_EVENT.get(MORTGAGEE_BILL_FINAL_EXP_NOTICE).forEach(job -> JobUtils.executeJob(job));
	}

	/**
	 * Create conversion policy based on Test Data
	 */
	private String createPolicyForTD(TestData testData) {
		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fill(testData);
		return PolicySummaryPage.linkPolicy.getValue();
	}

	private String createPolicyPupConvForTD(PolicyType policyType) {
		mainApp().open();
		String homePolicyNumber = createCorrespondingConversionHomePolicy(policyType);

		//adjust the conversion TD with valid home policy number and type
		String homePolicyType = defaultIfBlank(substringAfterLast(policyType.getShortName(), "_"), "HO3");
		TestData testData = getStateTestData(testDataManager.policy.get(getPolicyType()), "Conversion", "TestData")
				.adjust(TestData.makeKeyPath(
						PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(),
						PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.class.getSimpleName() + "[0]",
						PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.class.getSimpleName(),
						PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_NUMBER.getLabel()),
						homePolicyNumber)
				.adjust(TestData.makeKeyPath(
						PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(),
						PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.class.getSimpleName() + "[0]",
						PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.class.getSimpleName(),
						PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_TYPE.getLabel()),
						homePolicyType);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fill(testData);
		return PolicySummaryPage.linkPolicy.getValue();
	}

	/**
	 * 1. Create a Customer
	 * 2. Get default HO3 {@link TestData} and create HO3 policy
	 * @return policyNumber {@link String} of the created policy
	 */
	public String createCorrespondingConversionHomePolicy(PolicyType policyType) {
		createCustomerIndividual();
		TestData testData = getStateTestData(testDataManager.policy.get(policyType), "InitiateRenewalEntry", "TestData");
		customer.initiateRenewalEntry().perform(testData);
		testData = getStateTestData(testDataManager.policy.get(policyType), "Conversion", "TestData");
		policyType.get().getDefaultView().fill(testData);
		return PolicySummaryPage.linkPolicy.getValue();
	}

	/**
	 * Create conversion policy based on Test Data with linked PUP converted
	 */
	private String createPolicyForTDPup() {
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		TestData testData = getStateTestData(testDataManager.policy.get(PolicyType.PUP), "InitiateRenewalEntry", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.PREVIOUS_POLICY_NUMBER
						.getLabel()), legacyPolicyNumber);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		customer.initiateRenewalEntry().perform(testData);
		Tab.buttonSaveAndExit.click();
		log.info("Conversion PUP policy number: " + PolicySummaryPage.linkPolicy.getValue());
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
		assertThat(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName)).isEqualTo(policyNumber);
		assertThat(getPackageTag(policyNumber, "HdesPlcyNum", eventName).replaceAll("-", "")).isEqualTo(legacyPolicyNumber);
	}

	/**
	 * Method to verify tags are present and contain specific values in Document
	 * Note: Will be refactored after the refactoring of {@link DocGenHelper}
	 *
	 * @param document
	 * @param testData
	 * @param isPupPresent
	 */
	private void verifyRenewalDocumentTagData(Document document, TestData testData, boolean isPupPresent, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		assertThat(document.getxPathInfo()).isEqualTo("/Policy/Renewal");
		if(RENEWAL_BILL.equals(eventName)){
			verifyTagData(document, "ConvFlgYN", "Y");
		}
		else{
			if (isPupPresent) {
				verifyTagData(document, "PupCvrgYN", "Y");
			} else {
				verifyTagData(document, "PupCvrgYN", "N");
			}
		}
		if ("Yes".equals(testData.getTestData("MortgageesTab").getValue("Mortgagee"))) {
			verifyTagData(document, "ThrdPrtyHdr", "TestName");
			verifyTagData(document, "ThrdPrtyLnNum", "12345678");
		}
	}

	private void verifyTagDataBill(Document document, String policyNumber, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		assertThat(document.getxPathInfo()).isEqualTo("/Billing/Invoice Bills Statements");
		assertThat(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName)).isEqualTo(policyNumber);
		verifyTagData(document, "ConvFlgYN", "Y");
	}

	/**
	 * Verify that tag value is present in the Documents section
	 */
	private void verifyTagData(Document document, String tag, String textFieldValue) {
		assertThat(DocGenHelper.getDocumentDataElemByName(tag, document).getDataElementChoice().getTextField()).isEqualTo(textFieldValue);
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
		TestData mortgageeTD = getTestSpecificTD("MortgageesTab");
		//adjust TestData with Premium and Coverage tab data
		String premiumAndCoverageTabKey = TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName());
		TestData premiumAndCoverageTD = getTestSpecificTD("PremiumsAndCoveragesQuoteTab_Mortgagee");
		return policyTD.adjust(mortgageeTabKey, mortgageeTD).adjust(premiumAndCoverageTabKey, premiumAndCoverageTD);
	}

	/**
	 * Utility method that enhances Conversion {@link TestData} with PUP in OtherActiveAAAPolicies
	 */
	private TestData adjustWithPupData(TestData policyTD) {
		String pupOtherActiveAAAPoliciesTabKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
		TestData pupTD = getTestSpecificTD("OtherActiveAAAPolicies");
		return policyTD.adjust(pupOtherActiveAAAPoliciesTabKey, pupTD);
	}

}