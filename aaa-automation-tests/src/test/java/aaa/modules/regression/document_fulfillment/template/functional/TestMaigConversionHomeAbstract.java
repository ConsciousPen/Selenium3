package aaa.modules.regression.document_fulfillment.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;
import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static aaa.main.enums.DocGenEnum.Documents.*;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.assertj.core.api.Assertions.assertThat;
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
			ImmutableMap.of(PRE_RENEWAL, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.aaaPreRenewalNoticeAsyncJob),
					RENEWAL_OFFER, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.renewalOfferGenerationPart2, Jobs.aaaDocGenBatchJob));

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

		processRenewal(PRE_RENEWAL, effectiveDate, policyNumber);

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

		processRenewal(PRE_RENEWAL, effectiveDate, policyNumber);

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

		processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		assertThat(organicDocument).isEqualTo(null);
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
		log.info("Conversion Home policy number: " + policyNumber + " with legacy number: " + legacyPolicyNumber);

		processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		assertThat(organicDocument).isEqualTo(null);
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

		processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		assertThat(organicDocument).isEqualTo(null);
		DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
		verifyTagDataPup(policyNumber, policyType, RENEWAL_OFFER);
	}

	private void verifyTagDataPup(String policyNumber, PolicyType policyType, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		assertThat(policyNumber.equals(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName))).isTrue();
		switch (policyType.getShortName()) {
			case "HomeSS":
				assertThat("Homeowners".equals(getPackageTag(policyNumber, "PrmPlcyGrp", eventName))).isTrue();
				break;
			case "HomeSS_HO4":
				assertThat("Renters".equals(getPackageTag(policyNumber, "PrmPlcyGrp", eventName))).isTrue();
				break;
			case "HomeSS_HO6":
				assertThat("Condominium Owners".equals(getPackageTag(policyNumber, "PrmPlcyGrp", eventName))).isTrue();
				break;
			default:
				throw new IllegalArgumentException("Undefined policyType " + policyType.getShortName());
		}
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
				LocalDateTime preRenewalGenDate = getTimePoints().getPreRenewalLetterGenerationDate(effectiveDate);
				TimeSetterUtil.getInstance().nextPhase(preRenewalGenDate);
				JOBS_FOR_EVENT.get(eventName).forEach(job -> JobUtils.executeJob(job));
				break;
			case RENEWAL_OFFER:
				LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(effectiveDate);
				TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
				JOBS_FOR_EVENT.get(eventName).forEach(job -> JobUtils.executeJob(job));
				mainApp().reopen();
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
		String homePolicyNumber = PolicySummaryPage.linkPolicy.getValue();
		return homePolicyNumber;
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
		assertThat(policyNumber
				.equals(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName))).isTrue();
		assertThat(legacyPolicyNumber
				.equals(getPackageTag(policyNumber, "HdesPlcyNum", eventName).replaceAll("-", ""))).isTrue();
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
		assertThat("/Policy/Renewal".equals(document.getxPathInfo())).isTrue();
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
		assertThat(textFieldValue
				.equals(DocGenHelper.getDocumentDataElemByName(tag, document).getDataElementChoice().getTextField())).isTrue();
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