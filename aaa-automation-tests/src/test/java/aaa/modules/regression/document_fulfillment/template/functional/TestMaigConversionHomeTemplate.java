package aaa.modules.regression.document_fulfillment.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;
import static aaa.main.enums.DocGenEnum.Documents.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.inject.internal.ImmutableList;
import com.google.inject.internal.ImmutableMap;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
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
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public abstract class TestMaigConversionHomeTemplate extends PolicyBaseTest {

	protected TestData tdBilling = testDataManager.billingAccount;
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();

	private ProductRenewalsVerifier productRenewalsVerifier = new ProductRenewalsVerifier();

	protected BillingAccount billingAccount = new BillingAccount();
	protected MaigManualConversionHelper maigManualConversionHelper = new MaigManualConversionHelper();

	private static final Map<AaaDocGenEntityQueries.EventNames, List<Job>> JOBS_FOR_EVENT =
			ImmutableMap.of(PRE_RENEWAL, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.aaaPreRenewalNoticeAsyncJob),
					RENEWAL_OFFER, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.renewalOfferGenerationPart2));

	public void verifyConversionFormsSequence(TestData testData) throws NoSuchFieldException {
		LocalDateTime renewalOfferEffectiveDate = getTimePoints().getEffectiveDateForTimePoint(
				TimeSetterUtil.getInstance().getCurrentTime(), TimePoints.TimepointsList.RENEW_GENERATE_OFFER);

		// Get State/Product specific forms
		List<String> forms = getConversionGeneratedForms();
		//Change Membership number in testData to get AHMVCNV form - Validation letter

		/* Start PAS-2764 Scenario 1, Generate forms and check sequence*/
		/**PAS-9774, PAS-10111 - both has the same root cause which is a Base defect EISAAASP-1852 and has been already resolved in Base EIS 8.17.
		 It will come with next upgrade, until then there's simple workaround - need to run aaa-admin application instead of aaa-app.
		 Both, manual propose and automated propose should work running under aaa-admin.**/

		// Create manual entry
		String policyNumber = createFormsSpecificManualEntry(testData,renewalOfferEffectiveDate);

		checkPolicyStatus(policyNumber);

		List<Document> actualDocumentsListAfterFirstRenewal = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		maigManualConversionHelper.verifyFormSequence(forms, actualDocumentsListAfterFirstRenewal);
		// End PAS-2764 Scenario 1

		//PAS-9607 Verify that packages are generated with correct transaction code
		maigManualConversionHelper.pas9607_verifyPolicyTransactionCode("MCON", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		//needed for home banking form generation
		maigManualConversionHelper.setUpHomeBankingForConversionRenewal(policyNumber);

		maigManualConversionHelper.runRenewalOfferPart2();

		billGeneration(renewalOfferEffectiveDate);

		// Start PAS-2764 Scenario 1 Issue first renewal
		mainApp().open();
		maigManualConversionHelper.acceptPayment(policyNumber,testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"));

		upldatePolicyStatus(renewalOfferEffectiveDate);
		checkPolicyStatus(policyNumber);
		// End PAS-2764 Scenario 1 Issue first renewal

		/* Scenario 2, create and issue second renewal and verify documents list */
		issueSecondRenewal(renewalOfferEffectiveDate);

		openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		/**https://csaaig.atlassian.net/browse/PAS-9157*/
		/**PAS-10256
		 Cannot rate Home SS policy with effective date higher or equal to 2020-02-018*/

		//PAS-9607 Verify that packages are generated with correct transaction code
		maigManualConversionHelper.pas9607_verifyPolicyTransactionCode("0210", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		// Shouldn't be after second renewal
		maigManualConversionHelper.pas2674_verifyConversionRenewalPackageAbsence(forms, policyNumber, actualDocumentsListAfterFirstRenewal);

		//Generate Bill for the second renewal to verify Home Banking forms
		billGeneration(renewalOfferEffectiveDate.plusYears(1));
	}

	public void verifyBillingFormsSequence(TestData testData) throws NoSuchFieldException {
		/* Start PAS-2764 Scenario 1, Generate forms and check sequence*/
		/**PAS-9774, PAS-10111 - both has the same root cause which is a Base defect EISAAASP-1852 and has been already resolved in Base EIS 8.17.
		 It will come with next upgrade, until then there's simple workaround - need to run aaa-admin application instead of aaa-app.
		 Both, manual propose and automated propose should work running under aaa-admin.**/
		LocalDateTime renewalOfferEffectiveDate = getTimePoints().getEffectiveDateForTimePoint(
				TimeSetterUtil.getInstance().getCurrentTime(), TimePoints.TimepointsList.RENEW_GENERATE_OFFER);

		// Create manual entry
		String policyNumber = createFormsSpecificManualEntry(testData,renewalOfferEffectiveDate);

		checkPolicyStatus(policyNumber);
		//needed for home banking form generation
		maigManualConversionHelper.setUpHomeBankingForConversionRenewal(policyNumber);
		// Add Credit Card payment method and Enable AutoPayment
		Tab.buttonBack.click();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.update().perform(testDataManager.billingAccount.getTestData("Update", "TestData_AddAutopay"));

		maigManualConversionHelper.runRenewalOfferPart2();

		billGeneration(renewalOfferEffectiveDate);

		//PAS-9607 Verify that packages are generated with correct transaction code
		maigManualConversionHelper.pas9607_verifyPolicyTransactionCode("STMT", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);

		//PAS-9816 Verify that Billing Renewal package forms are generated and are in correct order
		maigManualConversionHelper.pas9816_verifyRenewalBillingPackageFormsPresence(policyNumber,getPolicyType());

		// Start PAS-2764 Scenario 1 Issue first renewal
		mainApp().open();
		maigManualConversionHelper.acceptPayment(policyNumber,testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"));

		upldatePolicyStatus(renewalOfferEffectiveDate);
		checkPolicyStatus(policyNumber,ProductConstants.PolicyStatus.POLICY_ACTIVE);
		// End PAS-2764 Scenario 1 Issue first renewal

		/* Scenario 2, create and issue second renewal and verify documents list */
		issueSecondRenewal(renewalOfferEffectiveDate);

		openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		/**https://csaaig.atlassian.net/browse/PAS-9157*/
		/**PAS-10256
		 Cannot rate Home SS policy with effective date higher or equal to 2020-02-018*/

		//Generate Bill for the second renewal to verify Home Banking forms
		billGeneration(renewalOfferEffectiveDate.plusYears(1));
		// Shouldn't be after second renewal
		maigManualConversionHelper.pas9816_verifyBillingRenewalPackageAbsence(policyNumber);

		//PAS-9607 Verify that packages are generated with correct transaction code
		maigManualConversionHelper.pas9607_verifyPolicyTransactionCode("STMT", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);

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
		maigManualConversionHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
		maigManualConversionHelper.verifyDocumentTagData(document, testData, isPupPresent);
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
		maigManualConversionHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
		maigManualConversionHelper.verifyDocumentTagData(document, testData, isPupPresent);
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
		maigManualConversionHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
		maigManualConversionHelper.verifyDocumentTagData(document, testData, isPupPresent);
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
		maigManualConversionHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
		maigManualConversionHelper.verifyDocumentTagData(document, testData, isPupPresent);
	}

	/**
	 * Create conversion policy based on Test Data
	 */
	private String createManualConversionRenewalEntry(TestData testData) {

		LocalDateTime renewalOfferEffectiveDate = getTimePoints().getEffectiveDateForTimePoint(
				TimeSetterUtil.getInstance().getCurrentTime(), TimePoints.TimepointsList.RENEW_GENERATE_OFFER).plusDays(5);

		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalOfferEffectiveDate);
		policy.getDefaultView().fillUpTo(testData, BindTab.class, false);
		policy.getDefaultView().getTab(BindTab.class).submitTab();
		return PolicySummaryPage.getPolicyNumber();
	}

	/**
	 * Create conversion policy based on Test Data with linked PUP converted
	 * ToDo: Refactor this after moving InitiateRenewalEntry to policy level
	 */
	private String createPolicyForTDPup() {
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		TestData testDataPup = getManualConversionInitiationTd()
				.adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.PREVIOUS_POLICY_NUMBER
						.getLabel()), legacyPolicyNumber);
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
				openPolicy(policyNumber);
				productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
				break;
			default:
				throw new IllegalArgumentException("Undefined eventName " + eventName.name());
		}
	}

	/**
	 * Utility method that enhances Conversion {@link TestData} with Mortgagee info
	 */
	protected TestData adjustWithMortgageeData(TestData policyTD) {
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
	private String createFormsSpecificManualEntry(TestData testData, LocalDateTime renewalOfferEffectiveDate){
		String membershipFieldMetaKey =
				TestData.makeKeyPath(new ApplicantTab().getMetaKey(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());

		mainApp().open();
		if(getState().equals(Constants.States.NJ)){
			createCustomerIndividual(getCustomerIndividualTD("DataGather", "TestData")
					.adjust(TestData.makeKeyPath("GeneralTab","Date of Birth"),TimeSetterUtil.getInstance().getCurrentTime().minusYears(65).format(DateTimeUtils.MM_DD_YYYY))); // if NJ adjust Date Of Birth
		}
		else{
			createCustomerIndividual();
		}

		// adjust with real policies if PUP )
		if (getPolicyType().equals(PolicyType.PUP)) {
			testData = new PrefillTab().adjustWithRealPolicies(testData, getPrimaryPoliciesForPup());
		}

		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalOfferEffectiveDate);
		// Needed for Membership AHMVCNV form, membership number have to be != active For all products except PUP
		if (!getPolicyType().equals(PolicyType.PUP)) {
			testData.adjust(membershipFieldMetaKey, "4290072030989503");
		}
		policy.getDefaultView().fill(testData);

		return PolicySummaryPage.getPolicyNumber();
	}

	private TestData adjustWithPupData(TestData policyTD) {
		TestData testDataOtherActiveAAAPolicies = getTestSpecificTD("OtherActiveAAAPolicies").resolveLinks();
		String pupOtherActiveAAAPoliciesTabKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
		return policyTD.adjust(pupOtherActiveAAAPoliciesTabKey, testDataOtherActiveAAAPolicies);
	}

	private void openPolicy(String policyNumber) {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
	}

	private List<String> getConversionGeneratedForms() {
		List<String> forms = new ArrayList<>();

		switch (getPolicyType().getShortName()) {
			case "HomeSS":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getHO3NJForms();
				} else {
					forms = maigManualConversionHelper.getHO3OtherStatesForms();
				}
				break;
			case "HomeSS_HO4":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getHO4NJForms();
				} else {
					forms = maigManualConversionHelper.getHO4OtherStatesForms();
				}
				break;
			case "HomeSS_HO6":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getHO6NJForms();
				} else {
					forms = maigManualConversionHelper.getHO6OtherStatesForms();
				}
				break;
			case "HomeSS_DP3":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getDP3NJForms();
				} else {
					forms = maigManualConversionHelper.getDP3OtherStatesForms();
				}
				break;
			case "PUP":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getPupNJForms();
				} else {
					forms = maigManualConversionHelper.getPupOtherStatesForms();
				}
				break;
		}
		return forms;
	}

	private void checkPolicyStatus(String policyNumber, String status) {
		openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(status);
	}

	private void issueSecondRenewal(LocalDateTime renewalOfferEffectiveDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalOfferEffectiveDate.plusYears(1)));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalOfferEffectiveDate.plusYears(1)));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalOfferEffectiveDate.plusYears(1)));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	private void checkPolicyStatus(String policyNumber) {
		openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
	}

	private void upldatePolicyStatus(LocalDateTime renewalOfferEffectiveDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalOfferEffectiveDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
	}

	private void billGeneration(LocalDateTime renewalOfferEffectiveDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalOfferEffectiveDate));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
	}

}
