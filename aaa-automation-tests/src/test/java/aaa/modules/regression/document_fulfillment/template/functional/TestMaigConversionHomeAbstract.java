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
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
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
import toolkit.utils.datetime.DateTimeUtils;

public abstract class TestMaigConversionHomeAbstract extends PolicyBaseTest {

	private static final Map<AaaDocGenEntityQueries.EventNames, List<Job>> JOBS_FOR_EVENT  = ImmutableMap.<AaaDocGenEntityQueries.EventNames, List<Job>>builder()
			.put(PRE_RENEWAL, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.aaaPreRenewalNoticeAsyncJob, Jobs.aaaDocGenBatchJob))
			.put(RENEWAL_OFFER, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.renewalOfferGenerationPart2, Jobs.aaaDocGenBatchJob))
			.put(RENEWAL_BILL, ImmutableList.of(Jobs.aaaRenewalNoticeBillAsyncJob, Jobs.aaaDocGenBatchJob))
			.put(BILL_FIRST_RENEW_REMINDER_NOTICE, ImmutableList.of(Jobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob, Jobs.aaaDocGenBatchJob))
			.put(MORTGAGEE_BILL_FINAL_EXP_NOTICE, ImmutableList.of(Jobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob, Jobs.aaaDocGenBatchJob))
			.put(EXPIRATION_NOTICE, ImmutableList.of(Jobs.aaaRenewalReminderGenerationAsyncJob, Jobs.aaaDocGenBatchJob))
			.build();

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
	 * @name Creation converted policy for checking 'Expiration Notice' letter
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Conversion Policy
	 * 3. Generate Bill at R-20
	 * 4. Generate 'Expiration Notice' at R+10
	 * 5. Check that form is getting generated with correct content
	 * @details
	 */
	public void pas20836_expirationNoticeFormGeneration(String state) throws NoSuchFieldException {
		expirationNoticeFormGeneration(getConversionPolicyDefaultTD(), AH64XX);
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
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		preRenewalJobExecution(expirationDate, policyNumber);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
		verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
		verifyRenewalDocumentTagData(document, testData, isPupPresent, PRE_RENEWAL);
	}

	/**
	 * @name Creation converted policy for checking 'Expiration Notice' letter
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Conversion Policy
	 * 3. Generate Bill at R-20
	 * 4. Generate 'Expiration Notice' at R+10
	 * 5. Check that form is getting generated with correct content
	 * @details
	 */
	private void expirationNoticeFormGeneration(TestData testData, DocGenEnum.Documents form) throws NoSuchFieldException {
		String policyNumber = openAppAndCreateConversionPolicy(testData);

		//Auto conversion policy does not have expiration date on PolicySummaryPage
		LocalDateTime policyExpirationDate = PolicySummaryPage.getEffectiveDate().plusYears(1);

		expirationNoticeJobExecution(policyExpirationDate);

		DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, EXPIRATION_NOTICE);
		String policyTransactionCode = getPackageTag(policyNumber, "PlcyTransCd", EXPIRATION_NOTICE);

		String expectedPolicyTransCode;

		if (getPolicyType().equals(PolicyType.AUTO_SS)) {
			switch (getState()) {
				case Constants.States.AZ:
				case Constants.States.NY:
				case Constants.States.OH:
					expectedPolicyTransCode = "CANC";
					break;
				default:
					expectedPolicyTransCode = "STMT";
					break;
			}
		} else {
			//PAS-20836
			switch (getState()) {
				case Constants.States.AZ:
				case Constants.States.NY:
				case Constants.States.OH:
					expectedPolicyTransCode = "CANB";
					break;
				default:
					expectedPolicyTransCode = "STMT";
					break;
			}
		}
		assertThat(policyTransactionCode).as("PlcyTransCd is not correct for " + getState()).isEqualTo(expectedPolicyTransCode);
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
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		String legacyPolicyNumber = createPolicyForTDPup();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		preRenewalJobExecution(expirationDate, policyNumber);

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
	 * @name Test Conversion Document generation (Renewal cover letters)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that HS65PA documents are getting generated
	 * @details
	 */
	public void pas8405_noticeOfNonRenewalLetterHS65PA(String state) throws NoSuchFieldException {
		int numberOfLetters = renewalCoverLetterFormsGeneration(getConversionPolicyDefaultTD(), HS65PA, false, state);
		assertThat(numberOfLetters).isEqualTo(2);
	}

	/**
	 * @name Test Conversion Document generation (Non Renewal cover letters)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that HS65MD documents are getting generated
	 * @details
	 */
	public void pas12047_noticeOfNonRenewalLetterHS65MD(String state) throws NoSuchFieldException {
		int numberOfLetters = renewalCoverLetterFormsGeneration(getConversionPolicyDefaultTD(), HS65MD, false, state);
		assertThat(numberOfLetters).isEqualTo(1);
	}

	/**
	 * @name Test Conversion Document generation (Non Renewal cover letters)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that HSPISKY documents are getting generated
	 * 5. Buy Conversion Policy
	 * 6. Move time to 2nd Renewals Offer Generation date (usually R-35)
	 * 7. Check that HSPISKY document is NOT generated
	 * @details
	 */
	public void pas18432_policyInformationSheetHSPISKY(String state) throws NoSuchFieldException {
		int numberOfLetters = renewalCoverLetterFormsGeneration(getConversionPolicyDefaultTD(), HSPISKY, false, state);
		assertThat(numberOfLetters).isEqualTo(1);
		checkSecondRenewalsOfferGenerationDoesNotGenerateForm(HSPISKY);
	}

	/**
	 * @name Test Conversion Document generation (Non Renewal cover letters)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that HSFLD documents are getting generated
	 * 5. Buy Conversion Policy
	 * 6. Move time to 2nd Renewals Offer Generation date (usually R-35)
	 * 7. Check that HSFLD document is NOT generated
	 * @details
	 */
	public void pas11772_importantNoticeRegardingFloodInsuranceHSFLD(String state) throws NoSuchFieldException {
		int numberOfLetters = renewalCoverLetterFormsGeneration(getConversionPolicyDefaultTD(), HSFLD, false, state);
		assertThat(numberOfLetters).isEqualTo(1);
		checkSecondRenewalsOfferGenerationDoesNotGenerateForm(HSFLD);
	}

	/**
	 /**
	 * @name Test Conversion Document generation (Non Renewal cover letters)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that HSIIHNV documents are getting generated
	 * 5. Buy Conversion Policy
	 * 6. Move time to 2nd Renewals Offer Generation date (usually R-35)
	 * 7. Check that HSIIHNV document is NOT generated
	 * @details
	 */
	public void pas18434_importantInfoRegardingYourPolicyHSIINV(String state) throws NoSuchFieldException {
		int numberOfLetters = renewalCoverLetterFormsGeneration(getConversionPolicyDefaultTD(), HSIIHNV, false, state);
		assertThat(numberOfLetters).isEqualTo(1);
		checkSecondRenewalsOfferGenerationDoesNotGenerateForm(HSIIHNV);
	}

	private void checkSecondRenewalsOfferGenerationDoesNotGenerateForm(DocGenEnum.Documents form) {
		PolicySummaryPage.buttonBackFromRenewals.click();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime conversionExpDate = PolicySummaryPage.getExpirationDate();

		payTotalAmtDue(conversionExpDate, policyNumber);

		LocalDateTime secondPolicyExpirationDate = PolicySummaryPage.getExpirationDate();
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(secondPolicyExpirationDate));

		JOBS_FOR_EVENT.get(RENEWAL_OFFER).forEach(job -> JobUtils.executeJob(job));

		//After first Renewal_Offer_Generation_Part2 run only Renewal Image is created in 'Data Gather' status
		//Running 2nd time proposes renewal image.
		if (DocGenHelper.getAllDocumentPackages(policyNumber, RENEWAL_OFFER).size() == 1) {
			JOBS_FOR_EVENT.get(RENEWAL_OFFER).forEach(job -> JobUtils.executeJob(job));
		}

		//Check that 2nd Renewal Offer is generated
		assertThat(DocGenHelper.getAllDocumentPackages(policyNumber, RENEWAL_OFFER).size()).as("2nd Renewal Offer is not generated").isEqualTo(2);
		//Check that 2nd Renewal Offer does not have the form (e.g. HSFLD, HSPISKY)
		assertThat(DocGenHelper.waitForMultipleDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER, false).size()).isEqualTo(1);
	}

	/**
	 * @name Creation converted policy for checking Renewal Cover letters
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 4. Check that forms are getting generated with correct content
	 * @details
	 * @return number of documents
	 */
	private int renewalCoverLetterFormsGeneration(TestData testData, DocGenEnum.Documents form, boolean isPupPresent, String state) throws NoSuchFieldException {
		String policyNumber = createPolicyForTD(testData);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getStaticElement(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER).getValue();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		renewalOfferCoverLetterJobExecution(expirationDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		assertThat(organicDocument).isEqualTo(null);

		List<Document> documents = DocGenHelper.waitForMultipleDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
		verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
		for (Document document : documents) {
			if (form!=HSFLD && form !=HSPISKY && form != HSIIHNV && form != DSIIDNV) {
				verifyRenewalDocumentTagDataConvFlgYN(document, testData, isPupPresent, RENEWAL_OFFER);
			}
		}
		return documents.size();
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
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getStaticElement(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER).getValue();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		renewalOfferCoverLetterJobExecution(expirationDate, policyNumber);

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
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		String legacyPolicyNumber = createPolicyForTDPup();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		renewalOfferCoverLetterJobExecution(expirationDate, policyNumber);

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
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		log.info("Conversion PUP policy number: " + policyNumber);

		renewalOfferCoverLetterJobExecution(expirationDate, policyNumber);

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
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		renewalBillJobExecution(expirationDate);

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
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		log.info("Conversion PUP policy number: " + policyNumber);

		renewalBillJobExecution(expirationDate);

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
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getStaticElement(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER).getValue();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		mortgageeBillFinalRenewalReminderNoticeJobExecution(expirationDate);

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
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getStaticElement(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER).getValue();
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		mortgageeBillFirstRenewalReminderNoticeJobExecution(expirationDate);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, BILL_FIRST_RENEW_REMINDER_NOTICE);
		verifyTagDataBill(document, policyNumber, BILL_FIRST_RENEW_REMINDER_NOTICE);
	}

	private void verifyTagDataPup(Document document, String policyNumber, PolicyType policyType, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		assertThat(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName)).isEqualTo(policyNumber);
		switch (policyType.getShortName()) {
			case "HomeSS_HO3":
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


	/**
	 /**
	 * @name Test Conversion Document generation (Non Renewal cover letters)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that DSIIDNV documents are getting generated
	 * 5. Buy Conversion Policy
	 * 6. Move time to 2nd Renewals Offer Generation date (usually R-35)
	 * 7. Check that DSIIDNV document is NOT generated
	 * @details
	 */
	public void pas18433_importantInformationRegardingYourNewDwellingFirePolicyDSIIDNV0512(String state) throws NoSuchFieldException {
		int numberOfLetters = renewalCoverLetterFormsGeneration(getConversionPolicyDefaultTD(), DSIIDNV, false, state);
		assertThat(numberOfLetters).isEqualTo(1);
		checkSecondRenewalsOfferGenerationDoesNotGenerateForm(DSIIDNV);
	}

	private void preRenewalJobExecution(LocalDateTime expirationDate, String policyNumber){
		SearchPage.openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		LocalDateTime preRenewalGenDate = getTimePoints().getPreRenewalLetterGenerationDate(expirationDate).plusHours(4);
		TimeSetterUtil.getInstance().nextPhase(preRenewalGenDate);
		JOBS_FOR_EVENT.get(PRE_RENEWAL).forEach(job -> JobUtils.executeJob(job));
	}

	private void renewalOfferCoverLetterJobExecution(LocalDateTime expirationDate, String policyNumber){
		SearchPage.openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JOBS_FOR_EVENT.get(RENEWAL_OFFER).forEach(job -> JobUtils.executeJob(job));
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
	}

	private void renewalBillJobExecution(LocalDateTime expirationDate){
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JOBS_FOR_EVENT.get(RENEWAL_OFFER).forEach(job -> JobUtils.executeJob(job));
		LocalDateTime billGenerationDate = getTimePoints().getBillGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenerationDate);
		JOBS_FOR_EVENT.get(RENEWAL_BILL).forEach(job -> JobUtils.executeJob(job));
	}

	private void mortgageeBillFirstRenewalReminderNoticeJobExecution(LocalDateTime expirationDate){
		renewalBillJobExecution(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(expirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		LocalDateTime mortgageeBillFirstRenewalReminder = getTimePoints().getMortgageeBillFirstRenewalReminder(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(mortgageeBillFirstRenewalReminder);
		JOBS_FOR_EVENT.get(BILL_FIRST_RENEW_REMINDER_NOTICE).forEach(job -> JobUtils.executeJob(job));
	}

	private void mortgageeBillFinalRenewalReminderNoticeJobExecution(LocalDateTime expirationDate){
		mortgageeBillFirstRenewalReminderNoticeJobExecution(expirationDate);
		LocalDateTime lapsedRenewal = getTimePoints().getRenewCustomerDeclineDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(lapsedRenewal);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		TimeSetterUtil.getInstance().nextPhase(expirationDate.plusMonths(2).minusDays(20).with(DateTimeUtils.closestPastWorkingDay));
		JOBS_FOR_EVENT.get(MORTGAGEE_BILL_FINAL_EXP_NOTICE).forEach(job -> JobUtils.executeJob(job));
	}

	private void expirationNoticeJobExecution(LocalDateTime expirationDate){
		renewalBillJobExecution(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(expirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getInsuranceRenewalReminderDate(expirationDate));
		JOBS_FOR_EVENT.get(EXPIRATION_NOTICE).forEach(job -> JobUtils.executeJob(job));
	}

	/**
	 * Create conversion policy based on Test Data
	 */
	private String createPolicyForTD(TestData testData) {
		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fill(testData);
		PolicySummaryPage.buttonBackFromRenewals.click();
		return PolicySummaryPage.getPolicyNumber();
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
		PolicySummaryPage.buttonBackFromRenewals.click();
		return PolicySummaryPage.getPolicyNumber();
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
		PolicySummaryPage.buttonBackFromRenewals.click();
		return PolicySummaryPage.getPolicyNumber();
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
		PolicySummaryPage.buttonBackFromRenewals.click();
		log.info("Conversion PUP policy number: " + PolicySummaryPage.getPolicyNumber());
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

	/**
	 * Method to verify tags are present and contain specific values in Document
	 * Note: Will be refactored after the refactoring of {@link DocGenHelper}
	 *
	 * @param document
	 * @param testData
	 * @param isPupPresent
	 */
	private void verifyRenewalDocumentTagDataConvFlgYN(Document document, TestData testData, boolean isPupPresent, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		assertThat(document.getxPathInfo()).isEqualTo("/Policy/Renewal");
		if(RENEWAL_BILL.equals(eventName) || RENEWAL_OFFER.equals(eventName)){
			verifyTagData(document, "ConvFlgYN", "Y");
		}
		else{
			if (isPupPresent) {
				verifyTagData(document, "PupCvrgYN", "Y");
			} else {
				verifyTagData(document, "PupCvrgYN", "N");
			}
		}}


	private void verifyTagDataBill(Document document, String policyNumber, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		assertThat(document.getxPathInfo()).isEqualTo("/Billing/Invoice Bills Statements");
		assertThat(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName)).isEqualTo(policyNumber);
		verifyTagData(document, "ConvFlgYN", "Y");
	}

	/**
	 * Verify that tag value is present in the Documents section
	 */
	protected void verifyTagData(Document document, String tag, String textFieldValue) {
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
