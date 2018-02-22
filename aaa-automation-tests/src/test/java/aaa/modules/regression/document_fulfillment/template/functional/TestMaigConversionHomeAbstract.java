package aaa.modules.regression.document_fulfillment.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;
import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static aaa.main.enums.DocGenEnum.Documents.*;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import java.text.MessageFormat;
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
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

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
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		log.info("Conversion Home policy number: "+policyNumber+" with legacy number: " + legacyPolicyNumber);

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
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		String legacyPolicyNumber = createPolicyForTDPup();
		log.info("Conversion Home policy number: "+policyNumber+" with legacy number: " + legacyPolicyNumber);

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

		renewalCoverLetterFormGeneration(getConversionPolicyTD(), HSRNHODPXX, false);

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
		log.info("Conversion Home policy number: "+policyNumber+" with legacy number: " + legacyPolicyNumber);

		processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		CustomAssert.assertTrue(organicDocument == null);
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
		log.info("Conversion Home policy number: "+policyNumber+" with legacy number: " + legacyPolicyNumber);

		processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		CustomAssert.assertTrue(organicDocument == null);
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

		renewalCoverLetterFormGenerationPup(getConversionPolicyTD(), HSRNHODPXX, true);

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
	 * @name Test Conversion Document generation (Renewal cover letter)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for PUP
	 * 4. Check that HSRNPUPXX document is getting generated
	 * @details
	 */
	public void pas2571_renewalCoverLetterHSRNPUPXX(String state, PolicyType policyType) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		renewalCoverLetterFormGenerationConvPup(getConversionPolicyTD(), HSRNPUPXX, policyType);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Creation converted policy for checking Renewal Cover letter
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 4. Check that form is getting generated with correct content
	 * @details
	 */
	private void renewalCoverLetterFormGenerationConvPup(TestData testData, DocGenEnum.Documents form, PolicyType policyType) throws NoSuchFieldException {
		String policyNumber = createPolicyPupConvForTD(testData, policyType);
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		log.info("Conversion PUP policy number: "+policyNumber);

		processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

		Document organicDocument = DocGenHelper.waitForDocumentsAppearanceInDB(HSRNXX, policyNumber, RENEWAL_OFFER, false);
		CustomAssert.assertTrue(organicDocument == null);
		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
		verifyTagDataPup(policyNumber, policyType, RENEWAL_OFFER);
	}

	private void verifyTagDataPup(String policyNumber, PolicyType policyType, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		CustomAssert.assertTrue(MessageFormat.format("Problem is in tags: [{0}], [{1}]", "PlcyPrfx", "PlcyNum"), policyNumber
				.equals(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName)));
		switch (policyType.getShortName()) {
			case "HomeSS":
				CustomAssert.assertTrue("Homeowners".equals(getPackageTag(policyNumber, "PrmPlcyGrp", eventName)));
				break;
			case "HomeSS_HO4":
				CustomAssert.assertTrue("Renters".equals(getPackageTag(policyNumber, "PrmPlcyGrp", eventName)));
				break;
			case "HomeSS_HO6":
				CustomAssert.assertTrue("Condominium Owners".equals(getPackageTag(policyNumber, "PrmPlcyGrp", eventName)));
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
				LocalDateTime preRenewalGenDate = getTimePoints().getPreRenewalGenerationDate(effectiveDate);
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
		initiateManualConversionForTest();
		policy.getDefaultView().fillUpTo(testData, BindTab.class, false);
		policy.getDefaultView().getTab(BindTab.class).submitTab();
		return PolicySummaryPage.linkPolicy.getValue();
	}

	private String createPolicyPupConvForTD(TestData testData, PolicyType policyType) {
		mainApp().open();
		String homePolicyNumber = createCorrespondingConversionHomePolicy(policyType);

		//adjust the conversion TD with valid home policy number and type
		String homePolicyType = defaultIfBlank(substringAfterLast(policyType.getShortName(), "_"), "HO3");
		testData.adjust(TestData.makeKeyPath(
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
		initiateManualConversionForTest();
		policy.getDefaultView().fillUpTo(testData, aaa.main.modules.policy.pup.defaulttabs.BindTab.class , false);
		policy.getDefaultView().getTab(aaa.main.modules.policy.pup.defaulttabs.BindTab.class).submitTab();
		return PolicySummaryPage.linkPolicy.getValue();
	}

	/**
	 * 1. Create a Customer
	 * 2. Get default HO3 {@link TestData} and create HO3 policy
	 * @return policyNumber {@link String} of the created policy
	 */
	public String createCorrespondingHomePolicy(PolicyType policyType) {
		createCustomerIndividual();
		TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");
		policyType.get().createPolicy(testData);
		String homePolicyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().reopen();
		return homePolicyNumber;
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
		policyType.get().getDefaultView().fillUpTo(testData, BindTab.class , false);
		policyType.get().getDefaultView().getTab(BindTab.class).submitTab();
		String homePolicyNumber = PolicySummaryPage.linkPolicy.getValue();
		mainApp().reopen();
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
		log.info("Conversion PUP policy number: "+PolicySummaryPage.linkPolicy.getValue());
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
		CustomAssert.assertTrue("/Policy/Renewal".equals(document.getxPathInfo()));
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